package com.metoo.sqlite.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.*;
import com.metoo.sqlite.gather.factory.gather.thread.Gather;
import com.metoo.sqlite.gather.factory.gather.thread.GatherFactory;
import com.metoo.sqlite.gather.self.SelfTerminalUtils;
import com.metoo.sqlite.manager.api.ApiService;
import com.metoo.sqlite.manager.api.JsonRequest;
import com.metoo.sqlite.manager.utils.gather.ProbeToTerminalAndDeviceScan;
import com.metoo.sqlite.manager.utils.gather.VerifyVendorUtils;
import com.metoo.sqlite.manager.utils.jx.JXDataUtils;
import com.metoo.sqlite.service.*;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 测绘采集服务-新版本
 *
 * @author zzy
 * @version 1.0
 * @date 2024/10/5 11:32
 */
@Service
@Slf4j
public class GatherAllInOneService {
    @Autowired
    private ApiService apiService;
    @Autowired
    private ISubnetService subnetService;
    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private ISurveyingLogService surveyingLogService;

    @Autowired
    private IProbeService probeService;
    @Autowired
    private IArpService arpService;
    private final ReentrantLock lock = new ReentrantLock();
    @Value("${AP.URL}")
    private String apUrl;
    @Autowired
    private IProbeResultService probeResultService;
    @Autowired
    private ProbeToTerminalAndDeviceScan probeToTerminalAndDeviceScan;
    @Autowired
    private VerifyVendorUtils verifyVendorUtils;

    @Autowired
    private JXDataUtils jxDataUtils;

    @Autowired
    private ISubnetIpv6Service subnetIpv6Service;

    @Autowired
    private IGatherLogService gatherLogService;
    @Autowired
    private IGatewayInfoService gatewayInfoService;
    @Autowired
    private ITerminalService terminalService;
    @Autowired
    private GatherAllInOneExecuteService executeService;
    @Autowired
    private SelfTerminalUtils selfTerminalUtils;

    /**
     * 测绘主逻辑
     *
     * @param type
     * @return
     */
    public Result startGather(Integer type) {
        if (!lock.tryLock()) {
            return ResponseUtil.ok(1002, "正在测绘");
        }
        try {
            if (type != null && type == 1) {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
                return ResponseUtil.ok(1001);
            }
            List<Device> devices = deviceService.selectObjByMap(null);
            if (devices.size() < 1) {
                return ResponseUtil.ok(1003, "请先添加设备");
            }
            boolean flag = checkDeviceVendor();
            // 清空测绘日志
            clearLogs();
            if (flag) {
                runSelfTerminalUtils();
                return ResponseUtil.ok("测绘完成");
            }
            // cf-scanner环境判断
            this.careateSureyingLog("采集模块检测", DateTools.getCreateTime(), 1);
            if (!existCFScannerFile()) {
                this.updateSureyingLog("采集模块检测", DateTools.getCreateTime(), 3);
                return ResponseUtil.error("采集模块出现问题");
            } else {
                this.updateSureyingLog("采集模块检测", DateTools.getCreateTime(), 2);
            }
            // os-scanner环境判断
            this.careateSureyingLog("扫描模块检测", DateTools.getCreateTime(), 1);
            if (!existOSScannerFile()) {
                this.updateSureyingLog("扫描模块检测", DateTools.getCreateTime(), 3);
                return ResponseUtil.error("扫描模块出现问题");
            } else {
                this.updateSureyingLog("扫描模块检测", DateTools.getCreateTime(), 2);
            }
            // elk服务启动
            String beginTime = DateTools.getCreateTime();
            // 调用main.pyc执行脚本
            this.careateSureyingLog("ipv4网段分析", beginTime, 1);
            //对json结果文件进行解析入库
            try {
                // 调用arp，ipv6 neighbors，alivein 采集
                executeService.callMainExecute();
                gatherIPv4();
            } catch (Exception e) {
                log.error("ipv4网段分析 出现错误：{}", e);
                String endTime = DateTools.getCreateTime();
                this.updateSureyingLog("ipv4网段分析", endTime, 3);
            }
            gatherIPv6();
            gatherArp();
            //gatherPing();
            getProbeResult();
            gatherSwitch();
            gatherTerminal();
            String endTime = DateTools.getCreateTime();
            String data = jxDataUtils.getEncryptedData();
            gatherUploadData(data);

            String surveying = getSurveyingResult();
            logGatheringResults(beginTime, endTime, data, surveying);
            return ResponseUtil.ok("测绘完成");

        } catch (Exception e) {
            log.error("测绘失败：{}", e);
            return ResponseUtil.error("测绘失败");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void runSelfTerminalUtils() {
        try {
            selfTerminalUtils.main();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkDeviceVendor() {
        List<Device> devices = deviceService.selectObjByMap(null);
        return devices.stream().anyMatch(device -> device.getDeviceVendorSequence() == 1);
    }

    private void clearLogs() {
        try {
            surveyingLogService.deleteTable();
            gatherLogService.deleteTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logGatheringResults(String beginTime, String endTime, String data, String surveying) {
        try {
            GatherLog gatherLog = new GatherLog();
            gatherLog.setCreateTime(beginTime);
            gatherLog.setBeginTime(beginTime);
            gatherLog.setEndTime(endTime);
            gatherLog.setType("手动");
            gatherLog.setResult("成功");
            gatherLog.setDetails(surveying);
            gatherLog.setData(data);
            gatherLogService.insert(gatherLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    // 获取测绘结果，采集格式化后的数据
    public String getSurveyingResult() {
        Map details = new HashMap();
        try {
            details.put("subme_ipv4", this.subnet_ipv4());
            details.put("subme_ipv6", this.subnet_ipv6());
            List<GatewayInfo> gatewayInfoList = this.gatewayInfoService.selectObjByMap(null);
            details.put("gatewayInfo", gatewayInfoList);
            Map params = new HashMap();
            params.clear();
            params.put("ipv4IsNotNull", "ipv4addr");
            List<Terminal> terminal_ipv4 = this.terminalService.selectObjByMap(params);
            params.clear();
            params.put("ipv4AndIpv6IsNotNull", "ipv6addr");
            List<Terminal> teminal_ipv6 = this.terminalService.selectObjByMap(params);
            details.put("ipv4", terminal_ipv4.size());
            details.put("ipv4_ipv6", teminal_ipv6.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONObject.toJSONString(details);
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

    public void gatherUploadData(String data) {
        String beginTime = DateTools.getCreateTime();
        this.careateSureyingLog("生成加密结果文件", beginTime, 1);

        try {
            Thread.sleep(1);//0000
            String endTime = DateTools.getCreateTime();
            this.updateSureyingLog("生成加密结果文件", endTime, 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public String getProbeResult() {
        log.info("Probe start===============");
        String begin_time = DateTools.getCreateTime();
        this.careateSureyingLog("全网资产扫描", begin_time, 1);
        try {
            List<Arp> arpList = this.arpService.selectObjByMap(null);
            if (arpList.isEmpty()) {
                String endTime = DateTools.getCreateTime();
                this.updateSureyingLog("全网资产扫描", endTime, 2);
                return null;
            }
            if (arpList.size() >= 200) {
                processInBatches(arpList);
            } else {
                processSingleBatch(arpList);
            }
            log.info("Probe end===============");
            return null;
        } catch (Exception e) {
            String endTime = DateTools.getCreateTime();
            this.updateSureyingLog("全网资产扫描", endTime, 3);
        }
        log.info("Probe end===============");
        return null;
    }

    private void processInBatches(List<Arp> arpList) {
        int batchSize = 100;
        for (int i = 0; i < arpList.size(); i += batchSize) {
            List<Arp> subList = arpList.subList(i, Math.min(arpList.size(), i + batchSize));
            String ipAddressString = extractIpAddresses(subList);
            if (StringUtil.isNotEmpty(ipAddressString)) {
                callChuangfa(ipAddressString);
            }
        }
        executeGatherAndBackup();
    }

    private void executeGatherAndBackup() {
        try {
            Gather gather = new GatherFactory().getGather("fileToProbe");
            gather.executeMethod();
            this.probeService.deleteTableBack();
            this.probeService.copyToBck();
            List<Probe> list = this.probeService.selectObjByMap(null);
            log.info("Chuangfa os-scanner " + JSONObject.toJSONString(list));
        } catch (Exception e) {
            log.error("Error during gather and backup", e);
        }
    }

    private String extractIpAddresses(List<Arp> arpList) {
        String ipStr = arpList.stream().filter(temp -> StrUtil.isNotEmpty(temp.getIp()))
                .map(Arp::getIp)
                .collect(Collectors.joining(","));
        String ipv6Str = arpList.stream().filter(temp -> StrUtil.isEmpty(temp.getIp()) && StrUtil.isNotEmpty(temp.getIpv6()))
                .map(Arp::getIpv6)
                .collect(Collectors.joining(","));
        if (StrUtil.isEmpty(ipStr)) {
            return ipv6Str;
        } else if (StrUtil.isEmpty(ipv6Str)) {
            return ipStr;
        } else {
            return ipStr + "," + ipv6Str;
        }
    }

    public void callChuangfa(String ipAddresses) {
        log.info("Ipaddress================" + ipAddresses);
        JsonRequest jsonRequest = new JsonRequest();

        jsonRequest.setTaskuuid(UUID.randomUUID().toString());

        jsonRequest.setThread("600");

        jsonRequest.setTimeout("300");

        jsonRequest.setIp(ipAddresses);

        String result = apiService.callThirdPartyApi(apUrl, jsonRequest);

        if (result != null) {
            ProbeResult probeResult = this.probeResultService.selectObjByOne();
            this.probeWait(result, probeResult);
        }
    }

    private void processSingleBatch(List<Arp> arpList) {
        String ipAddressString = extractIpAddresses(arpList);
        if (StringUtil.isNotEmpty(ipAddressString)) {
            boolean flag = this.callChuangfaSingle(ipAddressString);
            String endTime = DateTools.getCreateTime();
            this.updateSureyingLog("全网资产扫描", endTime, flag ? 2 : 3);
        }
    }

    public void gatherSwitch() {

        try {
            String beginTime = DateTools.getCreateTime();

            this.careateSureyingLog("网络设备分析", beginTime, 1);

            // GatherFactory factory = new GatherFactory();

            // executeGatherTask(factory, Global.PY_SUFFIX_GET_SWITCH);

            String endTime = DateTools.getCreateTime();

            this.updateSureyingLog("网络设备分析", endTime, 2);
        } catch (Exception e) {
            e.printStackTrace();
            String endTime = DateTools.getCreateTime();
            this.updateSureyingLog("网络设备分析", endTime, 3);
        }
    }

    public void gatherIPv4() {
        // 梳理结果，存入采集日志
        String endTime = DateTools.getCreateTime();
        // ipv4 网段梳理
        this.subnetService.comb();
        this.updateSureyingLog("ipv4网段分析", endTime, 2);

    }

    public void gatherIPv6() {
        try {

            String beginTime = DateTools.getCreateTime();
            this.careateSureyingLog("ipv6网段分析", beginTime, 1);
            GatherFactory factory = new GatherFactory();
            Gather gather = factory.getGather(Global.SUBNET_IPV6);
            gather.executeMethod();
            String endTime = DateTools.getCreateTime();
            this.updateSureyingLog("ipv6网段分析", endTime, 2);

        } catch (Exception e) {
            e.printStackTrace();
            String endTime = DateTools.getCreateTime();
            this.updateSureyingLog("ipv6网段分析", endTime, 3);
        }
    }

    public void gatherArp() {
        try {
            GatherFactory factory = new GatherFactory();
            Gather gather = factory.getGather(Global.ARP);
            gather.executeMethod();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void careateSureyingLog(String name, String beginTime, Integer status) {
        SurveyingLog surveyingLog = new SurveyingLog().createTime(DateTools.getCreateTime()).name(name).beginTime(beginTime).status(status);
        this.surveyingLogService.insert(surveyingLog);
    }

    public void updateSureyingLog(String name, String endTime, Integer status) {

        try {
            Thread.sleep(10000); // 手动设置测绘休眠
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        endTime = DateTools.getCreateTime();

        Map params = new HashMap();
        params.put("name", name);
        List<SurveyingLog> surveyingLogs = this.surveyingLogService.selectObjByMap(params);
        if (surveyingLogs.size() > 0) {
            SurveyingLog surveyingLog = surveyingLogs.get(0).endTime(endTime).status(status);
            this.surveyingLogService.update(surveyingLog);
        }
        try {
            Thread.sleep(200); // 手动设置测绘休眠
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 4.点击开始测绘时，对cf-scanner，os-scanner文件是否存在进行判断，文件不存在，则测绘进程终止，并前端提示
     *
     * @return
     */
    public boolean existCFScannerFile() {
        String directoryPath = Global.cf_scanner;
        String fileName = Global.cf_scanner_name;
        // 构建完整的文件路径
        File directory = new File(directoryPath);
        File file = new File(directory, fileName.replace("./", ""));
        // 检查文件是否存在
        if (file.exists() && file.isFile() && (fileName.contains(".exe") ? file.canExecute() : true)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean existOSScannerFile() {
        String directoryPath = Global.os_scanner + "1";
        String fileName = Global.os_scanner_name;
        // 构建完整的文件路径
        File directory = new File(directoryPath);
        File file = new File(directory, fileName.replace("./", ""));
        // 检查文件是否存在
        if (file.exists() && file.isFile() && (fileName.contains(".exe") ? file.canExecute() : true)) {
            return true;
        } else {
            return false;
        }
    }

    public void probeWait(String result, ProbeResult probeResult) {

        JSONObject json = JSONObject.parseObject(result);

        if (json.getInteger("code") == 0) {
            // 等待
            Map params = new HashMap();
            while (true) {

                try {
                    Thread.sleep(5000);
                    log.info("wait......");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                params.clear();
                params.put("result", probeResult.getResult() + 1);
                List<ProbeResult> obj = this.probeResultService.selectObjByMap(params);
                if (obj.size() > 0) {
                    break;
                }
            }
        }
    }

    public boolean callChuangfaSingle(String ipAddresses) {
        log.info("Ipaddress================" + ipAddresses);
        try {
            JsonRequest jsonRequest = new JsonRequest();

            jsonRequest.setTaskuuid(UUID.randomUUID().toString());

            jsonRequest.setThread("600");

            jsonRequest.setTimeout("3000");

            jsonRequest.setIp(ipAddresses);

            String result = apiService.callThirdPartyApi(apUrl, jsonRequest);

            if (result != null) {
                ProbeResult probeResult = this.probeResultService.selectObjByOne();
                try {
                    this.probeWaitSingle(result, probeResult);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public void probeWaitSingle(String result, ProbeResult probeResult) {

        JSONObject json = JSONObject.parseObject(result);

        if (json.getInteger("code") == 0) {
            // 等待
            Map params = new HashMap();
            while (true) {

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                params.clear();
                params.put("result", probeResult.getResult() + 1);
                List<ProbeResult> obj = this.probeResultService.selectObjByMap(params);
                if (obj.size() > 0) {

                    try {
                        GatherFactory factory = new GatherFactory();
                        Gather gather = factory.getGather("fileToProbe");
                        gather.executeMethod();

                        this.probeService.deleteTableBack();
                        this.probeService.copyToBck();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    List list = this.probeService.selectObjByMap(null);
                    log.info("chuangfa + os_scan========================================：" + JSONObject.toJSONString(list));

                    break;
                }
            }
        }

    }

    public void gatherDeviceScan() throws Exception {
        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather(Global.DEVICE_SCAN);
        gather.executeMethod();
    }

    public void gatherTerminal() {
        try {
            String beginTime = DateTools.getCreateTime();

            this.careateSureyingLog("终端分析", beginTime, 1);

            //this.gatherArp();

            this.gatherDeviceScan();

            GatherFactory factory = new GatherFactory();
            Gather gather = factory.getGather(Global.TERMINAL);
            gather.executeMethod();
            this.probeToTerminalAndDeviceScan.finalProbe();
            this.verifyVendorUtils.finalTerminal();
            String endTime = DateTools.getCreateTime();
            this.updateSureyingLog("终端分析", endTime, 2);
        } catch (Exception e) {
            e.printStackTrace();
            String endTime = DateTools.getCreateTime();
            this.updateSureyingLog("终端分析", endTime, 3);
        }
    }

    /**
     * 异步开启elk
     *
     * @return
     */
    public boolean startELK() {
        // TODO: 2024/10/7 开启elk服务
        return false;
    }

    /**
     * 停止elk服务
     */
    public void stopELK() {
        // TODO: 2024/10/7 停止elk服务 
    }
}
