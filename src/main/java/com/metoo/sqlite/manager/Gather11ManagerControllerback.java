//package com.metoo.sqlite.manager;
//
//import com.alibaba.fastjson.JSONObject;
//import com.github.pagehelper.util.StringUtil;
//import com.metoo.sqlite.entity.*;
//import com.metoo.sqlite.gather.factory.gather.thread.Gather;
//import com.metoo.sqlite.gather.factory.gather.thread.GatherFactory;
//import com.metoo.sqlite.gather.self.SelfTerminalUtils;
//import com.metoo.sqlite.manager.api.ApiService;
//import com.metoo.sqlite.manager.api.JsonRequest;
//import com.metoo.sqlite.manager.utils.gather.ProbeToTerminalAndDeviceScan;
//import com.metoo.sqlite.manager.utils.gather.VerifyVendorUtils;
//import com.metoo.sqlite.manager.utils.jx.JXDataUtils;
//import com.metoo.sqlite.service.*;
//import com.metoo.sqlite.utils.Global;
//import com.metoo.sqlite.utils.ResponseUtil;
//import com.metoo.sqlite.utils.date.DateTools;
//import com.metoo.sqlite.vo.Result;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.text.SimpleDateFormat;
//import java.util.*;
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
//public class Gather11ManagerControllerback {
//
//    private final ApiService apiService;
//    private final ReentrantLock lock = new ReentrantLock();
//    @Autowired
//    private IProbeResultService probeResultService;
//    @Autowired
//    private IProbeService probeService;
//    @Autowired
//    private IUnreachService unreachService;
//    @Autowired
//    private ISubnetIpv6Service subnetIpv6Service;
//    @Autowired
//    private IGatewayInfoService gatewayInfoService;
//    @Autowired
//    private ITerminalService terminalService;
//    @Autowired
//    private IGatherLogService gatherLogService;
//    @Autowired
//    private ISurveyingLogService surveyingLogService;
//    @Value("${AP.URL}")
//    private String apUrl;
//    @Autowired
//    private ISubnetService subnetService;
//    @Autowired
//    private JXDataUtils jxDataUtils;
//    @Autowired
//    private IDeviceScanService deviceScanService;
//    @Autowired
//    private IDeviceService deviceService;
//    @Autowired
//    private Ipv4Service ipv4Service;
//    @Autowired
//    private SelfTerminalUtils selfTerminalUtils;
//    @Autowired
//    private ProbeToTerminalAndDeviceScan probeToTerminalAndDeviceScan;
//    @Autowired
//    private VerifyVendorUtils verifyVendorUtils;
//
//    @Autowired
//    public Gather11ManagerControllerback(ApiService apiService) {
//        this.apiService = apiService;
//    }
//
//    public static String getDate() {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String date = simpleDateFormat.format(new Date());
//        return date;
//    }
//
//    @GetMapping("/main")
//    public Result main(@RequestParam(value = "type", required = false) Integer type) {
//
//        if (lock.tryLock()) {
//
//            if (type != null && type == 1) {
//                if (lock.isHeldByCurrentThread()) {
//                    lock.unlock();
//                }
//                return ResponseUtil.ok(1001);
//            }
//            boolean flag = true;
//            List<Device> devices = this.deviceService.selectObjByMap(null);
//            if (devices.size() <= 0) {
//                if (lock.isHeldByCurrentThread()) {
//                    lock.unlock();
//                }
//                return ResponseUtil.ok(1003, "请先添加设备");
//            } else {
//                for (Device device : devices) {
//                    if (device.getDeviceVendorSequence() == 1) {
//                        flag = false;
//                        break;
//                    }
//                }
//            }
//
//            // 清空测绘日志
//            try {
//                this.surveyingLogService.deleteTable();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            try {
//                this.gatherLogService.deleteTable();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//            if (!flag) {
//
//                try {
//                    this.selfTerminalUtils.main();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (lock.isHeldByCurrentThread()) {
//                        lock.unlock();
//                        return ResponseUtil.ok("测绘完成");
//                    }
//                }
//            } else {
//
//                try {
//
//
//                    // 采集结果 - 开始时间
//                    String beginTime = DateTools.getCreateTime();
//                    Thread.sleep(500);
//
//                    // ipv4
//                    this.gatherIpv4(beginTime);
//
//                    // ipv6
//                    this.gatherIpv6();
//
//                    // Ping 未执行ALIVEINT，不会产生数据，该方法也不会执行
//                    this.gatherPing();
//
////                //arp --
////                this.gatherArp();
//
//                    //（全网资产扫描）调用创发接口
//                    // 分析数据，等待创发回调
//                    this.getProbeResult();
//
//                    // firewall 出口设备分析
////                this.gatherFirewall();
//
//                    // Arp 移到os-scanner后面，在os-scanner中为arp写入mac地址
//
//                    // switch
//                    this.gatherSwitch();
//
//                    // probe -> terminal
//                    // probe -> deviceScan
//                    // probe -> unsure
//
//                    // terminal 终端分析 this.gatherDeviceScan();
//                    this.gatherTerminal();
//                    // probe最终表数据，需不需要写入unsure表，然后在清空最终probe
////            this.gatherUnsure();
//
//                    String endTime = DateTools.getCreateTime();
//
//                    String data = this.jxDataUtils.getEncryptedData();
//
//                    this.gatherUploadData(data);// 数据上传
//
//                    String surveying = this.getSurveyingResult();
//
//                    try {
//                        GatherLog gatherLog = new GatherLog();
//                        gatherLog.setCreateTime(beginTime);
//                        gatherLog.setBeginTime(beginTime);
//                        gatherLog.setEndTime(endTime);
//                        gatherLog.setType("手动");
//                        gatherLog.setResult("成功");
//                        gatherLog.setDetails(surveying);
//                        gatherLog.setData(data);
//                        this.gatherLogService.insert(gatherLog);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (lock.isHeldByCurrentThread()) {
//                        lock.unlock();
//                        return ResponseUtil.ok("测绘完成");
//                    }
//                }
//            }
//        } else {
//            return ResponseUtil.ok(1002, "正在测绘");
//        }
//        return ResponseUtil.ok("测绘完成");
//    }
//
//    public void deleteProbe(String ip_addr) {
//        if (StringUtil.isNotEmpty(ip_addr)) {
//            Map params = new HashMap();
//            params.put("ip_addr", ip_addr);
//            List<Probe> deleteProbes = probeService.selectObjByMap(params);
//            if (deleteProbes.size() > 0) {
//                for (Probe deleteProbe : deleteProbes) {
//                    try {
//                        probeService.delete(deleteProbe.getId());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
//
//    public void insertDeviceScan(Unreach instance, String os) {
//        DeviceScan deviceScan = new DeviceScan();
//        deviceScan.setDevice_product(os);
//        deviceScan.setDevice_ipv4(instance.getIp_addr());
//        deviceScan.setMac(instance.getMac_addr());
//        deviceScan.setMacVendor(instance.getMac_vendor());
//        try {
//            Map params = new HashMap();
//            params.put("device_ipv4", instance.getIp_addr());
//            List<DeviceScan> deviceScanList = this.deviceScanService.selectObjByMap(params);
//            if (deviceScanList.size() <= 0) {
//                this.deviceScanService.insert(deviceScan);
//            }
//            this.unreachService.delete(instance.getId());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void gatherIpv4(String beginTime) {
//        try {
//
//            this.careateSureyingLog("ipv4网段分析", beginTime, 1);
//
//            // ipv4
//            GatherFactory factory = new GatherFactory();
//            Gather gather = factory.getGather(Global.IPV4_PANABIT);
//            gather.executeMethod();
//
//            factory = new GatherFactory();
//            gather = factory.getGather(Global.PY_SUFFIX_ALIVEINT);
//            gather.executeMethod();
//
//            // ipv4 网段梳理
//            this.subnetService.comb();
//
//
//            // 梳理结果，存入采集日志
//            String endTime = DateTools.getCreateTime();
//
//            this.updateSureyingLog("ipv4网段分析", endTime, 2);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            String endTime = DateTools.getCreateTime();
//            this.updateSureyingLog("ipv4网段分析", endTime, 3);
//        }
//    }
//
//    public void gatherIpv6() {
//        try {
//
//            String beginTime = DateTools.getCreateTime();
//            this.careateSureyingLog("ipv6网段分析", beginTime, 1);
//
//            GatherFactory factory = new GatherFactory();
//            Gather gather = factory.getGather(Global.IPV6_PANABIT);
//            gather.executeMethod();
//
//            factory = new GatherFactory();
//            gather = factory.getGather(Global.SUBNET_IPV6);
//            gather.executeMethod();
//
//            String endTime = DateTools.getCreateTime();
//            this.updateSureyingLog("ipv6网段分析", endTime, 2);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            String endTime = DateTools.getCreateTime();
//            this.updateSureyingLog("ipv6网段分析", endTime, 3);
//        }
//    }
//
//    public void gatherPing() {
//
//        try {
//            String beginTime = DateTools.getCreateTime();
//
//            this.careateSureyingLog("全网ping测试", beginTime, 1);
//
//            GatherFactory factory = new GatherFactory();
//            Gather gather = factory.getGather(Global.PY_SUFFIX_PING);
//            gather.executeMethod();
//
//            String endTime = DateTools.getCreateTime();
//
//            this.updateSureyingLog("全网ping测试", endTime, 2);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            String endTime = DateTools.getCreateTime();
//            this.updateSureyingLog("全网ping测试", endTime, 3);
//        }
//    }
//
//    public void gatherArp() {
//        try {
//            GatherFactory factory = new GatherFactory();
//            Gather gather = factory.getGather(Global.ARP);
//            gather.executeMethod();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void gatherSwitch() {
//
//        try {
//            String beginTime = DateTools.getCreateTime();
//
//            this.careateSureyingLog("网络设备分析", beginTime, 1);
//
//            GatherFactory factory = new GatherFactory();
//            Gather gather = factory.getGather(Global.PY_SUFFIX_GET_SWITCH);
//            gather.executeMethod();
//
//            String endTime = DateTools.getCreateTime();
//
//            this.updateSureyingLog("网络设备分析", endTime, 2);
//        } catch (Exception e) {
//            e.printStackTrace();
//            String endTime = DateTools.getCreateTime();
//            this.updateSureyingLog("网络设备分析", endTime, 3);
//        }
//    }
//
//    public void gatherFirewall() {
//
//        try {
//            String beginTime = DateTools.getCreateTime();
//
////            this.careateSureyingLog("出口设备分析", beginTime, 1);
//
//            GatherFactory factory = new GatherFactory();
//            Gather gather = factory.getGather(Global.PY_SUFFIX_GET_FIREWALL);
//            gather.executeMethod();
//
//            String endTime = DateTools.getCreateTime();
//
////            this.updateSureyingLog("出口设备分析", endTime, 2);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void gatherDeviceScan() {
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.DEVICE_SCAN);
//        gather.executeMethod();
//    }
//
//    public void gatherTerminal() {
//        try {
//            String beginTime = DateTools.getCreateTime();
//
//            this.careateSureyingLog("终端分析", beginTime, 1);
//
//            this.gatherArp();
//
//            this.gatherDeviceScan();// metoo_scan
//
//            GatherFactory factory = new GatherFactory();
//            Gather gather = factory.getGather(Global.TERMINAL);
//            gather.executeMethod();
//
////            this.finalProbe();
//            this.probeToTerminalAndDeviceScan.finalProbe();
//            this.verifyVendorUtils.finalTerminal();
//
//            String endTime = DateTools.getCreateTime();
//
//
//            this.updateSureyingLog("终端分析", endTime, 2);
//        } catch (Exception e) {
//            e.printStackTrace();
//            String endTime = DateTools.getCreateTime();
//            this.updateSureyingLog("终端分析", endTime, 3);
//        }
//    }
//
//    public void gatherUploadData(String data) {
//        String beginTime = DateTools.getCreateTime();
//        this.careateSureyingLog("生成加密结果文件", beginTime, 1);
//
//        try {
//            Thread.sleep(1);//0000
//            String endTime = DateTools.getCreateTime();
//            this.updateSureyingLog("生成加密结果文件", endTime, 2);
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
//    public String getProbeResult() {
//
//        log.info("Probe start===============");
//
//        String begin_time = DateTools.getCreateTime();
//
//        this.careateSureyingLog("全网资产扫描", begin_time, 1);
//
//        try {
//            // 调用创发接口，分批次调用，每次100个ipv4地址调用创发接口，分批次调用，每次100个ipv4地址
//            List<Ipv4> ipv4List = this.ipv4Service.selectObjByMap(null);
//            if (ipv4List.size() > 0) {
//                if (ipv4List.size() >= 300) {
//                    int batchSize = 100;
//                    int totalSize = ipv4List.size();
//                    try {
//                        try {
//                            for (int i = 0; i < totalSize; i += batchSize) {
//                                int end = Math.min(totalSize, i + batchSize);
//                                List<Ipv4> subList = ipv4List.subList(i, end);
//                                // 处理数据
//                                // 提取ARP对象列表中的IP地址
//                                List<String> ipAddresses = subList.stream()
//                                        .map(Ipv4::getIp) // 提取IP地址
//                                        .filter(ip -> ip != null && !ip.isEmpty()) // 过滤掉空的IP地址
//                                        .collect(Collectors.toList()); // 收集成列表
//                                String ipAddressString = String.join(",", ipAddresses);
//                                if (StringUtil.isNotEmpty(ipAddressString)) {
//                                    this.callChuangfa(ipAddressString);
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            String endTime = DateTools.getCreateTime();
//                            this.updateSureyingLog("全网资产扫描", endTime, 3);
//
//                        }
//
//
//                        try {
//                            GatherFactory factory = new GatherFactory();
//                            Gather gather = factory.getGather("fileToProbe");
//                            gather.executeMethod();
//
//                            this.probeService.deleteTableBack();
//                            this.probeService.copyToBck();
//
//                            List list = this.probeService.selectObjByMap(null);
//                            log.info("chuangfa + os_scan========================================：" + JSONObject.toJSONString(list));
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        String endTime = DateTools.getCreateTime();
//                        this.updateSureyingLog("全网资产扫描", endTime, 2);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//
//                        String endTime = DateTools.getCreateTime();
//                        this.updateSureyingLog("全网资产扫描", endTime, 3);
//
//                    }
//                } else {
//                    // 提取ARP对象列表中的IP地址
//                    List<String> ipAddresses = ipv4List.stream()
//                            .map(Ipv4::getIp) // 提取IP地址
//                            .filter(ip -> ip != null && !ip.isEmpty()) // 过滤掉空的IP地址
//                            .collect(Collectors.toList()); // 收集成列表
//
//                    String ipAddressString = String.join(",", ipAddresses);
//
//                    if (StringUtil.isNotEmpty(ipAddressString)) {
//
//                        boolean flag = this.callChuangfaSingle(ipAddressString);
//                        if (flag) {
//                            String endTime = DateTools.getCreateTime();
//                            this.updateSureyingLog("全网资产扫描", endTime, 2);
//                        } else {
//                            String endTime = DateTools.getCreateTime();
//                            this.updateSureyingLog("全网资产扫描", endTime, 3);
//                        }
//                    }
//                }
//                log.info("Probe end===============");
//                return null;
//            } else {
//                String endTime = DateTools.getCreateTime();
//                this.updateSureyingLog("全网资产扫描", endTime, 2);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            // 这里记录日志，不用返回null判断
//            String endTime = DateTools.getCreateTime();
//            this.updateSureyingLog("全网资产扫描", endTime, 3);
//        }
//
//
//        log.info("Probe end===============");
//
//        return null;
//    }
//
//    public void callChuangfa(String ipAddresses) {
//        log.info("Ipaddress================" + ipAddresses);
//        JsonRequest jsonRequest = new JsonRequest();
//
//        jsonRequest.setTaskuuid(UUID.randomUUID().toString());
//
//        jsonRequest.setThread("600");
//
//        jsonRequest.setTimeout("300");
//
//        jsonRequest.setIp(ipAddresses);
//
//        String result = apiService.callThirdPartyApi(apUrl, jsonRequest);
//
//        if (result != null) {
//            ProbeResult probeResult = this.probeResultService.selectObjByOne();
//            this.probeWait(result, probeResult);
//        }
//    }
//
//    public void probeWait(String result, ProbeResult probeResult) {
//
//        JSONObject json = JSONObject.parseObject(result);
//
//        if (json.getInteger("code") == 0) {
//            // 等待
//            Map params = new HashMap();
//            while (true) {
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
//                if (obj.size() > 0) {
//                    break;
//                }
//            }
//        }
//    }
//
//    public boolean callChuangfaSingle(String ipAddresses) {
//        log.info("Ipaddress================" + ipAddresses);
//        try {
//            JsonRequest jsonRequest = new JsonRequest();
//
//            jsonRequest.setTaskuuid(UUID.randomUUID().toString());
//
//            jsonRequest.setThread("600");
//
//            jsonRequest.setTimeout("3000");
//
//            jsonRequest.setIp(ipAddresses);
//
//            String result = apiService.callThirdPartyApi(apUrl, jsonRequest);
//
//            if (result != null) {
//                ProbeResult probeResult = this.probeResultService.selectObjByOne();
//                try {
//                    this.probeWaitSingle(result, probeResult);
//                    return true;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    public void probeWaitSingle(String result, ProbeResult probeResult) {
//
//        JSONObject json = JSONObject.parseObject(result);
//
//        if (json.getInteger("code") == 0) {
//            // 等待
//            Map params = new HashMap();
//            while (true) {
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
//                if (obj.size() > 0) {
//
//                    try {
//                        GatherFactory factory = new GatherFactory();
//                        Gather gather = factory.getGather("fileToProbe");
//                        gather.executeMethod();
//
//                        this.probeService.deleteTableBack();
//                        this.probeService.copyToBck();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    List list = this.probeService.selectObjByMap(null);
//                    log.info("chuangfa + os_scan========================================：" + JSONObject.toJSONString(list));
//
//                    break;
//                }
//            }
//        }
//
//    }
//
//
//    // 获取测绘结果，采集格式化后的数据
//    public String getSurveyingResult() {
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
//    public void careateSureyingLog(String name, String beginTime, Integer status) {
//        SurveyingLog surveyingLog = new SurveyingLog().createTime(DateTools.getCreateTime()).name(name).beginTime(beginTime).status(status);
//        this.surveyingLogService.insert(surveyingLog);
//    }
//
//    public void updateSureyingLog(String name, String endTime, Integer status) {
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
//        if (surveyingLogs.size() > 0) {
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
//
//}
