package com.metoo.sqlite.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.metoo.sqlite.entity.PortIpv4;
import com.metoo.sqlite.entity.Subnet;
import com.metoo.sqlite.mapper.SubnetMapper;
import com.metoo.sqlite.service.IPortIpv4Service;
import com.metoo.sqlite.service.ISubnetService;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.utils.StringUtils;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.utils.net.Ipv4Utils;
import com.metoo.sqlite.utils.net.SubnetUtils;
import com.metoo.sqlite.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@Transactional
public class SubnetServiceImpl implements ISubnetService {

    @Autowired
    private SubnetMapper subnetMapper;
    @Autowired
    private IPortIpv4Service portIpv4Service;
    @Autowired
    private SubnetUtils subnetUtils;

    @Override
    public Subnet selectObjById(Integer id) {
        return this.subnetMapper.selectObjById(id);
    }

    @Override
    public Subnet selectObjByIp(String ip) {
        return this.subnetMapper.selectObjByIp(ip);
    }

    @Override
    public Subnet selectObjByIpAndMask(String ip, Integer mask) {
        return this.subnetMapper.selectObjByIpAndMask(ip, mask);
    }

    public static void main(String[] args) {
        List<Subnet> ipList = new ArrayList<>();
        ipList.add(new Subnet("192.168.1.10"));
        ipList.add(new Subnet("192.168.2.11"));
        ipList.add(new Subnet("10.0.0.1"));
        ipList.add(new Subnet("192.168.2.21"));
        ipList.add(new Subnet("192.168.1.11"));
        ipList.add(new Subnet("172.16.0.1"));

        // 使用自定义比较器按照 IP 地址排序
        Collections.sort(ipList, Comparator.comparing(Subnet::getIp));

        // 输出排序后的 IP 地址列表
        for (Subnet ip : ipList) {
            System.out.println(ip);
        }
    }

    @Override
    public List<Subnet> selectSubnetByParentId(Integer id) {
        List<Subnet> subnets = this.subnetMapper.selectSubnetByParentId(id);
//        Collections.sort(subnets, Comparator.comparing(IPSortExample::ipToLong));
        //IP地址字符串的格式：IP地址的字符串格式是规范化的，每个部分（如 192、168、1、10）都是数字，并且通过点号分隔。
            // 因此，它们可以安全地用作字符串来比较，而不需要将它们转换为整数形式
        Collections.sort(subnets, Comparator.comparing(Subnet::getIp));

        return subnets;
    }


    @Override
    public List<Subnet> selectSubnetByParentIp(Integer ip) {
        return this.subnetMapper.selectSubnetByParentIp(ip);
    }

    @Override
    public List<Subnet> selectObjByMap(Map params) {
        return this.subnetMapper.selectObjByMap(params);
    }

    @Override
    public int save(Subnet subnet) {
        try {
            subnet.setCreateTime(DateTools.getCreateTime());
            return this.subnetMapper.save(subnet);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Result update(Subnet instance) {
        try {
            if(this.selectObjById(instance.getId()) != null) {
                int i = this.subnetMapper.update(instance);
                if(i >= 0){
                    return ResponseUtil.ok();
                }
            }
            return ResponseUtil.dataNotFound();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.error();
        }
    }

    @Override
    public int delete(Integer id) {
        try {
            return this.subnetMapper.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int deleteTable() {
        try {
            return this.subnetMapper.deleteTable();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Result comb() {

        Long time = System.currentTimeMillis();
        log.info("Ipv4 subnet start......");
        this.deleteTable();

        Map params = new HashMap();
        params.put("ipIsNotNull", "is not null");
        params.put("NotIp", "127.0.0.1");
        List<PortIpv4> ports = this.portIpv4Service.selectObjByMap(params);
        Map<String, List<Object>> map = this.ipAddressCombingByDB(ports);
        for (String key : map.keySet()) {
            int index = key.indexOf("/");
            String firstIp = key.substring(0, index);
            int firstMask = Integer.parseInt(key.substring(index + 1));
            // 插入本地数据库 and 同步ipam
            // 插如一级ip网段
            Integer firstSubnetId = null;
            Subnet firstSubnet = this.selectObjByIpAndMask(firstIp, firstMask);
            if (firstSubnet != null) {
                firstSubnetId = firstSubnet.getId();
            }
            if (firstSubnet == null) {
                Subnet subnet = new Subnet();
                subnet.setIp(Ipv4Utils.ipConvertDec(firstIp));
                subnet.setIp(firstIp);
                subnet.setMask(firstMask);
                this.save(subnet);
                firstSubnetId = subnet.getId();
            }
            // 获取二级网段
            JSONArray array = JSONArray.parseArray(JSON.toJSONString(map.get(key)));
            for (Object obj : array) {
                if (obj instanceof String) {
                    String second = ((String) obj).trim();
                    int sequence = second.indexOf("/");
                    String ip = second.substring(0, sequence);
                    int secondMask = Integer.parseInt(second.substring(sequence + 1));
                    Subnet secondSubnet = this.selectObjByIpAndMask(ip, secondMask);
                    if (secondSubnet == null) {
                        Subnet subnet = new Subnet();
                        subnet.setIp(Ipv4Utils.ipConvertDec(ip));
                        subnet.setIp(ip);
                        subnet.setMask(secondMask);
                        subnet.setParentIp(Ipv4Utils.ipConvertDec(firstIp));
                        subnet.setParentIp(firstIp);
                        subnet.setParentId(firstSubnetId);
                        this.save(subnet);
                    }
                }
                if (obj instanceof JSONObject) {
                    JSONObject object = (JSONObject) obj;
                    for (String okey : object.keySet()) {
                        String second = okey.trim();
                        int sequence = second.indexOf("/");
                        String secondIp = second.substring(0, sequence);
                        int secondMask = Integer.parseInt(second.substring(sequence + 1));
                        Integer secondSubnetId = null;
                        Subnet secondSubnet = this.selectObjByIpAndMask(secondIp, secondMask);
                        if (secondSubnet != null) {
                            secondSubnetId = secondSubnet.getId();
                        }
                        if (secondSubnet == null) {
                            Subnet subnet = new Subnet();
                            subnet.setIp(Ipv4Utils.ipConvertDec(secondIp));
                            subnet.setIp(secondIp);
                            subnet.setMask(secondMask);
//                            subnet.setParentIp(Ipv4Utils.ipConvertDec(firstIp));
                            subnet.setParentIp(firstIp);
                            subnet.setParentId(firstSubnetId);
                            this.save(subnet);
                            secondSubnetId = subnet.getId();
                        }
                        JSONArray thirdArray = JSONArray.parseArray(object.get(okey).toString());
                        for (Object thirdKey : thirdArray) {
                            if (obj instanceof JSONObject) {
                                String third = ((String) thirdKey).trim();
                                int thirdSequence = third.indexOf("/");
                                String thirdIp = third.substring(0, thirdSequence);
                                int thirdMask = Integer.parseInt(third.substring(thirdSequence + 1));
                                Subnet thirdSubnet = this.selectObjByIpAndMask(thirdIp, thirdMask);
                                if (thirdSubnet == null) {
                                    Subnet subnet = new Subnet();
                                    subnet.setIp(Ipv4Utils.ipConvertDec(thirdIp));
                                    subnet.setIp(thirdIp);
                                    subnet.setMask(thirdMask);
                                    subnet.setParentIp(Ipv4Utils.ipConvertDec(secondIp));
                                    subnet.setParentIp(secondIp);
                                    subnet.setParentId(secondSubnetId);
                                    this.save(subnet);
                                }
                            }
                        }
                    }
                }
            }
        }

//        log.info("ipv6 port End......" + (System.currentTimeMillis() - time));
        log.info("Ipv4 subnet End......" + (System.currentTimeMillis() - time));

        return ResponseUtil.ok();
    }

    public Map<String, List<Object>> ipAddressCombingByDB(List<PortIpv4> portIpv4List) {
        if(portIpv4List.size() == 0){
            return new HashMap<>();
        }
        Map<String, Integer> map = new HashMap();
        List<Integer> masks = new ArrayList();
        for (PortIpv4 port : portIpv4List){
            if(StringUtils.isNonEmptyAndTrimmed(port.getIp()) && StringUtils.isNonEmptyAndTrimmed(port.getMask())){
                String ip = port.getIp();
                String mask = port.getMask();
                Integer maskBit = Ipv4Utils.getMaskBitByMask(mask);
                Map networkMap = Ipv4Utils.getNetworkIp(ip, mask);
                map.put(networkMap.get("network").toString(), maskBit);
                masks.add(maskBit);
            }
        }
        if(masks.size() <= 0){
            return new HashMap<>();
        }
        // 第二步：提取最短掩码，生成上级网段
        HashSet set = new HashSet(masks);
        masks.clear();
        masks.addAll(set);
        Collections.sort(masks);
        Integer firstMask = masks.get(0);// 最短掩码
        Map<String, Integer> firstMap = new HashMap();
        Map<String, Integer> otherMap = new HashMap();
        for (Map.Entry<String, Integer> entry : map.entrySet()){
            if(entry.getValue().equals(firstMask)){
                firstMap.put(entry.getKey(), entry.getValue());
            }else{
                otherMap.put(entry.getKey(), entry.getValue());
            }
        }
        // 提取
        Map<String, List<Object>> parentMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : firstMap.entrySet()) {
            Integer maskBit = entry.getValue();
            String ip = entry.getKey();
            Integer parentMask = null;
            if (maskBit > 24) {
                parentMask = 24;
            } else if (24 >= maskBit && maskBit > 16) {
                parentMask = 16;
            } else if (16 >= maskBit && maskBit > 8) {
                parentMask = 8;
            }else if(maskBit <= 8){
                parentMask = maskBit;
            }
            String segment = this.getParentSegment(ip, parentMask);// 生成网段
            String parentSegment = segment + "/" + parentMask;
            if (!parentMap.keySet().contains(parentSegment) && parentMap.get(parentSegment) == null) {
                List<Object> childList = new ArrayList<>();
                childList.add(ip + "/" + maskBit);
                parentMap.put(parentSegment, childList);
            } else {
                List<Object> childList = parentMap.get(parentSegment);
                if(childList != null){
                    childList.add(ip + "/" + maskBit);
                }
            }
        }
        // 遍历
        Map parentSegmentMap = new HashMap();
        for (Map.Entry entry : parentMap.entrySet()) {
            String parentSegment = (String) entry.getKey();
            Integer parentMask = null;
            parentMask = Integer.parseInt(parentSegment.substring(parentSegment.indexOf("/") + 1));
            int parentIndex = 0;
            String parentIpPartial = null;
            if (parentMask == 24) {
                parentIndex =  parentSegment.indexOf(".");
                parentIndex =  parentSegment.indexOf(".", parentIndex + 1);
                parentIndex =  parentSegment.indexOf(".", parentIndex + 1);
            } else if (parentMask == 16) {
                parentIndex =  parentSegment.indexOf(".");
                parentIndex =  parentSegment.indexOf(".", parentIndex + 1);
            } else if (parentMask == 8) {
                parentIndex =  parentSegment.indexOf(".");
            }
            parentIpPartial = parentSegment.substring(0, parentIndex);
            parentSegmentMap.put(parentIpPartial, parentSegment);
        }

        // 判断是否属于第一级
        for (Map.Entry<String, Integer> entry : otherMap.entrySet()) {
            Integer mask = entry.getValue();
            String ip = entry.getKey();
            int index = 0;
            if (mask > 24) {
                index =  ip.indexOf(".");
                index =  ip.indexOf(".", index + 1);
                index =  ip.indexOf(".", index + 1);
            } else if (24 >= mask && mask > 16) {
                index =  ip.indexOf(".");
                index =  ip.indexOf(".", index + 1);
            } else if (16 >= mask && mask > 8) {
                index =  ip.indexOf(".");
            }
            String ipParentIpPartial = ip.substring(0, index);
            if(parentSegmentMap.get(ipParentIpPartial) != null){
                List<Object> list = parentMap.get(parentSegmentMap.get(ipParentIpPartial));
                if(list == null){
                    list.add(ip + "/" + mask);
                }

            }else{
                Integer parentMask = null;
                if (mask > 24) {
                    parentMask = 24;
                } else if (24 >= mask && mask > 16) {
                    parentMask = 16;
                } else if (16 >= mask && mask > 8) {
                    parentMask = 8;
                }
                String parentIp = this.getParentIp(ip, parentMask);
                String parentSegment = parentIp + "/" + parentMask;
                List<Object> list = new ArrayList<>();
                list.add(ip + "/" + mask);
                parentMap.put(parentSegment, list);
                parentSegmentMap.put(ipParentIpPartial, parentSegment);
            }
        }
        // 遍历二级ip，生成上级Ip
        if(parentMap.size() > 1){
            Map<String, List<Object>> parent = this.getShortMask(parentMap);
            if(parent != null && parent.size() > 0){
                return parent;
            }
        }else{}
        return parentMap;
    }

    public String getParentSegment(String ip, Integer bitmask){
        String segment = "";
        if (24 == bitmask) {
            String mask = Ipv4Utils.bitMaskConvertMask(bitmask);
            segment = Ipv4Utils.getNetwork(ip, mask);
        } else if (16  == bitmask) {
            String mask = Ipv4Utils.bitMaskConvertMask(bitmask);
            segment = Ipv4Utils.getNetwork(ip, mask);
        }else if (8  == bitmask) {
            String mask = Ipv4Utils.bitMaskConvertMask(bitmask);
            segment = Ipv4Utils.getNetwork(ip, mask);
        }
        return segment;
    }

    /**
     *
     * @param ip
     * @param bitmask
     * @return
     */
    public String getParentIp(String ip, Integer bitmask){
        String segment = "";
        if (24 == bitmask) {
            String mask = Ipv4Utils.bitMaskConvertMask(bitmask);
            segment = Ipv4Utils.getNetwork(ip, mask);
        } else if (16  == bitmask) {
            String mask = Ipv4Utils.bitMaskConvertMask(bitmask);
            segment = Ipv4Utils.getNetwork(ip, mask);
        }else if (8  == bitmask) {
            String mask = Ipv4Utils.bitMaskConvertMask(bitmask);
            segment = Ipv4Utils.getNetwork(ip, mask);
        }
        return segment;
    }

    public Map<String, List<Object>> getShortMask(Map<String, List<Object>> parentMap){
//        String parentIp = null;
        Integer shorMask = 0;
        for (Map.Entry<String, List<Object>> entry : parentMap.entrySet()){
            String ip = entry.getKey();
            int index = ip.indexOf("/");
            int mask = Integer.parseInt(ip.substring(index + 1));
            if(mask > shorMask || shorMask == 0){
                shorMask = mask;
            }
        }
        // 遍历parentMap 获取掩码位等于parentmask网段集合

        Map<String, List<Object>> map = new HashMap<>();

        for (Map.Entry<String, List<Object>> entry : parentMap.entrySet()){
            String ipMask = entry.getKey();
            int index = ipMask.indexOf("/");
            int mask = Integer.parseInt(ipMask.substring(index + 1));
            // 判断当前mask是否等于最短mask
            if(mask != shorMask){
                map.put(ipMask, parentMap.get(ipMask));
            }
        }
        for (Map.Entry<String, List<Object>> entry : parentMap.entrySet()){
            String ipMask = entry.getKey();
            int index = ipMask.indexOf("/");
            int mask = Integer.parseInt(ipMask.substring(index + 1));
            String ip = ipMask.substring(0, index);
            Integer parentMask = null;
            // 判断当前mask是否等于最短mask
            if(mask == shorMask){
                // 同为最低等级mask/创建上级
                if (mask > 24) {
                    parentMask = 24;
                } else if (24 >= mask && mask > 16) {
                    parentMask = 16;
                } else if (16 >= mask && mask > 8) {
                    parentMask = 8;
                }
                // 生成上级网段
                String parentIp = this.getParentIp(ip, parentMask);
                parentIp = parentIp + "/" + parentMask;
                // 比较是否已经存在
                if(map.get(parentIp) != null){

                    List<Object> list = map.get(parentIp);

                    List<Object> childs = parentMap.get(ipMask);

                    Map child = new HashMap();
                    child.put(ipMask, childs);

                    list.add(child);

                    map.put(parentIp, list);

                }else{
                    List<Object> list =  new ArrayList<>();

                    List<Object> childs = parentMap.get(ipMask);

                    Map child = new HashMap();
                    child.put(ipMask, childs);

                    list.add(child);

                    map.put(parentIp, list);
                }
            }
        }
        return map;
    }
}
