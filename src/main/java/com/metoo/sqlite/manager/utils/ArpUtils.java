package com.metoo.sqlite.manager.utils;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.Arp;
import com.metoo.sqlite.entity.Ipv4;
import com.metoo.sqlite.entity.Ipv6;
import com.metoo.sqlite.entity.MacVendor;
import com.metoo.sqlite.service.IMacVendorService;
import com.metoo.sqlite.service.Ipv4Service;
import com.metoo.sqlite.service.Ipv6Service;
import com.metoo.sqlite.utils.date.DateTools;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-25 20:11
 */
@Component
public class ArpUtils {

    @Autowired
    private IMacVendorService macVendorService;
    @Autowired
    private Ipv4Service ipv4Service;
    @Autowired
    private Ipv6Service ipv6Service;


    /**
     *
     * 优化嵌套
     * 优化方法，使用map存储一个集合的mac属性和对象本身，从而将查找时间复杂度降低到 O(1)
     * O(1) 时间复杂度：常数时间复杂度是算法效率的最高级别，表示不论输入数据的大小，执行该操作所需的时间都是恒定的。这通常是因为算法只对数据进行一次访问或处理。
     *
     * 示例：在上面的优化示例中，使用了一个 Map 数据结构来存储 ObjectB 对象，以 mac 作为键。通过这种方式，查找特定 mac 对应的 ObjectB 对象的时间复杂度是 O(1)，因为通过键直接访问值的操作是常数时间复杂度的。
     *
     * 比较：相比之下，嵌套循环的方法可能会导致时间复杂度达到 O(n*m)，其中 n 和 m 分别是集合 listA 和 listB 的大小，因为每个元素都需要与另一个集合中的每个元素进行比较。
     * @return
     */
    public List<Arp> getArp() {
        List<Ipv4> ipv4List = this.ipv4Service.selectObjByMap(null);
        List<Ipv6> ipv6List = this.ipv6Service.selectObjByMap(null);


        // 使用Map来存储相同mac的数据，合并IPv4数据
        Map<String, List<Ipv4>> macToIpMap = new HashMap<>();

        // 处理IPv4数据
        for (Ipv4 ipv4 : ipv4List) {
            if (!macToIpMap.containsKey(ipv4.getMac())) {
                macToIpMap.put(ipv4.getMac(), new ArrayList<>());
            }
            macToIpMap.get(ipv4.getMac()).add(ipv4);
        }

        // 输出合并后的IPv4集合
        List<Ipv4> mergedIPv4Objects = new ArrayList<>();

        macToIpMap.forEach((mac, ipv4ObjectsList) -> {
            // Combine IPs into an array
            List<String> ips = new ArrayList<>();
            for (Ipv4 obj : ipv4ObjectsList) {
                ips.add(obj.getIp());
            }

            Ipv4 ele = ipv4ObjectsList.get(0);
            // Create merged object with all properties
            Ipv4 mergedObject = new Ipv4(String.join(",", ips), mac, ele.getPort(),
                    ele.getVlan(), ele.getAging(), ele.getType(), ele.getDeviceUuid());
            mergedIPv4Objects.add(mergedObject);
        });

        // 打印输出结果
        mergedIPv4Objects.forEach(System.out::println);

        List<Arp> arps = new ArrayList<>();
        for (Ipv4 mergedIPv4Object : mergedIPv4Objects) {
            Arp arp2 = new Arp(mergedIPv4Object.getIp(), mergedIPv4Object.getMac(), mergedIPv4Object.getPort(), mergedIPv4Object.getDeviceUuid(), null);
            arps.add(arp2);
        }

        List<Arp> arps2 = new ArrayList<>();
        if(ipv6List.size() > 0){
            List<Ipv6> duplicatesIpv6List = getArpIpv6(ipv6List);
            if(duplicatesIpv6List.size() > 0 && arps.size() > 0){
                for (Ipv6 ipv6 : duplicatesIpv6List) {
                    boolean flag = true;
                    for (Arp arp : arps) {
                        if(arp.getMac().equals(ipv6.getIpv6_mac())){
                            arp.setIpv6(ipv6.getIpv6_address());
                            flag = false;
                            break;
                        }
                    }// 优化方法，使用map存储一个集合的mac属性和对象本身，从而将查找时间复杂度降低到 O(1)
                    if(flag){
                        Arp arp2 = new Arp(ipv6.getIpv6_address(), ipv6.getIpv6_mac(), ipv6.getPort(), ipv6.getDeviceUuid());
                        arps2.add(arp2);
                    }
                }
            }
        }

        arps.addAll(arps2);

        String time = DateTools.getCreateTime();
        if (arps.size() > 0){
            Map params = new HashMap();
            for (Arp arp : arps) {
                arp.setCreateTime(time);
                if(arp.getMac() != null && !"".equals(arp.getMac())){
                    // 方法二：使用正则表达式匹配并提取前三段
                   if(isValidMACAddress(arp.getMac())){
                       params.clear();
                       params.put("mac", MacUtils.getMac(arp.getMac()));
                       List<MacVendor> macVendorList = this.macVendorService.selectObjByMap(params);
                       if(macVendorList.size() > 0){
                           MacVendor macVendor = macVendorList.get(0);
                           arp.setMacVendor(macVendor.getVendor());
                       }
                   }
                }
            }
        }
        return arps;
    }


    public List<Arp> getArpVersion(){
        Map params = new HashMap();

        List<Arp> arpList = new ArrayList<>();
        List<Arp> arpList2 = new ArrayList<>();

        List<Ipv4> ipv4List = this.ipv4Service.selectObjByMap(null);
        List<Ipv6> ipv6List = this.ipv6Service.selectObjByMap(null);
        // 根据mac地址去重
        // 根据 mac 去重
        ipv6List = ipv6List.stream()
                .collect(Collectors.toMap(
                        Ipv6::getIpv6_mac,  // 使用 mac 作为 key
                        ipv6 -> ipv6,  // 保留 Ipv6 对象
                        (existing, replacement) -> existing))  // 若出现相同 mac，保留第一次出现的对象
                .values()  // 获取去重后的值
                .stream()
                .collect(Collectors.toList());  // 转换回 List

        if(ipv4List.size() > 0){
            for (Ipv4 ipv4 : ipv4List) {

                Arp arpIpv4 = new Arp(ipv4.getIp(), ipv4.getMac(), ipv4.getPort(), ipv4.getDeviceUuid(), null);

                if(StringUtil.isNotEmpty(arpIpv4.getIp()) && StringUtil.isNotEmpty(arpIpv4.getMac())){
                    params.clear();
                    params.put("mac", MacUtils.getMac(ipv4.getMac()));
                    List<MacVendor> macVendorList = this.macVendorService.selectObjByMap(params);
                    if(macVendorList.size() > 0){
                        MacVendor macVendor = macVendorList.get(0);
                        arpIpv4.setMacVendor(macVendor.getVendor());
                    }
                }

                arpList.add(arpIpv4);
            }

            for (Arp arp : arpList) {
                for (Ipv6 ipv6 : ipv6List) {
                    if(StringUtil.isNotEmpty(ipv6.getIpv6_address()) && arp.getMac() != null
                            && arp.getMac().toLowerCase().equals(ipv6.getIpv6_mac().toLowerCase())){
                        if(arp.getPort() != null && ipv6.getPort() != null
                                && arp.getPort().toLowerCase().equals(ipv6.getPort().toLowerCase()) ){
                            arp.setIpv6(ipv6.getIpv6_address());
                        }
                        break;
                    }
                }
                arpList2.add(arp);
            }

            for (Ipv6 ipv6 : ipv6List) {
                boolean flag = false;
                for (Arp arpIpv4 : arpList) {
                    if(arpIpv4.getMac() != null && arpIpv4.getMac().equals(ipv6.getIpv6_mac())){
                        flag = true;
                    }
                }
                if(!flag){
                    Arp arpIpv6 = new Arp(ipv6.getIpv6_address(), ipv6.getIpv6_mac(), ipv6.getPort(), ipv6.getDeviceUuid());
                    if(StringUtil.isNotEmpty(arpIpv6.getMac())) {
                        params.clear();
                        params.put("mac", MacUtils.getMac(ipv6.getIpv6_mac()));
                        List<MacVendor> macVendorList = this.macVendorService.selectObjByMap(params);
                        if(macVendorList.size() > 0){
                            MacVendor macVendor = macVendorList.get(0);
                            arpIpv6.setMacVendor(macVendor.getVendor());
                        }
                    }

                    arpList2.add(arpIpv6);
                }
            }
        }else{
            for (Ipv6 ipv6 : ipv6List) {
                Arp arpIpv6 = new Arp(ipv6.getIpv6_address(), ipv6.getIpv6_mac(), ipv6.getPort(), ipv6.getDeviceUuid());
                if(StringUtil.isNotEmpty(arpIpv6.getMac())) {
                    params.clear();
                    params.put("mac", MacUtils.getMac(ipv6.getIpv6_mac()));
                    List<MacVendor> macVendorList = this.macVendorService.selectObjByMap(params);
                    if(macVendorList.size() > 0){
                        MacVendor macVendor = macVendorList.get(0);
                        arpIpv6.setMacVendor(macVendor.getVendor());
                    }
                }

                arpList2.add(arpIpv6);
            }
        }
        return arpList2;

    }

    public List<Arp> getArpVersionPanabit(){
        String time = DateTools.getCreateTime();

        Map params = new HashMap();

        List<Arp> arpList = new ArrayList<>();
        List<Arp> arpList2 = new ArrayList<>();

        List<Ipv4> ipv4List = this.ipv4Service.selectObjByMap(null);
        List<Ipv6> ipv6List = this.ipv6Service.selectObjByMap(null);

        if(ipv4List.size() > 0){
            for (Ipv4 ipv4 : ipv4List) {

                Arp arpIpv4 = new Arp(ipv4.getIp(), ipv4.getMac(), ipv4.getPort(), ipv4.getDeviceUuid(), null);

                params.clear();
                params.put("mac", MacUtils.getMac(ipv4.getMac()));
                List<MacVendor> macVendorList = this.macVendorService.selectObjByMap(params);
                if(macVendorList.size() > 0){
                    MacVendor macVendor = macVendorList.get(0);
                    arpIpv4.setMacVendor(macVendor.getVendor());
                }
                arpList.add(arpIpv4);
            }

            for (Arp arp : arpList) {
                for (Ipv6 ipv6 : ipv6List) {
                    if(arp.getMac().toLowerCase().equals(ipv6.getIpv6_mac().toLowerCase())){
                        arp.setIpv6(ipv6.getIpv6_address());
                        break;
                    }
                }
                arpList2.add(arp);
            }

            for (Ipv6 ipv6 : ipv6List) {
                boolean flag = false;
                for (Arp arpIpv4 : arpList) {
                    if(arpIpv4.getMac().equals(ipv6.getIpv6_mac())){
                        flag = true;
                    }
                }
                if(!flag){
                    Arp arpIpv6 = new Arp(ipv6.getIpv6_address(), ipv6.getIpv6_mac(), ipv6.getPort(), ipv6.getDeviceUuid());
                    params.clear();
                    params.put("mac", MacUtils.getMac(ipv6.getIpv6_mac()));
                    List<MacVendor> macVendorList = this.macVendorService.selectObjByMap(params);
                    if(macVendorList.size() > 0){
                        MacVendor macVendor = macVendorList.get(0);
                        arpIpv6.setMacVendor(macVendor.getVendor());
                    }
                    arpList2.add(arpIpv6);
                }
            }
        }else{
            for (Ipv6 ipv6 : ipv6List) {
                Arp arpIpv6 = new Arp(ipv6.getIpv6_address(), ipv6.getIpv6_mac(), ipv6.getPort(), ipv6.getDeviceUuid());
                params.clear();
                params.put("mac", MacUtils.getMac(ipv6.getIpv6_mac()));
                List<MacVendor> macVendorList = this.macVendorService.selectObjByMap(params);
                if(macVendorList.size() > 0){
                    MacVendor macVendor = macVendorList.get(0);
                    arpIpv6.setMacVendor(macVendor.getVendor());
                }
                arpList2.add(arpIpv6);
            }
        }
        return arpList2;

    }

    private static final String MAC_ADDRESS_PATTERN =
            "([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2}|[0-9A-Fa-f]{12}";

    private static final Pattern MAC_ADDRESS_REGEX = Pattern.compile(MAC_ADDRESS_PATTERN);

    /**
     * 判断字符串是否为有效的 MAC 地址
     *
     * @param macAddress 要验证的字符串
     * @return 如果是有效的 MAC 地址，则返回 true；否则返回 false
     */
    public static boolean isValidMACAddress(String macAddress) {
        if (macAddress == null) {
            return false;
        }
        return MAC_ADDRESS_REGEX.matcher(macAddress).matches();
    }


    public static void main(String[] args) {
        System.out.println(containsThreeColons("as:ss:ss:ss"));
    }
    public static boolean containsThreeColons(String str) {
        int firstIndex = str.indexOf(':');
        if (firstIndex == -1) return false;

        int secondIndex = str.indexOf(':', firstIndex + 1);
        if (secondIndex == -1) return false;

        int thirdIndex = str.indexOf(':', secondIndex + 1);
        return thirdIndex != -1;
    }

    // 合并
    @Test
    public void test(){
        List<Ipv4> objects = new ArrayList<>();
        objects.add(new Ipv4("192.168.1.1", "00:11:22:33:44:55", "11"));
        objects.add(new Ipv4("192.168.1.2", "00:11:22:33:44:55", "22"));
        objects.add(new Ipv4("192.168.1.3", "00:11:22:33:44:56", "33"));
        objects.add(new Ipv4("192.168.1.4", "00:11:22:33:44:56", "44"));


        // 使用Map来存储相同mac的数据
        Map<String, List<String>> macToIpMap = new HashMap<>();

        // 遍历对象列表，将相同mac的ip合并到数组中
        objects.forEach(obj -> {
            if (!macToIpMap.containsKey(obj.getMac())) {
                macToIpMap.put(obj.getMac(), new ArrayList<>());
            }
            macToIpMap.get(obj.getMac()).add(obj.getIp());
        });

        // 打印结果
        macToIpMap.forEach((mac, ips) -> {
            System.out.println("MAC: " + mac + ", IPs: " + ips);
        });
    }


    @Test
    public void test2(){
        List<Object> objects = new ArrayList<>();
        objects.add(new Ipv4("192.168.1.1", "00:11:22:33:44:55", "11"));
        objects.add(new Ipv4("192.168.1.2", "00:11:22:33:44:55", "22"));
        objects.add(new Ipv4("192.168.1.3", "00:11:22:33:44:56", "33"));
        objects.add(new Ipv4("192.168.1.4", "00:11:22:33:44:56", "44"));

        List<Object> objects6 = new ArrayList<>();
        objects6.add(new Ipv6("192.168.1.5", "00:11:22:33:44:55", "11"));
        objects6.add(new Ipv6("192.168.1.6", "00:11:22:33:44:55", "22"));
        objects6.add(new Ipv6("192.168.1.7", "00:11:22:33:44:56", "33"));
        objects6.add(new Ipv6("192.168.1.8", "00:11:22:33:44:56", "44"));

        objects6.addAll(objects);

    }
//
//
//    public static void main(String[] args) {
//        // 假设有一组IPv4数据和一组IPv6数据
//        List<Ipv4> ipv4Objects = new ArrayList<>();
//        ipv4Objects.add(new Ipv4("192.168.1.1", "00:11:22:33:44:55", "8080"));
//        ipv4Objects.add(new Ipv4("192.168.1.2", "00:11:22:33:44:56", "8081"));
//
//        List<Ipv6> ipv6Objects = new ArrayList<>();
//        ipv6Objects.add(new Ipv6("FE80::1", "00:11:22:33:44:55", "8080"));
//        ipv6Objects.add(new Ipv6("FE80::2", "00:11:22:33:44:56", "8081"));
//
//        // 使用Map来存储相同mac的数据，分别存储IPv4和IPv6
//        Map<String, List<String>> ipv4Map = new HashMap<>();
//        Map<String, List<String>> ipv6Map = new HashMap<>();
//
//        // 处理IPv4数据
//        for (Ipv4 ipv4 : ipv4Objects) {
//            if (!ipv4Map.containsKey(ipv4.getMac())) {
//                ipv4Map.put(ipv4.getMac(), new ArrayList<>());
//            }
//            ipv4Map.get(ipv4.getMac()).add(ipv4.getIp());
//        }
//
//        // 处理IPv6数据
//        for (Ipv6 ipv6 : ipv6Objects) {
//            if (!ipv6Map.containsKey(ipv6.getIpv6_mac())) {
//                ipv6Map.put(ipv6.getIpv6_mac(), new ArrayList<>());
//            }
//            ipv6Map.get(ipv6.getIpv6_mac()).add(ipv6.getIpv6_address());
//        }
//
//        // 打印结果
//        System.out.println("IPv4 数据：");
//        ipv4Map.forEach((mac, ips) -> {
//            System.out.println("MAC: " + mac + ", IPs: " + ips);
//        });
//
//        System.out.println("\nIPv6 数据：");
//        ipv6Map.forEach((mac, ips) -> {
//            System.out.println("MAC: " + mac + ", IPs: " + ips);
//        });
//    }

//    public static void main(String[] args) {
//        // 假设有一组IPv4数据
//        List<Ipv4> ipv4Objects = new ArrayList<>();
//        ipv4Objects.add(new Ipv4("192.168.1.1", "00:11:22:33:44:55", "8080"));
//        ipv4Objects.add(new Ipv4("192.168.1.2", "00:11:22:33:44:55", "8081"));
//        ipv4Objects.add(new Ipv4("192.168.1.3", "00:11:22:33:44:56", "8082"));
//        ipv4Objects.add(new Ipv4("192.168.1.4", "00:11:22:33:44:56", "8083"));
//
//
//        // 使用Map来存储相同mac的数据，合并IPv4数据
//        Map<String, List<Ipv4>> macToIpMap = new HashMap<>();
//
//        // 处理IPv4数据
//        for (Ipv4 ipv4 : ipv4Objects) {
//            if (!macToIpMap.containsKey(ipv4.getMac())) {
//                macToIpMap.put(ipv4.getMac(), new ArrayList<>());
//            }
//            macToIpMap.get(ipv4.getMac()).add(ipv4);
//        }
//
//        // 输出合并后的IPv4集合
//        List<Ipv4> mergedIPv4Objects = new ArrayList<>();
//
//        macToIpMap.forEach((mac, ipv4ObjectsList) -> {
//            // Combine IPs into an array
//            List<String> ips = new ArrayList<>();
//            for (Ipv4 obj : ipv4ObjectsList) {
//                ips.add(obj.getIp());
//            }
//
//            // Create merged object with all properties
//            Ipv4 mergedObject = new Ipv4(String.join(",", ips), mac,
//                    ipv4ObjectsList.get(0).getPort(), ipv4ObjectsList.get(0).getAging(),
//                    ipv4ObjectsList.get(0).getDeviceUuid());
//            mergedIPv4Objects.add(mergedObject);
//        });
//
//        // 打印输出结果
//        mergedIPv4Objects.forEach(System.out::println);
//    }


    public List<Ipv6> getArpIpv6(List<Ipv6> ipv6List) {
        // 假设有一组IPv4数据

        // 使用Map来存储相同mac的数据，合并IPv4数据
        Map<String, List<Ipv6>> macToIpMap = new HashMap<>();

        // 处理IPv4数据
        for (Ipv6 ipv6 : ipv6List) {
            if (!macToIpMap.containsKey(ipv6.getIpv6_mac())) {
                macToIpMap.put(ipv6.getIpv6_mac(), new ArrayList<>());
            }
            macToIpMap.get(ipv6.getIpv6_mac()).add(ipv6);
        }

        // 输出合并后的IPv4集合
        List<Ipv6> mergedIPv6Objects = new ArrayList<>();

        macToIpMap.forEach((mac, ipv6ObjectsList) -> {
            // Combine IPs into an array
            List<String> ips = new ArrayList<>();
            for (Ipv6 obj : ipv6ObjectsList) {
                ips.add(obj.getIpv6_address());
            }
            Ipv6 ele = ipv6ObjectsList.get(0);
            // Create merged object with all properties
            Ipv6 mergedObject = new Ipv6(String.join(",", ips), mac, ele.getPort(),
                     ele.getVid(), ele.getType(), ele.getAge(), ele.getVpninstance(), ele.getDeviceUuid());
            mergedIPv6Objects.add(mergedObject);
        });

        // 打印输出结果
        mergedIPv6Objects.forEach(System.out::println);

        return mergedIPv6Objects;
    }

    public void ipv4(List<Ipv4> ipv4List){
        Map<String, List<String>> ipv4Map = new HashMap<>();

        // 处理IPv4数据
        for (Ipv4 ipv4 : ipv4List) {
            if (!ipv4Map.containsKey(ipv4.getMac())) {
                ipv4Map.put(ipv4.getMac(), new ArrayList<>());
            }
            ipv4Map.get(ipv4.getMac()).add(ipv4.getIp());
        }

    }


    public void arp(List<Ipv4> ipv4List){
        Map<String, List<String>> ipv4Map = new HashMap<>();

        // 处理IPv4数据
        for (Ipv4 ipv4 : ipv4List) {
            if (!ipv4Map.containsKey(ipv4.getMac())) {
                ipv4Map.put(ipv4.getMac(), new ArrayList<>());
            }
            ipv4Map.get(ipv4.getMac()).add(ipv4.getIp());
        }

    }

    @Test
    public void test3(){
        // 假设有一组IPv4数据和一组IPv6数据
//        List<Ipv4> ipv4Objects = new ArrayList<>();
//        ipv4Objects.add(new Ipv4("192.168.1.1", "00:11:22:33:44:55", "8080"));
//        ipv4Objects.add(new Ipv4("192.168.1.2", "00:11:22:33:44:56", "8081"));
//
//        List<Ipv6> ipv6Objects = new ArrayList<>();
//        ipv6Objects.add(new Ipv6("FE80::1", "00:11:22:33:44:55", "8080"));
//        ipv6Objects.add(new Ipv6("FE80::2", "00:11:22:33:44:56", "8081"));


        List<Ipv4> ipv4Objects = new ArrayList<>();
        ipv4Objects.add(new Ipv4("192.168.1.1", "00:11:22:33:44:55", "11"));
        ipv4Objects.add(new Ipv4("192.168.1.2", "00:11:22:33:44:55", "22"));
        ipv4Objects.add(new Ipv4("192.168.1.3", "00:11:22:33:44:56", "33"));
        ipv4Objects.add(new Ipv4("192.168.1.4", "00:11:22:33:44:56", "44"));

        List<Ipv6> ipv6Objects = new ArrayList<>();
        ipv6Objects.add(new Ipv6("FE80::1", "00:11:22:33:44:55", "11"));
        ipv6Objects.add(new Ipv6("FE80::2", "00:11:22:33:44:55", "22"));
        ipv6Objects.add(new Ipv6("FE80::3", "00:11:22:33:44:56", "33"));
        ipv6Objects.add(new Ipv6("FE80::4", "00:11:22:33:44:56", "44"));

        // 使用Map来存储相同mac的数据，合并IPv4和IPv6数据
        Map<String, List<String>> ipv4Map = new HashMap<>();
        Map<String, List<String>> ipv6Map = new HashMap<>();

        // 处理IPv4数据
        for (Ipv4 ipv4 : ipv4Objects) {
            if (!ipv4Map.containsKey(ipv4.getMac())) {
                ipv4Map.put(ipv4.getMac(), new ArrayList<>());
            }
            ipv4Map.get(ipv4.getMac()).add(ipv4.getIp());
        }
        // 处理IPv6数据
        for (Ipv6 ipv6 : ipv6Objects) {
            if (!ipv6Map.containsKey(ipv6.getIpv6_mac())) {
                ipv6Map.put(ipv6.getIpv6_mac(), new ArrayList<>());
            }
            ipv6Map.get(ipv6.getIpv6_mac()).add(ipv6.getIpv6_address());
        }

        // 将合并后的数据存储到数组中
        List<String> mergedIPv4AndIPv6 = new ArrayList<>();
        List<String> ipv4Addresses = new ArrayList<>();
        List<String> ipv6Addresses = new ArrayList<>();

        // 合并IPv4和IPv6数据
        ipv4Map.forEach((mac, ips) -> {
            ipv6Map.computeIfPresent(mac, (k, ipv6s) -> {
                // 合并IPv4和IPv6数据到一条记录中
                for (String ipv4Ip : ips) {
                    for (String ipv6Ip : ipv6s) {
                        mergedIPv4AndIPv6.add("MAC: " + mac + ", IPv4: " + ipv4Ip + ", IPv6: " + ipv6Ip);
                    }
                }
                return ipv6s; // 返回计算后的IPv6列表
            });
        });

        System.out.println(JSONObject.toJSONString(ipv4Map));
        System.out.println(JSONObject.toJSONString(ipv6Map));

        // 将IPv4地址和IPv6地址分别存储到数组中
        ipv4Map.forEach((mac, ips) -> ipv4Addresses.addAll(ips));
        ipv6Map.forEach((mac, ips) -> ipv6Addresses.addAll(ips));

        // 打印结果
        System.out.println("合并的IPv4和IPv6数据：");
        mergedIPv4AndIPv6.forEach(System.out::println);

        System.out.println("\nIPv4 数组：");
        ipv4Addresses.forEach(System.out::println);

        System.out.println("\nIPv6 数组：");
        ipv6Addresses.forEach(System.out::println);
    }

}
