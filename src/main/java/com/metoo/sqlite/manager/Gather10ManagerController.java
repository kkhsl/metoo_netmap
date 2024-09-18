//package com.metoo.sqlite.manager;
//
//import com.alibaba.fastjson.JSONObject;
//import com.github.pagehelper.util.StringUtil;
//import com.metoo.sqlite.entity.*;
//import com.metoo.sqlite.gather.factory.gather.thread.Gather;
//import com.metoo.sqlite.gather.factory.gather.thread.GatherFactory;
//import com.metoo.sqlite.manager.api.ApiService;
//import com.metoo.sqlite.manager.api.JsonRequest;
//import com.metoo.sqlite.manager.utils.ArpUtils;
//import com.metoo.sqlite.manager.utils.MacUtils;
//import com.metoo.sqlite.manager.utils.ProbeUtils;
//import com.metoo.sqlite.manager.utils.jx.JXDataUtils;
//import com.metoo.sqlite.service.*;
//import com.metoo.sqlite.utils.CopyPropertiesReflect;
//import com.metoo.sqlite.utils.Global;
//import com.metoo.sqlite.utils.ResponseUtil;
//import com.metoo.sqlite.utils.date.DateTools;
//import com.metoo.sqlite.vo.Result;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
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
//public class Gather10ManagerController {
//
//    private final ApiService apiService;
//    @Autowired
//    private IProbeResultService probeResultService;
//    @Autowired
//    private IProbeService probeService;
//    @Autowired
//    private IUnsureService unsureService;
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
//    @Autowired
//    private IDeviceScanService deviceScanService;
//    @Autowired
//    private IMacVendorService macVendorService;
//
//    @Autowired
//    public Gather10ManagerController(ApiService apiService) {
//        this.apiService = apiService;
//    }
//
//    @Autowired
//    private ProbeUtils probeUtils;
//
//    private final ReentrantLock lock = new ReentrantLock();
//
//    @GetMapping("/main")
//    public Result main(@RequestParam(value = "type", required = false) Integer type) {
//
//        if (lock.tryLock()) {
//
//            if(type != null && type == 1){
//                if (lock.isHeldByCurrentThread()) {
//                    lock.unlock();
//                }
//                return ResponseUtil.ok(1001);
//            }
//
//            try {
//
//                // 清空测绘日志
//                try {
//                    this.surveyingLogService.deleteTable();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                    this.gatherLogService.deleteTable();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                // 采集结果 - 开始时间
//                String beginTime = DateTools.getCreateTime();
//                Thread.sleep(500);
//
//                // ipv4
//                this.gatherIpv4(beginTime);
//
//                // ipv6
//                this.gatherIpv6();
//
//                // Ping 未执行ALIVEINT，不会产生数据，该方法也不会执行
//                this.gatherPing();
//
//                //arp
//                this.gatherArp();
//
//                //（全网资产扫描）调用创发接口
//                ProbeResult probeResult = this.probeResultService.selectObjByOne();
//                // 分析数据，等待创发回调
//                String result = this.getProbeResult();
//
//                // switch
//                this.gatherSwitch();
//
//                // firewall 出口设备分析
////                this.gatherFirewall();
//
//                // 等待创发回调
////                if(result != null){
////                    this.probeWait(result, probeResult);
////                }
//
//                // probe -> terminal
//                // probe -> deviceScan
//                // probe -> unsure
//
//                // terminal 终端分析 this.gatherDeviceScan();
//                this.gatherTerminal();
//                // probe最终表数据，需不需要写入unsure表，然后在清空最终probe
////            this.gatherUnsure();
//
//                String endTime = DateTools.getCreateTime();
//
//                String data = this.jxDataUtils.getEncryptedData();
//
//                this.gatherUploadData(data);// 数据上传
//
//
//                String surveying = this.getSurveyingResult();
//
//                try {
//                    GatherLog gatherLog = new GatherLog();
//                    gatherLog.setCreateTime(beginTime);
//                    gatherLog.setBeginTime(beginTime);
//                    gatherLog.setEndTime(endTime);
//                    gatherLog.setType("手动");
//                    gatherLog.setResult("成功");
//                    gatherLog.setDetails(surveying);
//                    gatherLog.setData(data);
//                    this.gatherLogService.insert(gatherLog);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (lock.isHeldByCurrentThread()) {
//                    lock.unlock();
//                }
//            }
//        }else{
//            return ResponseUtil.ok(1002, "正在测绘");
//        }
//        return ResponseUtil.ok("测绘完成");
//    }
//
//    public void finalProbe() {
//        Map params = new HashMap();
//        // probe最终表数据
//        List<Probe> probes = this.probeService.selectObjByMap(null);
//        if(probes.size() > 0){
//            List<Probe> uniqueDevices = probes.stream()
//                    .collect(Collectors.toMap(
//                            Probe::getIp_addr,
//                            probe -> probe,
//                            (existing, replacement) -> existing
//                    ))
//                    .values()
//                    .stream()
//                    .collect(Collectors.toList());
//            for (Probe probe : uniqueDevices) {
//                if(StringUtil.isNotEmpty(probe.getIp_addr())){
//                    params.clear();
//                    params.put("ip", probe.getIp_addr());
//                    List<Arp> arps = this.arpService.selectObjByMap(params);
//                    if(arps.size() > 0){
//                        Arp arp = arps.get(0);
//                        if(StringUtil.isNotEmpty(arp.getMac())){
//                            if(!MacUtils.getMac(arp.getMac()).equals("")){
//                                params.clear();
//                                params.put("mac", MacUtils.getMac(arp.getMac()));
//                                List<MacVendor> macVendorList = macVendorService.selectObjByMap(params);
//                                if(macVendorList.size() > 0){
//                                    MacVendor macVendor = macVendorList.get(0);
//                                    if(macVendor.getVendor() != null){
//                                        String vendor = macVendor.getVendor();
//                                        if(vendor.toLowerCase().contains("Tenda".toLowerCase())){
//                                            insertDeviceScan(probe, vendor, arp.getMac(), arp.getIpv6());
//                                        }else if(vendor.toLowerCase().toLowerCase().contains("h3c".toLowerCase())){
//                                            insertDeviceScan(probe, vendor, arp.getMac(), arp.getIpv6());
//                                        }else if(vendor.toLowerCase().toLowerCase().contains("TP-LINK".toLowerCase())){
//                                            insertDeviceScan(probe, vendor, arp.getMac(), arp.getIpv6());
//                                        }else if(vendor.toLowerCase().toLowerCase().contains("Lite-On".toLowerCase())){
//                                            insertDeviceScan(probe, vendor, arp.getMac(), arp.getIpv6());
//                                        }else if(vendor.toLowerCase().toLowerCase().contains("mercury".toLowerCase())){
//                                            insertDeviceScan(probe, vendor, arp.getMac(), arp.getIpv6());
//                                        }else if(vendor.toLowerCase().toLowerCase().contains("Huawei Device".toLowerCase())){
//                                            insertDeviceScan(probe, vendor, arp.getMac(), arp.getIpv6());
//                                        }else if(vendor.toLowerCase().contains("ruijie".toLowerCase())){
//                                            insertDeviceScan(probe, vendor, arp.getMac(), arp.getIpv6());
//                                        }else
//                                        if(vendor.toLowerCase().toLowerCase().contains("Espressif".toLowerCase())){
//                                            insertTerminal(probe, "Espressif");
//                                        }else if(vendor.toLowerCase().toLowerCase().contains("CLOUD".toLowerCase())){
//                                            insertTerminal(probe, "CLOUD");
//                                        }else if(vendor.toLowerCase().toLowerCase().contains("Intel".toLowerCase())){
//                                            insertTerminal(probe, "Intel");
//                                        }else if(vendor.toLowerCase().toLowerCase().contains("vivo".toLowerCase())){
//                                            insertTerminal(probe, "vivo");
//                                        }else if(vendor.toLowerCase().toLowerCase().contains("xiaomi".toLowerCase())){
//                                            insertTerminal(probe, "xiaomi");
//                                        }else if(vendor.toLowerCase().toLowerCase().contains("oppo".toLowerCase())){
//                                            insertTerminal(probe, "oppo");
//                                        }else if(vendor.toLowerCase().toLowerCase().contains("apple".toLowerCase())){
//                                            insertTerminal(probe, "apple");
//                                        }else if(vendor.toLowerCase().toLowerCase().contains("Honor".toLowerCase())){
//                                            insertTerminal(probe, "Honor");
//                                        }else if(vendor.toLowerCase().toLowerCase().contains("Chicony".toLowerCase())){
//                                            insertTerminal(probe, "Chicony");
//                                        }else if(vendor.toLowerCase().toLowerCase().contains("Liteon".toLowerCase())){
//                                            insertTerminal(probe, "Liteon");
//                                        }else if(vendor.toLowerCase().toLowerCase().contains("Lite-OnHUAWEI TECHNOLOGIES".toLowerCase())){
//                                            insertTerminal(probe, "Lite-OnHUAWEI TECHNOLOGIES");
//                                        }
//                                    }
//                                }
//                            }
//
//                        }
//                    }
//                }
//
//            }
//
//            //
////            this.unsureService.deleteTable();
////            List<Probe> finalProbe = this.probeService.selectObjByMap(null);
////            if(finalProbe.size() > 0){
////                for (Probe probe : finalProbe) {
////                    Unsure unsure = new Unsure();
////                    CopyPropertiesReflect.copyPropertiesExceptId(probe, unsure);
////
////                    if(StringUtil.isNotEmpty(probe.getIp_addr())){
////                        params.clear();
////                        params.put("ip", probe.getIp_addr());
////                        List<Arp> arps = this.arpService.selectObjByMap(params);
////                        if(arps.size() > 0){
////                            Arp arp = arps.get(0);
////                            if(StringUtil.isNotEmpty(arp.getMac())){
////                                params.clear();
////                                params.put("mac", MacUtils.getMac(arp.getMac()));
////                                List<MacVendor> macVendorList = macVendorService.selectObjByMap(params);
////                                if(macVendorList.size() > 0){
////                                    MacVendor macVendor = macVendorList.get(0);
////                                    unsure.setMac_vendor(macVendor.getVendor());
////                                }
////                            }
////                        }
////                    }
////                    this.unsureService.insert(unsure);
////                    this.probeService.delete(probe.getId());
////                }
////            }
//
//            this.probeUtils.insertTerminal();// 剩余probe写入终端
//
//        }
//
//        // arp表中只有ipv6地址没有ipv4地址的条目放入terminal_detail，os填mac_vendor的内容
//        params.clear();
//        List<Arp> arps = this.arpService.selectObjByMap(params);
//        if(arps.size() > 0){
//            for (Arp arp : arps) {
//                if(StringUtil.isEmpty(arp.getIp()) && StringUtil.isNotEmpty(arp.getIpv6())){
//                    insertTerminal(arp);
//                }
//            }
//        }
//
//    }
//
//
//    public void gatherUnsure() {
//        this.unsureService.deleteTable();
//        // probe最终表数据，需不需要写入unsure表，然后在清空最终probe
//        List<Probe> probes = this.probeService.selectObjByMap(null);
//        if(probes.size() > 0){
//            Map params = new HashMap();
//            for (Probe probe : probes) {
//                Unsure unsure = new Unsure();
//                CopyPropertiesReflect.copyPropertiesExceptId(probe, unsure);
//
//                if(StringUtil.isNotEmpty(probe.getIp_addr())){
//                    params.clear();
//                    params.put("ip", probe.getIp_addr());
//                    List<Arp> arps = this.arpService.selectObjByMap(params);
//                    if(arps.size() > 0){
//                        Arp arp = arps.get(0);
//                        if(StringUtil.isNotEmpty(arp.getMac())){
//                            if(!MacUtils.getMac(arp.getMac()).equals("")){
//                                params.clear();
//                                params.put("mac", MacUtils.getMac(arp.getMac()));
//                                List<MacVendor> macVendorList = macVendorService.selectObjByMap(params);
//                                if(macVendorList.size() > 0){
//                                    MacVendor macVendor = macVendorList.get(0);
//                                    unsure.setMac_vendor(macVendor.getVendor());
//                                }
//                            }
//
//                        }
//                    }
//                }
//
//                this.unsureService.insert(unsure);
//            }
//        }
//        this.probeService.deleteTable();
//
//        List<Unsure> unsures = this.unsureService.selectObjByMap(null);
//        if(unsures.size() > 0){
//            for (Unsure unsure : unsures) {
//                if(unsure.getMac_vendor() != null){
//                    if(unsure.getMac_vendor().toLowerCase().contains("Tenda".toLowerCase())){
//                        insertDeviceScan(unsure, "Tenda");
//                    }else if(unsure.getMac_vendor().toLowerCase().contains("h3c".toLowerCase())){
//                        insertDeviceScan(unsure, "h3c");
//                    }else if(unsure.getMac_vendor().toLowerCase().contains("TP-LINK".toLowerCase())){
//                        insertDeviceScan(unsure, "TP-LINK");
//                    }else if(unsure.getMac_vendor().toLowerCase().contains("Lite-On".toLowerCase())){
//                        insertDeviceScan(unsure, "Lite-On");
//                    }
//                }
//            }
//            unsures = this.unsureService.selectObjByMap(null);
//            for (Unsure unsure : unsures) {
//                if(unsure.getMac_vendor() != null){
//                    if(unsure.getMac_vendor().toLowerCase().contains("Espressif".toLowerCase())){
//                        insertTerminal(unsure, "Espressif");
//                    }else if(unsure.getMac_vendor().toLowerCase().contains("CLOUD".toLowerCase())){
//                        insertTerminal(unsure, "CLOUD");
//                    }else if(unsure.getMac_vendor().toLowerCase().contains("Intel".toLowerCase())){
//                        insertTerminal(unsure, "Intel");
//                    }else if(unsure.getMac_vendor().toLowerCase().contains("vivo".toLowerCase())){
//                        insertTerminal(unsure, "vivo");
//                    }else if(unsure.getMac_vendor().toLowerCase().contains("xiaomi".toLowerCase())){
//                        insertTerminal(unsure, "xiaomi");
//                    }else if(unsure.getMac_vendor().toLowerCase().contains("oppo".toLowerCase())){
//                        insertTerminal(unsure, "oppo");
//                    }else if(unsure.getMac_vendor().toLowerCase().contains("apple".toLowerCase())){
//                        insertTerminal(unsure, "apple");
//                    }else if(unsure.getMac_vendor().toLowerCase().contains("Honor".toLowerCase())){
//                        insertTerminal(unsure, "Honor");
//                    }else if(unsure.getMac_vendor().toLowerCase().contains("Chicony".toLowerCase())){
//                        insertTerminal(unsure, "Chicony");
//                    }
//                }
//            }
//        }
//        //
//
//        List<Unreach> unreaches = this.unreachService.selectObjByMap(null);
//        if(unreaches.size() > 0){
//            for (Unreach unreach : unreaches) {
//                if(unreach.getMac_vendor() != null){
//                    if(unreach.getMac_vendor().toLowerCase().contains("Tenda".toLowerCase())){
//                        insertDeviceScan(unreach, "Tenda");
//                    }else if(unreach.getMac_vendor().toLowerCase().contains("h3c".toLowerCase())){
//                        insertDeviceScan(unreach, "h3c");
//                    }else if(unreach.getMac_vendor().toLowerCase().contains("TP-LINK".toLowerCase())){
//                        insertDeviceScan(unreach, "TP-LINK");
//                    }else if(unreach.getMac_vendor().toLowerCase().contains("Lite-On".toLowerCase())){
//                        insertDeviceScan(unreach, "Lite-On");
//                    }
//                }
//            }
//            unreaches = this.unreachService.selectObjByMap(null);
//            for (Unreach unreach : unreaches) {
//                if(unreach.getMac_vendor() != null){
//                    if(unreach.getMac_vendor().toLowerCase().contains("Espressif".toLowerCase())){
//                        insertTerminal(unreach, "Espressif");
//                    }else if(unreach.getMac_vendor().toLowerCase().contains("CLOUD".toLowerCase())){
//                        insertTerminal(unreach, "CLOUD");
//                    }else if(unreach.getMac_vendor().toLowerCase().contains("Intel".toLowerCase())){
//                        insertTerminal(unreach, "Intel");
//                    }else if(unreach.getMac_vendor().toLowerCase().contains("vivo".toLowerCase())){
//                        insertTerminal(unreach, "vivo");
//                    }else if(unreach.getMac_vendor().toLowerCase().contains("xiaomi".toLowerCase())){
//                        insertTerminal(unreach, "xiaomi");
//                    }else if(unreach.getMac_vendor().toLowerCase().contains("oppo".toLowerCase())){
//                        insertTerminal(unreach, "oppo");
//                    }else if(unreach.getMac_vendor().toLowerCase().contains("apple".toLowerCase())){
//                        insertTerminal(unreach, "apple");
//                    }else if(unreach.getMac_vendor().toLowerCase().contains("Honor".toLowerCase())){
//                        insertTerminal(unreach, "Honor");
//                    }else if(unreach.getMac_vendor().toLowerCase().contains("Chicony".toLowerCase())){
//                        insertTerminal(unreach, "Chicony");
//                    }
//                }
//            }
//        }
//    }
//
//    public void insertDeviceScan(Probe probe, String macVendor, String mac, String ipv6){
//        DeviceScan deviceScan = new DeviceScan();
//        deviceScan.setDevice_ipv4(probe.getIp_addr());
//        deviceScan.setDevice_product(macVendor);
//        deviceScan.setMac(mac);
//        deviceScan.setMacVendor(macVendor);
//        deviceScan.setDevice_ipv6(ipv6);
//        try {
//            Map params = new HashMap();
//            params.put("device_ipv4", deviceScan.getDevice_ipv4());
//            List<DeviceScan> deviceScanList = this.deviceScanService.selectObjByMap(params);
//            if(deviceScanList.size() <= 0){
//                this.deviceScanService.insert(deviceScan);
//            }
//            this.probeService.delete(probe.getId());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void insertTerminal(Probe probe, String os){
//        Map params = new HashMap();
//        Terminal terminal = new Terminal();
//        terminal.setOs(os);
//        if(StringUtil.isNotEmpty(probe.getIp_addr())){
//            terminal.setIpv4addr(probe.getIp_addr());
//            params.clear();
//            params.put("likeIp", "%" + probe.getIp_addr() + "%");
//            List<Arp> arps = arpService.selectObjByMap(params);
//            if(arps.size() > 0){
//                Arp arp = arps.get(0);
//                terminal.setIpv6addr(arp.getIpv6());
//                terminal.setMac(arp.getMac());
//                terminal.setMacvendor(arp.getMacVendor());
//            }
//        }
//
//        params.clear();
//        params.put("ipv4addr", terminal.getIpv4addr());
//        List<Terminal> terminals = this.terminalService.selectObjByMap(params);
//        if(terminals.size() <= 0){
//
//            this.terminalService.insert(terminal);
//        }else{
//
//            this.terminalService.update(terminal);
//        }
//        this.probeService.delete(probe.getId());
//    }
//
//
//    public void insertTerminal(Arp arp){
//        Terminal terminal = new Terminal();
//        terminal.setIpv6addr(arp.getIpv6());
//        terminal.setMac(arp.getMac());
//        terminal.setMacvendor(arp.getMacVendor());
//        terminal.setOs(arp.getMacVendor());
//        this.terminalService.insert(terminal);
//    }
//
//    public void insertDeviceScan(Unsure instance, String os){
//        DeviceScan deviceScan = new DeviceScan();
//        deviceScan.setDevice_product(os);
//        deviceScan.setDevice_ipv4(instance.getIp_addr());
//        deviceScan.setMac(instance.getMac_addr());
//        deviceScan.setMacVendor(instance.getMac_vendor());
//        try {
//            Map params = new HashMap();
//            params.put("device_ipv4", instance.getIp_addr());
//            List<DeviceScan> deviceScanList = this.deviceScanService.selectObjByMap(params);
//            if(deviceScanList.size() <= 0){
//                this.deviceScanService.insert(deviceScan);
//            }
//            this.unsureService.delete(instance.getId());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void insertDeviceScan(Unreach instance, String os){
//        DeviceScan deviceScan = new DeviceScan();
//        deviceScan.setDevice_product(os);
//        deviceScan.setDevice_ipv4(instance.getIp_addr());
//        deviceScan.setMac(instance.getMac_addr());
//        deviceScan.setMacVendor(instance.getMac_vendor());
//        try {
//            Map params = new HashMap();
//            params.put("device_ipv4", instance.getIp_addr());
//            List<DeviceScan> deviceScanList = this.deviceScanService.selectObjByMap(params);
//            if(deviceScanList.size() <= 0){
//                this.deviceScanService.insert(deviceScan);
//            }
//            this.unreachService.delete(instance.getId());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void insertTerminal(Unsure instance, String os){
//        Map params = new HashMap();
//        Terminal terminal = new Terminal();
//        terminal.setOs(os);
//        terminal.setIpv4addr(instance.getIp_addr());
//        if(StringUtil.isNotEmpty(instance.getIp_addr())){
//            params.clear();
//            params.put("ip", instance.getIp_addr());
//            List<Arp> arps = arpService.selectObjByMap(params);
//            if(arps.size() > 0){
//                Arp arp = arps.get(0);
//                terminal.setIpv6addr(arp.getIpv6());
//                terminal.setMac(arp.getMac());
//                terminal.setMacvendor(arp.getMacVendor());
//            }
//        }
//        params.clear();
//        params.put("ipv4addr", terminal.getIpv4addr());
//        List<Terminal> terminals = this.terminalService.selectObjByMap(params);
//        if(terminals.size() <= 0){
//
//            this.terminalService.insert(terminal);
//        }else{
//
//            this.terminalService.update(terminal);
//        }
//
//        this.unreachService.delete(instance.getId());
//    }
//
//    public void insertTerminal(Unreach instance, String os){
//        Map params = new HashMap();
//        Terminal terminal = new Terminal();
//        terminal.setOs(os);
//        terminal.setIpv4addr(instance.getIp_addr());
//        if(StringUtil.isNotEmpty(instance.getIp_addr())){
//            params.clear();
//            params.put("ip", instance.getIp_addr());
//            List<Arp> arps = arpService.selectObjByMap(params);
//            if(arps.size() > 0){
//                Arp arp = arps.get(0);
//                terminal.setIpv6addr(arp.getIpv6());
//                terminal.setMac(arp.getMac());
//                terminal.setMacvendor(arp.getMacVendor());
//            }
//            params.clear();
//            params.put("ipv4addr", terminal.getIpv4addr());
//            List<Terminal> terminals = this.terminalService.selectObjByMap(params);
//            if(terminals.size() <= 0){
//
//                this.terminalService.insert(terminal);
//            }else{
//
//                this.terminalService.update(terminal);
//            }
//        }
//        this.unreachService.delete(instance.getId());
//    }
//
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
//    public void gatherIpv6(){
//        try {
//
//            String beginTime = DateTools.getCreateTime();
//            this.careateSureyingLog("ipv6网段分析", beginTime, 1);
//
//            GatherFactory factory = new GatherFactory();
//            Gather gather = factory.getGather(Global.IPV6_PANABIT);
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
//
//    public void portIpv4AndIpv6(){
//        try {
//            // port ipv4/ipv6 采集开始时间
//
////        this.careateSureyingLog("ipv4网段分析", beginTime, 1);
//
//            GatherFactory factory = new GatherFactory();
//            Gather gather = factory.getGather(Global.PY_SUFFIX_ALIVEINT);
//            gather.executeMethod();
//
//            // ipv4/ipv6 网段梳理
//            this.subnetService.comb();
//
//            // port ipv4/ipv6 采集结束时间
//
//            // 梳理结果，存入采集日志
//
//            String endTime = DateTools.getCreateTime();
//
//            this.updateSureyingLog("ipv4网段分析", endTime, 2);
//
//            String beginTime = DateTools.getCreateTime();
//            this.careateSureyingLog("ipv6网段分析", beginTime, 1);
//
//            // 休眠时间
//            try {
//                Thread.sleep(10000 + new Random().nextInt(5000));
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            endTime = DateTools.getCreateTime();
//            this.updateSureyingLog("ipv6网段分析", endTime, 2);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void gatherPing(){
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
//    public void gatherArp(){
//        try {
//            GatherFactory factory = new GatherFactory();
//            Gather gather = factory.getGather(Global.ARP);
//            gather.executeMethod();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void gatherSwitch(){
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
//    public void gatherFirewall(){
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
//    public void gatherDeviceScan(){
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.DEVICE_SCAN);
//        gather.executeMethod();
//    }
//
//    public void gatherTerminal(){
//        try {
//            String beginTime = DateTools.getCreateTime();
//
//            this.careateSureyingLog("终端分析", beginTime, 1);
//
//            this.gatherDeviceScan();// metoo_scan
//
//            GatherFactory factory = new GatherFactory();
//            Gather gather = factory.getGather(Global.TERMINAL);
//            gather.executeMethod();
//
//            this.finalProbe();
//
//            String endTime = DateTools.getCreateTime();
//
//            this.updateSureyingLog("终端分析", endTime, 2);
//        } catch (Exception e) {
//            e.printStackTrace();
//            String endTime = DateTools.getCreateTime();
//            this.updateSureyingLog("终端分析", endTime, 3);
//        }
//    }
//
//    public void gatherUploadData(String data){
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
//    public static String getDate(){
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String date = simpleDateFormat.format(new Date());
//        return date;
//    }
//
//    public static void main(String[] args) {
//        List<Integer> list = new ArrayList<>();
//        // 假设list已经填充了数据
//        for (int i = 0; i < 1100; i++) {
//            list.add(i);
//        }
//
//        int batchSize = 100;
//        int totalSize = list.size();
//
//        for (int i = 0; i < totalSize; i += batchSize) {
//            int end = Math.min(totalSize, i + batchSize);
//            List<Integer> subList = list.subList(i, end);
//
//            // 处理数据
//            processBatch(subList);
//        }
//    }
//
//    private static void processBatch(List<Integer> batch) {
//        // 处理每个批次的数据
//        System.out.println("Processing batch: " + batch);
//    }
//
//    public String getProbeResult(){
//
//        String begin_time = DateTools.getCreateTime();
//
//        this.careateSureyingLog("全网资产扫描", begin_time, 1);
//
//        try {
//            //arp表里的所有ip
//            List<Arp> arps = this.arpService.selectObjByMap(null);
//            if(arps.size() > 0){
//                this.callChuangfaVersion2(arps);
//                return null;
//            }else{
//                String endTime = DateTools.getCreateTime();
//                this.updateSureyingLog("全网资产扫描", endTime, 2);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            // 这里记录日志，不用返回null判断
//            String endTime = DateTools.getCreateTime();
//            this.updateSureyingLog("全网资产扫描", endTime, 3);
//        }
//        return null;
//    }
//
//
//    public void callChuangfa(String ipAddresses){
//        JsonRequest jsonRequest = new JsonRequest();
//
//        jsonRequest.setTaskuuid(UUID.randomUUID().toString());
//
////                jsonRequest.setPort("1-10000");
//
//        jsonRequest.setThread("600");
//
//        jsonRequest.setTimeout("300000");
//
//        // 将IP地址列表组合成逗号分隔的字符串
//        String ipAddressString = String.join(",", ipAddresses);
//        jsonRequest.setIp(ipAddressString);
//        String result = apiService.callThirdPartyApi(apUrl, jsonRequest);
//
//        if(result != null){
//            ProbeResult probeResult = this.probeResultService.selectObjByOne();
//            this.probeWait(result, probeResult);
//        }
//    }
//
//    public void probeWait(String result, ProbeResult probeResult){
//
//        JSONObject json = JSONObject.parseObject(result);
//
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
//
//        String endTime = DateTools.getCreateTime();
//        this.updateSureyingLog("全网资产扫描", endTime, 2);
//    }
//
//
//    public void callChuangfaVersion2(List<Arp> arpList){
//        ProbeResult probeResult = this.probeResultService.selectObjByOne();
//        if(arpList.size() <= 500){
//            List<String> ipAddresses = arpList.stream()
//                    .map(Arp::getIp) // 提取IP地址
//                    .filter(ip -> ip != null && !ip.isEmpty()) // 过滤掉空的IP地址
//                    .collect(Collectors.toList()); // 收集成列表
//            String ipAddressString = String.join(",", ipAddresses);
//            this.callChuangfa(ipAddressString);
//        }else{
//
//            // 创建固定大小的线程池
//            ExecutorService executor = Executors.newFixedThreadPool(10);
//
//            List<Future<Integer>> futures = new ArrayList<>();
//
//            int batchSize = 100;
//            int totalSize = arpList.size();
//            for (int i = 0; i < totalSize; i += batchSize) {
//                int start = i;
//                int end = Math.min(totalSize, i + batchSize);
//
//                // 提交并发任务
//                Future<Integer> future = executor.submit(() -> {
//                    List<Arp> subList = arpList.subList(start, end);
//
//                    // 处理数据
//                    List<String> ipAddresses = subList.stream()
//                            .map(Arp::getIp)
//                            .filter(ip -> ip != null && !ip.isEmpty())
//                            .collect(Collectors.toList());
//
//                    String ipAddressString = String.join(",", ipAddresses);
//
//                    // 调用远程服务并返回结果
//                    return callChuangfaVersion2(ipAddressString);
//                });
//
//                futures.add(future);
//
//            }
//
//            int number = 0;
//            // 等待所有任务完成，并累加结果
//            for (Future<Integer> future : futures) {
//                Integer result = null;  // 阻塞，等待任务完成
//                try {
//                    result = future.get();
//                    if (result == 1) {
//                        number += result;
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            // 关闭线程池
//            executor.shutdown();
//
//            // 查询这个回调
//            Map params = new HashMap();
//            params.clear();
//            params.put("result", probeResult.getResult() + number);
//            List<ProbeResult> obj = this.probeResultService.selectObjByMap(params);
//            if(obj.size() > 0){
//
//                try {
//                    GatherFactory factory = new GatherFactory();
//                    Gather gather = factory.getGather("fileToProbe");
//                    gather.executeMethod();
//
//                    this.probeService.deleteTableBack();
//                    this.probeService.copyToBck();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                List list = this.probeService.selectObjByMap(null);
//                log.info("chuangfa + os_scan========================================：" + JSONObject.toJSONString(list));
//
//            }
//        }
//
//    }
//
//    public Integer callChuangfaVersion2(String ipAddresses){
//        try {
//            JsonRequest jsonRequest = new JsonRequest();
//
//            jsonRequest.setTaskuuid(UUID.randomUUID().toString());
//
////                jsonRequest.setPort("1-10000");
//
//            jsonRequest.setThread("600");
//
//            jsonRequest.setTimeout("300000");
//
//            // 将IP地址列表组合成逗号分隔的字符串
//            String ipAddressString = String.join(",", ipAddresses);
//            jsonRequest.setIp(ipAddressString);
//            String result = apiService.callThirdPartyApi(apUrl, jsonRequest);
//
//            if(result != null){
//                JSONObject json = JSONObject.parseObject(result);
//
//                if(json.getInteger("code") == 0){
//                    return 1;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//        return 0;
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
//    @GetMapping("/ipv4/panabit")
//    public void ipv4_panabit() {
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.IPV4_PANABIT);
//        gather.executeMethod();
//    }
//
//    @GetMapping("/ipv6/panabit")
//    public void ipv6_panabit() {
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather(Global.IPV6_PANABIT);
//        gather.executeMethod();
//    }
//
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
//    @GetMapping("/fileToProbe2")
//    public void fileToProbe2() {
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather("fileToProbe2");
//        gather.executeMethod();
//    }
//
//    @GetMapping("/fileToProbe")
//    public void fileToProbe() {
//        GatherFactory factory = new GatherFactory();
//        Gather gather = factory.getGather("fileToProbe");
//        gather.executeMethod();
//    }
//
//
//    @GetMapping("/unsure")
//    public void unsure() {
//        this.unsureService.deleteTable();
//        // probe最终表数据，需不需要写入unsure表，然后在清空最终probe
//        List<Probe> probes = this.probeService.selectObjByMap(null);
//        if(probes.size() > 0){
//            for (Probe probe : probes) {
//                Unsure unsure = new Unsure();
//                CopyPropertiesReflect.copyPropertiesExceptId(probe, unsure);
//                this.unsureService.insert(unsure);
//            }
//        }
//        this.probeService.deleteTable();
//    }
//
//
//
//    @GetMapping("gatherUnsure")
//    public void gatherUnsureTest(){
//        this.gatherUnsure();
//    }
//
//    @GetMapping("finalProbe")
//    public void finalProbew(){
//        this.gatherTerminal();
//        this.finalProbe();
//    }
//
//}
