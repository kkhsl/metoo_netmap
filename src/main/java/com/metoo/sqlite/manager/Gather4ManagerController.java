package com.metoo.sqlite.manager;

import com.alibaba.fastjson.JSONObject;
import com.metoo.sqlite.entity.Arp;
import com.metoo.sqlite.entity.GatherSemaphore;
import com.metoo.sqlite.entity.Subnet;
import com.metoo.sqlite.entity.SubnetIpv6;
import com.metoo.sqlite.gather.factory.gather.thread.Gather;
import com.metoo.sqlite.gather.factory.gather.thread.GatherFactory;
import com.metoo.sqlite.manager.api.ApiService;
import com.metoo.sqlite.manager.utils.ArpUtils;
import com.metoo.sqlite.service.*;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-24 12:50
 */


@Slf4j
@RequestMapping("/admin/gather/4")
@RestController
public class Gather4ManagerController {

    private final ApiService apiService;
    @Autowired
    private IProbeResultService probeResultService;
    @Autowired
    private ISubnetIpv6Service subnetIpv6Service;
    @Autowired
    private IGatewayInfoService gatewayInfoService;
    @Autowired
    private ITerminalService terminalService;
    @Autowired
    private IGatherLogService gatherLogService;
    @Autowired
    private IGatherSemaphoreService gatherSemaphoreService;
    @Autowired
    private IArpService arpService;
    @Value("${AP.URL}")
    private String apUrl;
    @Autowired
    private ArpUtils arpUtils;
    @Autowired
    private ISubnetService subnetService;

    @Autowired
    public Gather4ManagerController(ApiService apiService) {
        this.apiService = apiService;
    }

    private final Lock lock = new ReentrantLock();
//    private InterProcessMutex lock; // 分布式锁


    @GetMapping("/main")
    public Result main() {

        GatherSemaphore gatherSemaphore = null;
        lock.lock();
        try {
            // 查询采集信号量
            gatherSemaphore = this.gatherSemaphoreService.selectObjByOne();
            if (gatherSemaphore.getSemaphore() == 1) {
                return ResponseUtil.badArgument("正在采集...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        try {
            gatherSemaphore.setSemaphore(1);
            this.gatherSemaphoreService.update(gatherSemaphore);
        } catch (Exception e) {
            e.printStackTrace();
            //            return ResponseUtil.error("采集失败");
        }

        try {

                log.info("running 1 .....");
                Thread.sleep(1000);

                log.info("running 2 .....");
                Thread.sleep(1000);

                log.info("running 3 .....");
                Thread.sleep(1000);


    //            String beginTime = DateTools.getCreateTime();
    //
    //            GatherFactory factory = new GatherFactory();
    //            Gather gather = factory.getGather(Global.PY_SUFFIX_IPV4);
    //            gather.executeMethod();
    //
    //            gather = factory.getGather(Global.PY_SUFFIX_IPV6_NEIGHBORS);
    //            gather.executeMethod();
    //
    //            // 端口 ipv4/ipv6
    //            gather = factory.getGather(Global.PY_SUFFIX_ALIVEINT);
    //            gather.executeMethod();
    //
    //            Map details = new HashMap();
    //
    //            // 对ipv4网段梳理
    //            this.subnetService.comb();
    //            // 对梳理过后的结果，进行封装，然后存入采集日志
    //
    //            details.put("subme_ipv4", this.subnet_ipv4());
    //            details.put("subme_ipv6", this.subnet_ipv6());
    //
    //            // Ping
    ////        gather = factory.getGather(Global.PY_SUFFIX_PING);
    ////        gather.executeMethod();
    //
    //            // arp
    //            gather = factory.getGather(Global.ARP);
    //            gather.executeMethod();
    //
    //            // 调用创发接口
    //            String result = null;
    //
    //            ProbeResult probeResult = this.probeResultService.selectObjByOne();
    //            // 分析数据，等待创发回调
    //
    ////        String apiUrl = "http://192.168.5.101:8384/probeNmap/assignmentScanTask";
    ////        String apiUrl = "http://127.0.0.1:8384/probeNmap/assignmentScanTask";
    //            try {
    //                JsonRequest jsonRequest = new JsonRequest();
    //                jsonRequest.setTaskuuid(UUID.randomUUID().toString());
    //
    //                //arp表里的所有ip
    //                List<Arp> arps = this.arpService.selectObjByMap(null);
    //                if(arps.size() > 0){
    //                    // 提取ARP对象列表中的IP地址
    //                    List<String> ipAddresses = arps.stream()
    //                            .map(Arp::getIp) // 提取IP地址
    //                            .filter(ip -> ip != null && !ip.isEmpty()) // 过滤掉空的IP地址
    //                            .collect(Collectors.toList()); // 收集成列表
    //
    //                    // 将IP地址列表组合成逗号分隔的字符串
    //                    String ipAddressString = String.join(",", ipAddresses);
    //                    jsonRequest.setIp(ipAddressString);
    //                }
    //                result = apiService.callThirdPartyApi(apUrl, jsonRequest);
    //                System.out.println(result);
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //
    //            // switch
    //            gather = factory.getGather(Global.PY_SUFFIX_GET_SWITCH);
    //            gather.executeMethod();
    //
    //            // firewall
    //            gather = factory.getGather(Global.PY_SUFFIX_GET_FIREWALL);
    //            gather.executeMethod();
    //            // 出口数据
    //            List<GatewayInfo> gatewayInfoList = this.gatewayInfoService.selectObjByMap(null);
    //            details.put("gatewayInfo", gatewayInfoList);
    //
    //            // 防火墙品牌
    ////        gather = factory.getGather(Global.GATEWAY_OPERATOR);
    ////        gather.executeMethod();
    //
    //            if(result != null){
    //
    //                JSONObject json = JSONObject.parseObject(result);
    //                if(json.getInteger("code") == 0 ){
    //                    // 等待
    //                    Map params = new HashMap();
    //                    while (true){
    //
    //                        try {
    //                            Thread.sleep(5000);
    //                        } catch (InterruptedException e) {
    //                            e.printStackTrace();
    //                        }
    //
    //                        System.out.println("wait...........");
    //
    //                        params.clear();
    //                        params.put("result", probeResult.getResult() + 1);
    //                        List<ProbeResult> obj = this.probeResultService.selectObjByMap(params);
    //                        if(obj.size() > 0){
    //                            break;
    //                        }
    //                    }
    //                }
    //            }
    //
    //
    //            // metoo_scan
    //            gather = factory.getGather(Global.DEVICE_SCAN);
    //            gather.executeMethod();
    //
    //
    //            // terminal
    //            gather = factory.getGather(Global.TERMINAL);
    //            gather.executeMethod();
    //
    //
    //            Map params = new HashMap();
    //            params.clear();
    //            params.put("ipv4IsNotNull", "");
    //            List<Terminal> terminal_ipv4 = this.terminalService.selectObjByMap(params);
    //            params.clear();
    //            params.put("ipv4AndIpv6IsNotNull", "");
    //            List<Terminal> teminal_ipv6 = this.terminalService.selectObjByMap(params);
    //
    //            details.put("ipv4", terminal_ipv4);
    //            details.put("ipv4_ipv6", teminal_ipv6);
    //
    //            String endTime = DateTools.getCreateTime();
    //
    //            GatherLog gatherLog = new GatherLog();
    //            gatherLog.setCreateTime(DateTools.getCreateTime());
    //            gatherLog.setBeginTime(beginTime);
    //            gatherLog.setEndTime(endTime);
    //            gatherLog.setType("自动");
    //            gatherLog.setResult("成功");
    //            gatherLog.setDetails(JSONObject.toJSONString(details));
    //
    //            this.gatherLogService.insert(gatherLog);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                gatherSemaphore.setSemaphore(0);// 采集结束
                this.gatherSemaphoreService.update(gatherSemaphore);
            }

            return ResponseUtil.ok("采集成功");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        finally {
//            lock.unlock();
//        }

    }

    public String subnet_ipv4() {
        // 获取所有子网一级
        List<Subnet> parentList = this.subnetService.selectSubnetByParentId(null);
        if (parentList.size() > 0) {
            for (Subnet subnet : parentList) {
                this.genericSubnet(subnet);
            }
        }
        return JSONObject.toJSONString(parentList);
    }

    public List<Subnet> genericSubnet(Subnet subnet) {
        List<Subnet> subnets = this.subnetService.selectSubnetByParentId(subnet.getId());
        if (subnets.size() > 0) {
            for (Subnet child : subnets) {
                List<Subnet> subnetList = genericSubnet(child);
                if (subnetList.size() > 0) {
                    child.setSubnetList(subnetList);
                }
            }
            subnet.setSubnetList(subnets);
        }
        return subnets;
    }

    public String subnet_ipv6() {
        // 获取所有子网一级
        List<SubnetIpv6> parentList = this.subnetIpv6Service.selectSubnetByParentId(null);
        if (parentList.size() > 0) {
            for (SubnetIpv6 subnet : parentList) {
                this.genericSubnetIpv6(subnet);
            }
        }
        return JSONObject.toJSONString(parentList);
    }

    public List<SubnetIpv6> genericSubnetIpv6(SubnetIpv6 subnetIpv6) {
        List<SubnetIpv6> subnets = this.subnetIpv6Service.selectSubnetByParentId(subnetIpv6.getId());
        if (subnets.size() > 0) {
            for (SubnetIpv6 child : subnets) {
                List<SubnetIpv6> subnetList = genericSubnetIpv6(child);
                if (subnetList.size() > 0) {
                    child.setSubnetList(subnetList);
                }
            }
            subnetIpv6.setSubnetList(subnets);
        }
        return subnets;
    }

    @GetMapping("/port/ipv4")
    public void portIpv4() {
        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather(Global.PY_SUFFIX_ALIVEINT);
        gather.executeMethod();
    }

    @GetMapping("/ipv4")
    public void ipv4() {
        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather(Global.PY_SUFFIX_IPV4);
        gather.executeMethod();
    }

    @GetMapping("/ipv6")
    public void ipv6() {
        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather(Global.PY_SUFFIX_IPV6_NEIGHBORS);
        gather.executeMethod();
    }

    @GetMapping("/ping")
    public void ping() {
        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather(Global.PY_SUFFIX_PING);
        gather.executeMethod();
    }

    @GetMapping("/subnet/ipv6")
    public void subnet() {
        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather(Global.SUBNET_IPV6);
        gather.executeMethod();
    }

    @GetMapping("/arp2")
    public void arp2() {
        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather(Global.ARP);
        gather.executeMethod();
    }

    @GetMapping("/device")
    public void device() {
        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather(Global.PY_SUFFIX_GET_SWITCH);
        gather.executeMethod();
    }

    @GetMapping("/gateway/info")
    public void gatewayInfo() {
        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather(Global.PY_SUFFIX_GET_FIREWALL);
        gather.executeMethod();
    }

    @GetMapping("/gateway/operator")
    public void gatewayInfoOperator() {
        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather(Global.GATEWAY_OPERATOR);
        gather.executeMethod();
    }

    @Transactional
    @GetMapping("/arp")
    public void arp() {

        this.arpService.deleteTable();

        List<Arp> arps = arpUtils.getArp();

        this.arpService.batchInsert(arps);

        System.out.println(arps);
    }

    @GetMapping("pingTest")
    public void pingTest() {
        Set<Subnet> set = new HashSet<>();
        List<Subnet> parentList = this.subnetService.selectSubnetByParentId(null);
        if (parentList.size() > 0) {
            for (Subnet subnet : parentList) {
                this.genericSubnet(subnet, set);
            }
        }
        System.out.println(set);
    }

    public List<Subnet> genericSubnet(Subnet subnet, Set<Subnet> set) {
        List<Subnet> subnets = this.subnetService.selectSubnetByParentId(subnet.getId());
        if (subnets.size() > 0) {
            for (Subnet child : subnets) {
                List<Subnet> subnetList = genericSubnet(child, set);
                if (subnetList.size() > 0) {
                    child.setSubnetList(subnetList);
                } else {
                    set.add(child);
                }
            }
        } else {
            set.add(subnet);
        }
        return subnets;
    }
}
