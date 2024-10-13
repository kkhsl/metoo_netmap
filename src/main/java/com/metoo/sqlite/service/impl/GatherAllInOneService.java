package com.metoo.sqlite.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.enums.LogStatusType;
import com.metoo.sqlite.dto.SessionInfoDto;
import com.metoo.sqlite.entity.*;
import com.metoo.sqlite.gather.common.GatherCacheManager;
import com.metoo.sqlite.gather.factory.gather.thread.Gather;
import com.metoo.sqlite.gather.factory.gather.thread.GatherFactory;
import com.metoo.sqlite.gather.self.SelfTerminalUtils;
import com.metoo.sqlite.manager.api.ApiService;
import com.metoo.sqlite.manager.api.JsonRequest;
import com.metoo.sqlite.manager.utils.gather.ProbeToTerminalAndDeviceScan;
import com.metoo.sqlite.manager.utils.gather.VerifyVendorUtils;
import com.metoo.sqlite.manager.utils.jx.JXDataUtils;
import com.metoo.sqlite.model.es.EsQueryService;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
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
    private PublicService publicService;

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
    private IGatewayInfoService gatewayInfoService;
    @Autowired
    private ITerminalService terminalService;
    @Autowired
    private GatherAllInOneExecuteService executeService;
    @Autowired
    private SelfTerminalUtils selfTerminalUtils;

    @Autowired
    private EsQueryService esQuery;
    private Future<?> runningTask = null;

    private ExecutorService executorService;

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
            if (runningTask != null && !runningTask.isDone()) {
                log.error("当前测绘任务正在进行");
                return ResponseUtil.ok(1002, "正在测绘");
            }
            executorService = ThreadUtil.newSingleExecutor();
            Callable<Void> task = () -> {
                if (Thread.currentThread().isInterrupted()) {
                    log.error("任务中断");
                    return null;
                }
                try {
                    gatherMethod();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // 保留中断状态
                    log.error("具体任务中断");
                    return null;
                }
                return null;
            };
            runningTask = executorService.submit(task);
            try {
                // 等待任务执行完毕
                runningTask.get();
                log.info("测绘任务完成情况========");
            } catch (InterruptedException | ExecutionException e) {
                // 处理可能的异常
                log.error("等待测绘任务执行完毕出错：{}", e);
                // 如果被中断，则保留中断状态
                Thread.currentThread().interrupt();
                return ResponseUtil.error("测绘已终止");
            }
            return ResponseUtil.ok("测绘完成");
        } catch (Exception e) {
            log.error("测绘失败：{}", e);
            return ResponseUtil.error("测绘失败");
        } finally {
            // 清除引用，以便可以提交新的任务
            runningTask = null;
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
            GatherCacheManager.running = true;
            executorService.shutdown();

        }
    }

    /**
     * 测绘任务方法
     *
     * @throws Exception
     */
    private void gatherMethod() throws Exception {
        // boolean flag = checkDeviceVendor();
        // 清空测绘日志
        publicService.clearLogs();
//        if (flag) {
//            runSelfTerminalUtils();
//        }else {
        // cf-scanner环境判断
        int cjLogId = publicService.createSureyingLog("采集模块检测", DateTools.getCreateTime(), LogStatusType.init.getCode(), null);
        if (!existCFScannerFile()) {
            publicService.updateSureyingLog(cjLogId, LogStatusType.FAIL.getCode());
            throw new Exception("采集模块检测出错");
        } else {
            publicService.updateSureyingLog(cjLogId, LogStatusType.SUCCESS.getCode());
        }
        // os-scanner环境判断
        int scLogId = publicService.createSureyingLog("扫描模块检测", DateTools.getCreateTime(), LogStatusType.init.getCode(), null);
        if (!existOSScannerFile()) {
            publicService.updateSureyingLog(scLogId, LogStatusType.FAIL.getCode());
            throw new Exception("扫描模块检测出错");
        } else {
            publicService.updateSureyingLog(scLogId, LogStatusType.SUCCESS.getCode());
        }
        // TODO: elk设备启动
        // 启动日志模块日志
        String beginTime = DateTools.getCreateTime();
        // 调用main.pyc执行脚本
        int logId = publicService.createSureyingLog("启动日志模块", beginTime, LogStatusType.init.getCode(), null);
        //对json结果文件进行解析入库
        try {
            // 调用arp，ipv6 neighbors，alivein 采集
            executeService.callMainExecute(logId);
            gatherIPv4();
        } catch (Exception e) {
            log.error("启动日志模块 出现错误：{}", e);
            publicService.updateSureyingLog(logId, LogStatusType.FAIL.getCode());
        }
        // 启动日志成功
        publicService.updateSureyingLog(logId, LogStatusType.SUCCESS.getCode());
        gatherIPv6();
        gatherArp();
        //gatherPing();
        getProbeResult();
        //gatherSwitch();
        gatherTerminal();
        if(!GatherCacheManager.running) {
            throw new RuntimeException("测绘已手动中止");
        }
        String endTime = DateTools.getCreateTime();
        String data = jxDataUtils.getEncryptedData();
        gatherUploadData(data);
        String surveying = getSurveyingResult();
        publicService.logGatheringResults(beginTime, endTime, data, surveying);
        //  }
    }

    /**
     * 手动停止测绘
     *
     * @return
     */
    public boolean stopGather() {
        if (runningTask != null && !runningTask.isDone()) {
            // 表示如果必要则中断正在运行的线程
            runningTask.cancel(true);
            // 清除引用，以便可以提交新的任务
            runningTask = null;

        }
        executorService.shutdownNow();
        GatherCacheManager.running = false;
        return true;
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
            // 补充es查询后数据
            SessionInfoDto SessionInfoDto = esQuery.querySessionInfo();
            details.put("sessionInfo", SessionInfoDto);
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
        int ecLogId = publicService.createSureyingLog("生成加密结果文件", beginTime, 1, null);
        publicService.updateSureyingLog(ecLogId, 2);

    }

    public String getProbeResult() {
        log.info("Probe start===============");
        String begin_time = DateTools.getCreateTime();
        int probeLogId = publicService.createSureyingLog("全网资产扫描", begin_time, 1, null);
        try {
            List<Arp> arpList = this.arpService.selectObjByMap(null);
            if (arpList.isEmpty()) {
                publicService.updateSureyingLog(probeLogId, 2);
                return null;
            }
            if (arpList.size() >= 200) {
                processInBatches(arpList);
            } else {
                processSingleBatch(arpList, probeLogId);
            }
            log.info("Probe end===============");
            return null;
        } catch (Exception e) {
            publicService.updateSureyingLog(probeLogId, 3);
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

    private void processSingleBatch(List<Arp> arpList, int probeLogId) {
        String ipAddressString = extractIpAddresses(arpList);
        if (StringUtil.isNotEmpty(ipAddressString)) {
            boolean flag = this.callChuangfaSingle(ipAddressString);
            publicService.updateSureyingLog(probeLogId, flag ? 2 : 3);
        }
    }
//
//    public void gatherSwitch() {
//
//        try {
//            String beginTime = DateTools.getCreateTime();
//
//            publicService.createSureyingLog("网络设备分析", beginTime, 1,null);
//
//            String endTime = DateTools.getCreateTime();
//
//            publicService.updateSureyingLog("网络设备分析", endTime, 2);
//        } catch (Exception e) {
//            e.printStackTrace();
//            String endTime = DateTools.getCreateTime();
//        }
//    }

    public void gatherIPv4() {
        // ipv4 网段梳理
        this.subnetService.comb();
    }

    public void gatherIPv6() {
        try {

            GatherFactory factory = new GatherFactory();
            Gather gather = factory.getGather(Global.SUBNET_IPV6);
            gather.executeMethod();

        } catch (Exception e) {
            e.printStackTrace();
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
                if(!GatherCacheManager.running) {
                    throw new RuntimeException("测绘已手动中止");
                }
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
//
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

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
        String beginTime = DateTools.getCreateTime();
        int temLogId = publicService.createSureyingLog("终端分析", beginTime, 1, null);
        try {
            //this.gatherArp();
            this.gatherDeviceScan();
            GatherFactory factory = new GatherFactory();
            Gather gather = factory.getGather(Global.TERMINAL);
            gather.executeMethod();
            this.probeToTerminalAndDeviceScan.finalProbe();
            this.verifyVendorUtils.finalTerminal();
            publicService.updateSureyingLog(temLogId, 2);
        } catch (Exception e) {
            e.printStackTrace();
            publicService.updateSureyingLog(temLogId, 3);
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
