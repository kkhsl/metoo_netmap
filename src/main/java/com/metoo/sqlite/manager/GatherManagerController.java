//package com.metoo.sqlite.manager;
//
//import com.alibaba.fastjson.JSONObject;
//import com.metoo.sqlite.entity.*;
//import com.metoo.sqlite.gather.factory.gather.thread.Gather;
//import com.metoo.sqlite.gather.factory.gather.thread.GatherFactory;
//import com.metoo.sqlite.manager.api.ApiService;
//import com.metoo.sqlite.manager.api.JsonRequest;
//import com.metoo.sqlite.manager.utils.ArpUtils;
//import com.metoo.sqlite.manager.utils.jx.JXDataUtils;
//import com.metoo.sqlite.manager.utils.jx.SendKafkaMsg;
//import com.metoo.sqlite.service.*;
//import com.metoo.sqlite.utils.Global;
//import com.metoo.sqlite.utils.ResponseUtil;
//import com.metoo.sqlite.utils.date.DateTools;
//import com.metoo.sqlite.vo.DeviceInfoVo;
//import com.metoo.sqlite.vo.GatewayInfoVo;
//import com.metoo.sqlite.vo.Result;
//import com.metoo.sqlite.vo.TerminalInfoVo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//import java.util.stream.Collectors;
//
///**
// * @author HKK
// * @version 1.0
// * @date 2024-06-24 12:50
// */
//
//
//@Slf4j
//@RequestMapping("/admin/gather")
//@RestController
//public class GatherManagerController {
//
//    private final ApiService apiService;
//    @Autowired
//    private IProbeResultService probeResultService;
//    @Autowired
//    private ISubnetIpv6Service subnetIpv6Service;
//    @Autowired
//    private IGatewayInfoService gatewayInfoService;
//    @Autowired
//    private ITerminalService terminalService;
//    @Autowired
//    private IGatherLogService gatherLogService;
//    @Autowired
//    private IGatherSemaphoreService gatherSemaphoreService;
//    @Autowired
//    private ISurveyingLogService surveyingLogService;
//    @Autowired
//    private IArpService arpService;
//    @Value("${AP.URL}")
//    private String apUrl;
//    @Autowired
//    private ArpUtils arpUtils;
//    @Autowired
//    private ISubnetService subnetService;
//    @Autowired
//    private JXDataUtils jxDataUtils;
//
//    @Autowired
//    public GatherManagerController(ApiService apiService) {
//        this.apiService = apiService;
//    }
//
//    private final Lock lock = new ReentrantLock();
//
//    @GetMapping("/main1")
//    public void test(){
//        String data = this.jxDataUtils.getData();// 数据上传
//        System.out.println(data);
//    }
//
//    // 暂时不加锁，不考虑并发
//    @GetMapping("/main")
//    public Result main() {
//
//        GatherSemaphore gatherSemaphore = null;
//
//        // 查询采集信号量
//        gatherSemaphore = this.gatherSemaphoreService.selectObjByOne(); // 死锁
//        if (gatherSemaphore.getSemaphore() == 1) {
//            return ResponseUtil.badArgument("正在采集...");
//        }
//
//        try {
//            gatherSemaphore.setSemaphore(1);
//            this.gatherSemaphoreService.update(gatherSemaphore);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//
//            // 清空测绘日志
//            this.surveyingLogService.deleteTable();
//
//            // 采集结果 - 开始时间
//            String beginTime = DateTools.getCreateTime();
//
//            Thread.sleep(500);
//            this.careateSureyingLog("ipv4网段分析", beginTime, 1);
//
//            // ipv4
//            this.gatherIpv4();
//
//            // ipv6
//            this.gatherIpv6();
//
//            // port ipv4/ipv6 （ipv4网段分析/ipv6网段分析）
//            this.portIpv4AndIpv6();
//
//            // Ping
//            this.gatherPing();
//
//            // arp
//            this.gatherArp();
//
//            //（全网资产扫描）调用创发接口
//            ProbeResult probeResult = this.probeResultService.selectObjByOne();
//            // 分析数据，等待创发回调
//            String result = this.getProbeResult();
//
//            if(result == null){
//                String endTime = DateTools.getCreateTime();
//                this.updateSureyingLog("全网资产扫描", endTime, 3);
//            }
//
//            // switch
//            this.gatherSwitch();
//
//            // firewall 出口设备分析
//            this.gatherFirewall();
//
//            if(result != null){
//                this.probeWait(result, probeResult);
//            }
//
//            // terminal 终端分析 this.gatherDeviceScan();
//            this.gatherTerminal();
//
//            String endTime = DateTools.getCreateTime();
//
//            String data = this.jxDataUtils.getEncryptedData();
//
//            this.gatherUploadData(data);// 数据上传
//
//            String surveying = this.getSurveyingResult();
//
//            try {
//                GatherLog gatherLog = new GatherLog();
//                gatherLog.setCreateTime(beginTime);
//                gatherLog.setBeginTime(beginTime);
//                gatherLog.setEndTime(endTime);
//                gatherLog.setType("手动");
//                gatherLog.setResult("成功");
//                gatherLog.setDetails(surveying);
//                gatherLog.setData(data);
//                this.gatherLogService.insert(gatherLog);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            gatherSemaphore.setSemaphore(0);// 采集结束
//            this.gatherSemaphoreService.update(gatherSemaphore);
//        }
//
//        return ResponseUtil.ok("采集成功");
//
//    }
//
//    public void gatherIpv4(){
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.PY_SUFFIX_IPV4);
//        gather.executeMethod();
//    }
//
//    public void gatherIpv6(){
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.PY_SUFFIX_IPV6_NEIGHBORS);
//        gather.executeMethod();
//    }
//
//
//    public void portIpv4AndIpv6(){
//        // port ipv4/ipv6 采集开始时间
//
////        this.careateSureyingLog("ipv4网段分析", beginTime, 1);
//
//
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.PY_SUFFIX_ALIVEINT);
//        gather.executeMethod();
//
//        // ipv4/ipv6 网段梳理
//        this.subnetService.comb();
//
//        // port ipv4/ipv6 采集结束时间
//
//        // 梳理结果，存入采集日志
//
//        String endTime = DateTools.getCreateTime();
//
//        this.updateSureyingLog("ipv4网段分析", endTime, 2);
//
//
//        String beginTime = DateTools.getCreateTime();
//        this.careateSureyingLog("ipv6网段分析", beginTime, 1);
//
//        // 休眠时间
//        try {
//            Thread.sleep(10000 + new Random().nextInt(5000));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        endTime = DateTools.getCreateTime();
//        this.updateSureyingLog("ipv6网段分析", endTime, 2);
//
//    }
//
//    public void gatherPing(){
//
//        String beginTime = DateTools.getCreateTime();
//
//        this.careateSureyingLog("全网ping测试", beginTime, 1);
//
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.PY_SUFFIX_PING);
//        gather.executeMethod();
//
//
//        String endTime = DateTools.getCreateTime();
//
//        this.updateSureyingLog("全网ping测试", endTime, 2);
//    }
//
//    public void gatherArp(){
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.ARP);
//        gather.executeMethod();
//    }
//
//    public void gatherSwitch(){
//
//        String beginTime = DateTools.getCreateTime();
//
//        this.careateSureyingLog("网络设备分析", beginTime, 1);
//
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.PY_SUFFIX_GET_SWITCH);
//        gather.executeMethod();
//
//        String endTime = DateTools.getCreateTime();
//
//        this.updateSureyingLog("网络设备分析", endTime, 2);
//    }
//
//    public void gatherFirewall(){
//
//        String beginTime = DateTools.getCreateTime();
//
//        this.careateSureyingLog("出口设备分析", beginTime, 1);
//
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.PY_SUFFIX_GET_FIREWALL);
//        gather.executeMethod();
//
//        String endTime = DateTools.getCreateTime();
//
//        this.updateSureyingLog("出口设备分析", endTime, 2);
//    }
//
//    public void gatherDeviceScan(){
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.DEVICE_SCAN);
//        gather.executeMethod();
//    }
//
//    public void gatherTerminal(){
//        String beginTime = DateTools.getCreateTime();
//        this.gatherDeviceScan();// metoo_scan
//        this.careateSureyingLog("终端分析", beginTime, 1);
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.TERMINAL);
//        gather.executeMethod();
//        String endTime = DateTools.getCreateTime();
//        this.updateSureyingLog("终端分析", endTime, 2);
//    }
//
//    public void gatherUploadData(String data){
//        String beginTime = DateTools.getCreateTime();
//        this.careateSureyingLog("数据上传", beginTime, 1);
//
//        try {
//            Thread.sleep(1);//0000
//            String endTime = DateTools.getCreateTime();
//            this.updateSureyingLog("数据上传", endTime, 2);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//
//        // 休眠时间
////        try {
////            // 调用kafka
////            SendKafkaMsg sendKafkaMsg = new SendKafkaMsg();
////            boolean flag =sendKafkaMsg.send(data);
////
////            Thread.sleep(10000);
////            if(flag){
////                String endTime = DateTools.getCreateTime();
////                this.updateSureyingLog("数据上传", endTime, 2);
////
////            }else{
////                String endTime = DateTools.getCreateTime();
////                this.updateSureyingLog("数据上传", endTime, 3);
////
////            }
////
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////
////            String endTime = DateTools.getCreateTime();
////            this.updateSureyingLog("数据上传", endTime, 3);
////        }
//    }
//
//    public static String getDate(){
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String date = simpleDateFormat.format(new Date());
//        return date;
//    }
//
//    public String getProbeResult(){
//        //
//        String scan_beginTime = DateTools.getCreateTime();
//        this.careateSureyingLog("全网资产扫描", scan_beginTime, 1);
//
//        try {
//            JsonRequest jsonRequest = new JsonRequest();
//            jsonRequest.setTaskuuid(UUID.randomUUID().toString());
//
//            // 调创发接口填port参数 OsFamily
//            jsonRequest.setPort("1-10000");
//
//            //arp表里的所有ip
//            List<Arp> arps = this.arpService.selectObjByMap(null);
//            if(arps.size() > 0){
//                // 提取ARP对象列表中的IP地址
//                List<String> ipAddresses = arps.stream()
//                        .map(Arp::getIp) // 提取IP地址
//                        .filter(ip -> ip != null && !ip.isEmpty()) // 过滤掉空的IP地址
//                        .collect(Collectors.toList()); // 收集成列表
//
//                // 将IP地址列表组合成逗号分隔的字符串
//                String ipAddressString = String.join(",", ipAddresses);
//
//                log.info("ipAddressString: " + ipAddressString);
//
//                jsonRequest.setIp(ipAddressString);
//            }
//            String result = apiService.callThirdPartyApi(apUrl, jsonRequest);
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public void probeWait(String result, ProbeResult probeResult){
//        JSONObject json = JSONObject.parseObject(result);
//        if(json.getInteger("code") == 0 ){
//            // 等待
//            Map params = new HashMap();
//            while (true){
//
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                System.out.println("wait...........");
//
//                params.clear();
//                params.put("result", probeResult.getResult() + 1);
//                List<ProbeResult> obj = this.probeResultService.selectObjByMap(params);
//                if(obj.size() > 0){
//                    break;
//                }
//            }
//        }
//
//
//        String endTime = DateTools.getCreateTime();
//        this.updateSureyingLog("全网资产扫描", endTime, 2);
//    }
//
//    // 获取测绘结果，采集格式化后的数据
//    public String getSurveyingResult(){
//        Map details = new HashMap();
//        try {
//            details.put("subme_ipv4", this.subnet_ipv4());
//            details.put("subme_ipv6", this.subnet_ipv6());
//            List<GatewayInfo> gatewayInfoList = this.gatewayInfoService.selectObjByMap(null);
//            details.put("gatewayInfo", gatewayInfoList);
//            Map params = new HashMap();
//            params.clear();
//            params.put("ipv4IsNotNull", "ipv4addr");
//            List<Terminal> terminal_ipv4 = this.terminalService.selectObjByMap(params);
//            params.clear();
//            params.put("ipv4AndIpv6IsNotNull", "ipv6addr");
//            List<Terminal> teminal_ipv6 = this.terminalService.selectObjByMap(params);
//            details.put("ipv4", terminal_ipv4.size());
//            details.put("ipv4_ipv6", teminal_ipv6.size());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return JSONObject.toJSONString(details);
//    }
//
//
//    public void careateSureyingLog(String name, String beginTime, Integer status){
//        SurveyingLog surveyingLog = new SurveyingLog().createTime(DateTools.getCreateTime()).name(name).beginTime(beginTime).status(status);
//        this.surveyingLogService.insert(surveyingLog);
//    }
//
//    public void updateSureyingLog(String name, String endTime, Integer status){
//
//        try {
//            Thread.sleep(10000); // 手动设置测绘休眠
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        endTime = DateTools.getCreateTime();
//
//        Map params = new HashMap();
//        params.put("name", name);
//        List<SurveyingLog> surveyingLogs = this.surveyingLogService.selectObjByMap(params);
//        if(surveyingLogs.size() > 0){
//            SurveyingLog surveyingLog = surveyingLogs.get(0).endTime(endTime).status(status);
//            this.surveyingLogService.update(surveyingLog);
//        }
//
//
//        try {
//            Thread.sleep(200); // 手动设置测绘休眠
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//    public String subnet_ipv4() {
//        // 获取所有子网一级
//        List<Subnet> parentList = this.subnetService.selectSubnetByParentId(null);
//        if (parentList.size() > 0) {
//            for (Subnet subnet : parentList) {
//                this.genericSubnet(subnet);
//            }
//        }
//        return JSONObject.toJSONString(parentList);
//    }
//
//    public List<Subnet> genericSubnet(Subnet subnet) {
//        List<Subnet> subnets = this.subnetService.selectSubnetByParentId(subnet.getId());
//        if (subnets.size() > 0) {
//            for (Subnet child : subnets) {
//                List<Subnet> subnetList = genericSubnet(child);
//                if (subnetList.size() > 0) {
//                    child.setSubnetList(subnetList);
//                }
//            }
//            subnet.setSubnetList(subnets);
//        }
//        return subnets;
//    }
//
//    public String subnet_ipv6() {
//        // 获取所有子网一级
//        List<SubnetIpv6> parentList = this.subnetIpv6Service.selectSubnetByParentId(null);
//        if (parentList.size() > 0) {
//            for (SubnetIpv6 subnet : parentList) {
//                this.genericSubnetIpv6(subnet);
//            }
//        }
//        return JSONObject.toJSONString(parentList);
//    }
//
//    public List<SubnetIpv6> genericSubnetIpv6(SubnetIpv6 subnetIpv6) {
//        List<SubnetIpv6> subnets = this.subnetIpv6Service.selectSubnetByParentId(subnetIpv6.getId());
//        if (subnets.size() > 0) {
//            for (SubnetIpv6 child : subnets) {
//                List<SubnetIpv6> subnetList = genericSubnetIpv6(child);
//                if (subnetList.size() > 0) {
//                    child.setSubnetList(subnetList);
//                }
//            }
//            subnetIpv6.setSubnetList(subnets);
//        }
//        return subnets;
//    }
//
//    @GetMapping("/port/ipv4")
//    public void portIpv4() {
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.PY_SUFFIX_ALIVEINT);
//        gather.executeMethod();
//    }
//
//    @GetMapping("/ipv4")
//    public void ipv4() {
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.PY_SUFFIX_IPV4);
//        gather.executeMethod();
//    }
//
//    @GetMapping("/device/scan")
//    public void deviceScan() {
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.DEVICE_SCAN);
//        gather.executeMethod();
//    }
//
//    @GetMapping("/ipv6")
//    public void ipv6() {
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.PY_SUFFIX_IPV6_NEIGHBORS);
//        gather.executeMethod();
//    }
//
//    @GetMapping("/ping")
//    public void ping() {
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.PY_SUFFIX_PING);
//        gather.executeMethod();
//    }
//
//    @GetMapping("/subnet/ipv6")
//    public void subnet() {
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.SUBNET_IPV6);
//        gather.executeMethod();
//    }
//
//    @GetMapping("/arp2")
//    public void arp2() {
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.ARP);
//        gather.executeMethod();
//    }
//
//    @GetMapping("/device")
//    public void device() {
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.PY_SUFFIX_GET_SWITCH);
//        gather.executeMethod();
//    }
//
//    @GetMapping("/gateway/info")
//    public void gatewayInfo() {
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.PY_SUFFIX_GET_FIREWALL);
//        gather.executeMethod();
//    }
//
//    @GetMapping("/gateway/operator")
//    public void gatewayInfoOperator() {
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.GATEWAY_OPERATOR);
//        gather.executeMethod();
//    }
//
//    @Transactional
//    @GetMapping("/arp")
//    public void arp() {
//
//        this.arpService.deleteTable();
//
//        List<Arp> arps = arpUtils.getArp();
//
//        this.arpService.batchInsert(arps);
//
//        System.out.println(arps);
//    }
//
//    @GetMapping("pingTest")
//    public void pingTest() {
//        Set<Subnet> set = new HashSet<>();
//        List<Subnet> parentList = this.subnetService.selectSubnetByParentId(null);
//        if (parentList.size() > 0) {
//            for (Subnet subnet : parentList) {
//                this.genericSubnet(subnet, set);
//            }
//        }
//        System.out.println(set);
//    }
//
//    public List<Subnet> genericSubnet(Subnet subnet, Set<Subnet> set) {
//        List<Subnet> subnets = this.subnetService.selectSubnetByParentId(subnet.getId());
//        if (subnets.size() > 0) {
//            for (Subnet child : subnets) {
//                List<Subnet> subnetList = genericSubnet(child, set);
//                if (subnetList.size() > 0) {
//                    child.setSubnetList(subnetList);
//                } else {
//                    set.add(child);
//                }
//            }
//        } else {
//            set.add(subnet);
//        }
//        return subnets;
//    }
//
//
//
//
//
//
//
//
//
//
//
//
////    @GetMapping("/main")
////    public Result main() {
////
////        GatherSemaphore gatherSemaphore = null;
////
////        // 查询采集信号量
////        gatherSemaphore = this.gatherSemaphoreService.selectObjByOne();
////        if (gatherSemaphore.getSemaphore() == 1) {
////            return ResponseUtil.badArgument("正在采集...");
////        }
////
////        try {
////            gatherSemaphore.setSemaphore(1);
////            this.gatherSemaphoreService.update(gatherSemaphore);
////        } catch (Exception e) {
////            e.printStackTrace();
//////            return ResponseUtil.error("采集失败");
////        }
////
////        try {
////
////            // 采集结果开始时间
////            String beginTime = DateTools.getCreateTime();
////
////
////
////
////            GatherFactory factory = new GatherFactory();
////            Gather gather = factory.getGather(Global.PY_SUFFIX_IPV4);
////            gather.executeMethod();
////
////            gather = factory.getGather(Global.PY_SUFFIX_IPV6_NEIGHBORS);
////            gather.executeMethod();
////
////
////            // port ipv4/ipv6 采集开始时间
////            String port_ipv4_ipv6_beginTime = DateTools.getCreateTime();
////
////            SurveyingLog surveyingLog = new SurveyingLog();
////            surveyingLog.setCreateTime(DateTools.getCreateTime());
////
////            gather = factory.getGather(Global.PY_SUFFIX_ALIVEINT);
////            gather.executeMethod();
////
////            Map details = new HashMap();
////
////            // ipv4/ipv6 网段梳理
////            this.subnetService.comb();
////
////            // port ipv4/ipv6 采集结束时间
////            String port_ipv4_ipv6_endTime = DateTools.getCreateTime();
////
////
////            // 梳理结果，存入采集日志
////            details.put("subme_ipv4", this.subnet_ipv4());
////            details.put("subme_ipv6", this.subnet_ipv6());
////
////            // Ping
////            gather = factory.getGather(Global.PY_SUFFIX_PING);
////            gather.executeMethod();
////
////            // arp
////            gather = factory.getGather(Global.ARP);
////            gather.executeMethod();
////
////            // 调用创发接口
////            String result = null;
////
////            ProbeResult probeResult = this.probeResultService.selectObjByOne();
////            // 分析数据，等待创发回调
////
////            try {
////                JsonRequest jsonRequest = new JsonRequest();
////                jsonRequest.setTaskuuid(UUID.randomUUID().toString());
////
////                //arp表里的所有ip
////                List<Arp> arps = this.arpService.selectObjByMap(null);
////                if(arps.size() > 0){
////                    // 提取ARP对象列表中的IP地址
////                    List<String> ipAddresses = arps.stream()
////                            .map(Arp::getIp) // 提取IP地址
////                            .filter(ip -> ip != null && !ip.isEmpty()) // 过滤掉空的IP地址
////                            .collect(Collectors.toList()); // 收集成列表
////
////                    // 将IP地址列表组合成逗号分隔的字符串
////                    String ipAddressString = String.join(",", ipAddresses);
////                    jsonRequest.setIp(ipAddressString);
////                }
////                result = apiService.callThirdPartyApi(apUrl, jsonRequest);
////                System.out.println(result);
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////
////            // switch
////            gather = factory.getGather(Global.PY_SUFFIX_GET_SWITCH);
////            gather.executeMethod();
////
////            // firewall
////            gather = factory.getGather(Global.PY_SUFFIX_GET_FIREWALL);
////            gather.executeMethod();
////            // 出口数据
////            List<GatewayInfo> gatewayInfoList = this.gatewayInfoService.selectObjByMap(null);
////            details.put("gatewayInfo", gatewayInfoList);
////
////            // 防火墙品牌(作废)
//////        gather = factory.getGather(Global.GATEWAY_OPERATOR);
//////        gather.executeMethod();
////
////            if(result != null){
////
////                JSONObject json = JSONObject.parseObject(result);
////                if(json.getInteger("code") == 0 ){
////                    // 等待
////                    Map params = new HashMap();
////                    while (true){
////
////                        try {
////                            Thread.sleep(5000);
////                        } catch (InterruptedException e) {
////                            e.printStackTrace();
////                        }
////
////                        System.out.println("wait...........");
////
////                        params.clear();
////                        params.put("result", probeResult.getResult() + 1);
////                        List<ProbeResult> obj = this.probeResultService.selectObjByMap(params);
////                        if(obj.size() > 0){
////                            break;
////                        }
////                    }
////                }
////            }
////
////
////            // metoo_scan
////            gather = factory.getGather(Global.DEVICE_SCAN);
////            gather.executeMethod();
////
////
////            // terminal
////            gather = factory.getGather(Global.TERMINAL);
////            gather.executeMethod();
////
////
////            Map params = new HashMap();
////            params.clear();
////            params.put("ipv4IsNotNull", "ipv4addr");
////            List<Terminal> terminal_ipv4 = this.terminalService.selectObjByMap(params);
////            params.clear();
////            params.put("ipv4AndIpv6IsNotNull", "ipv6addr");
////            List<Terminal> teminal_ipv6 = this.terminalService.selectObjByMap(params);
////
////            details.put("ipv4", terminal_ipv4.size());
////            details.put("ipv4_ipv6", teminal_ipv6.size());
////
////            String endTime = DateTools.getCreateTime();
////
////            try {
////                GatherLog gatherLog = new GatherLog();
////                gatherLog.setCreateTime(DateTools.getCreateTime());
////                gatherLog.setBeginTime(beginTime);
////                gatherLog.setEndTime(endTime);
////                gatherLog.setType("自动");
////                gatherLog.setResult("成功");
////                gatherLog.setDetails(JSONObject.toJSONString(details));
////
////                this.gatherLogService.insert(gatherLog);
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////
////        } catch (Exception e) {
////            e.printStackTrace();
////        } finally {
////            gatherSemaphore.setSemaphore(0);// 采集结束
////            this.gatherSemaphoreService.update(gatherSemaphore);
////        }
////
////        return ResponseUtil.ok("采集成功");
////
////    }
//}
