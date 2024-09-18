package com.metoo.sqlite.manager;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.*;
import com.metoo.sqlite.gather.factory.gather.thread.Gather;
import com.metoo.sqlite.gather.factory.gather.thread.GatherFactory;
import com.metoo.sqlite.gather.factory.gather.thread.TestThreadExec;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.other.Ipv4PanabitCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.manager.utils.jx.DataUtils;
import com.metoo.sqlite.manager.utils.jx.JXDataUtils;
import com.metoo.sqlite.service.*;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.utils.elasticsearch.ElasticsearchService;
import com.metoo.sqlite.utils.file.DataFileWrite;
import com.metoo.sqlite.utils.file.FileToDatabase;
import com.metoo.sqlite.utils.muyun.MuyunService;
import com.metoo.sqlite.utils.panabit.PanabitSendMsgUtils.PanabitService;
import com.metoo.sqlite.utils.version.DownloadAndExecuteBatFromZip;
import com.metoo.sqlite.vo.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-27 11:18
 */
@Slf4j
@Api("测试按钮接口")
@RequestMapping("/admin/test")
@RestController
public class TestManagerController {

    @Autowired
    private DataUtils dataUtils;
    @Autowired
    private IVersionService versionService;
    @Autowired
    private FileToDatabase fileToDatabase;
    @Autowired
    private IGatherLogService gatherLogService;
    @Autowired
    private IGatewayInfoService gatewayInfoService;
    @Autowired
    private ITerminalService terminalService;
    @Autowired
    private JXDataUtils jxDataUtils;
    @Autowired
    private Ipv4Service ipv4Service;
    @Autowired
    private PanabitService panabitService;
    @Autowired
    private IPanaSwitchService panaSwitchService;
    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private PyExecUtils pyExecUtils;
    @Autowired
    private ElasticsearchService elasticsearchService;

    @GetMapping("/elasticsearch")
    public void elasticsearch() throws IOException {
        this.elasticsearchService.searchDocuments();;
    }

    @GetMapping("/searchAndAggregateDocuments")
    public void searchAndAggregateDocuments() throws IOException {
        this.elasticsearchService.searchAndAggregateDocuments();;
    }



    @GetMapping("gatheripv4")
    public void testGatherIpv4(){
        TestThreadExec.gatherIpv4();
    }

    @Test
    public void test11(){
        List<Ipv6> ipv6List = JSONObject.parseArray("[]", Ipv6.class);
    }

    @Autowired
    private MuyunService muyunService;

    @GetMapping("/muyun/arp")
    public String arp(){
        String result = this.muyunService.getArp("", 0, "", "");
        return result;
    }

    @GetMapping("/muyun/ipv6_neighbor")
    public String ipv6_neighbor(){
        String result = this.muyunService.getIpv6_neighbor("", 0, "", "");
        return result;
    }


    @GetMapping("pana/{id}")
    public Object test(@PathVariable String id){
        this.ipv4Service.deleteTable();
        Device device = this.deviceService.selectObjById(Integer.parseInt(id));
        Context context = new Context();
        context.setCreateTime(DateTools.getCreateTime());
        context.setEntity(device);

        Ipv4PanabitCollectionStrategy collectionStrategy = new Ipv4PanabitCollectionStrategy(ipv4Service,
                panabitService, panaSwitchService);

        collectionStrategy.collectData(context);

//        Ipv4CollectionStrategy collectionStrategy2 = new Ipv4CollectionStrategy(ipv4Service,
//                pyExecUtils);
//        collectionStrategy2.collectData(context);

        return this.ipv4Service.selectObjByMap(null);
    }

    @GetMapping("clearFile")
    public void clearFile(){
        DataFileWrite.clearFile();
    }

//     @GetMapping("/version/update")
//    public Result test8(){
//        try {
//            DownloadAndExecuteBatFromZip.versionUpdate();
//            return ResponseUtil.ok();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return ResponseUtil.error();
//    }
    @Autowired
    private ISubnetIpv6Service subnetIpv6Service;
    @Autowired
    private ISubnetService subnetService;

    public static void main(String[] args) {
        List list = new ArrayList();
        list.add(1);
        list.add("a");
        System.out.println(list);

        list.removeAll(list);

        System.out.println(list);


        String result = "[\n" +
                "  {\n" +
                "    \"interface\": \"GigabitEthernet1/0/0\",\n" +
                "    \"state\": \"UP\",\n" +
                "    \"ip_address\": \"222.223.224.225/24\",\n" +
                "    \"description\": \"Huawei, USG6000V2 Series, GigabitEthernet1/0/0 Interface\",\n" +
                "    \"operator\": \"\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"interface\": \"GigabitEthernet1/0/1\",\n" +
                "    \"state\": \"UP\",\n" +
                "    \"ip_address\": \"1.1.1.3/24\",\n" +
                "    \"description\": \"Huawei, USG6000V2 Series, GigabitEthernet1/0/1 Interface\",\n" +
                "    \"operator\": \"\"\n" +
                "  }\n" +
                "]";
        List<GatewayInfo> gatewayInfoList = JSONObject.parseArray(result, GatewayInfo.class);
        System.out.println(gatewayInfoList);
    }

    public void test1() {
        int n = 1 / 0;
    }

    public void test4() {
        test1();
    }

    @Test
    public void test10() {
        String a = "1.1.2";
        String b = "1.1.1";
        int result = a.compareTo(b);
        System.out.println(result);
    }

    @Test
    public void test5() {
        try {
            test4();
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("Exception caught in main: " + e.getMessage());
        }
    }

    @GetMapping("/version/update")
    public Result test8() {


        log.info("update version start =====================");

//        if(versionFlag){
//            String currentVersion = "";
//
//            Version version = this.versionService.selectObjByOne();
//
//            if (version != null) {
//                currentVersion = version.getVersion();
//            } else {
//                version = new Version();
//            }
//
//            String versionStr = DownloadAndExecuteBatFromZip.verifyVersion(currentVersion);
//            if (StringUtil.isNotEmpty(versionStr)) {
//                if (StringUtil.isNotEmpty(versionStr)) {
//
////                    version.setVersion(versionStr);
////                    this.versionService.save(version);
//
//                    log.info("update version exec start =====================");
//
//                    try {
//                        DownloadAndExecuteBatFromZip.versionUpdate();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//
//                    try {
//                        version.setVersion(versionStr);
//                        this.versionService.save(version);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    try {
//                        // 执行
//                        String extractDirectory = Global.versionUnzip;
//                        // 执行 .bat 文件
//                        String batFilePath = extractDirectory + File.separator + Global.versionScriptName;
//                        DownloadAndExecuteBatFromZip.executeBatchFile(batFilePath);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//
//                    log.info("update version exec end =====================");
//
//
//                }
//            }
//        }
//
//        log.info("update version end =====================");


        try {
            String currentVersion = "";

            Version version = this.versionService.selectObjByOne();

            if (version != null) {
                currentVersion = version.getVersion();
            } else {
                version = new Version();
            }

            String versionStr = DownloadAndExecuteBatFromZip.verifyVersion(currentVersion);
            if (StringUtil.isNotEmpty(versionStr)) {
                if (StringUtil.isNotEmpty(versionStr)) {

    //                    version.setVersion(versionStr);
    //                    this.versionService.save(version);

                    log.info("update version exec start =====================");

                    try {
                        DownloadAndExecuteBatFromZip.versionUpdate();

                        try {
                            version.setVersion(versionStr);
                            this.versionService.save(version);
                        } catch (Exception e) {
                            e.printStackTrace(); return ResponseUtil.ok();
                        }

                        try {
                            // 执行
                            String extractDirectory = Global.versionUnzip;
                            // 执行 .bat 文件
                            String batFilePath = extractDirectory + File.separator + Global.versionScriptName;
                            DownloadAndExecuteBatFromZip.executeBatchFile(batFilePath);
                            log.info("更新结束");
                        } catch (IOException e) {
                            e.printStackTrace(); return ResponseUtil.ok();
                        } catch (InterruptedException e) {
                            e.printStackTrace(); return ResponseUtil.ok();
                        }

                        log.info("update version exec end =====================");

                    }catch (Exception e){ return ResponseUtil.ok();}
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); return ResponseUtil.ok();
        }
        log.info("update version end =====================");
        return ResponseUtil.ok();
    }


    @GetMapping("/version/executeBatchFile")
    public Result executeBatchFile() {
        try {
            DownloadAndExecuteBatFromZip.executeBatchFile();
            return ResponseUtil.ok();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseUtil.error();
    }

    @GetMapping("/version/restart")
    public Result restart() {
//        try {
//            // 调用 restart.bat 脚本
//            String batFilePath = "C:\\Users\\hkk\\Desktop\\metoo\\restart\\restart.bat";
//            Process process = Runtime.getRuntime().exec("cmd /c start /B " + batFilePath);
//
//            // 不需要等待脚本执行完成，直接退出当前 Java 进程
//            System.exit(0);
//            return ResponseUtil.ok();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return ResponseUtil.error();


        try {
            DownloadAndExecuteBatFromZip.executeBatchFile("C:\\Users\\hkk\\Desktop\\metoo\\restart\\restart.bat");
            return ResponseUtil.ok();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseUtil.error();
    }

    @GetMapping("/version/restart/test")
    public Result restartTest() {
        return ResponseUtil.ok(3);
    }

    @GetMapping("/version/updateById")
    public Result test8(String id) {
        try {
            DownloadAndExecuteBatFromZip.versionUpdate();
            return ResponseUtil.ok();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseUtil.error();
    }

    @GetMapping("test")
    public Integer testt() {
        return 6;
    }

    @GetMapping("getData")
    public Object getData() {
        return this.dataUtils.getUnsureInfo();
    }
    @GetMapping("getData2")
    public Object getData2() {
        return this.jxDataUtils.getData();
    }

    @Test
    public void test23() {
        List<Probe> probes = new ArrayList<>();
        Probe probe = new Probe();
        probe.setPort_service_product("switch1");

        Probe probe4 = new Probe();
        probe4.setPort_service_product("switch2");

        Probe probe2 = new Probe();
        probe2.setPort_service_product("router1");

        Probe probe3 = new Probe();
        probe3.setPort_service_product("router2");

        Probe probe5 = new Probe();
        probe5.setMac_addr("router2");

        probes.add(probe);
        probes.add(probe2);
        probes.add(probe3);
        probes.add(probe4);
        probes.add(probe5);


        List<Probe> probes_switch = probes.stream()
                .filter(e -> e.getPort_service_product() != null && (e.getPort_service_product().contains("switch")
                        || e.getPort_service_product().contains("router")))
                .collect(Collectors.toList());
        System.out.println(JSONObject.toJSONString(probes_switch));
    }

    @GetMapping("/fileToProbe")
    public Result test() {

        this.fileToDatabase.write("asd");


        return ResponseUtil.ok();
    }

    @GetMapping("/fileToProbe2")
    public Result test2() {

        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather("fileToProbe");
        gather.executeMethod();

        return ResponseUtil.ok();
    }

    @GetMapping("/fileToProbe3")
    public Result fileToProbe3() {
        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather("osScanner");
        gather.executeMethod();
        return ResponseUtil.ok();
    }

    @GetMapping("/gatherLog")
    public void test3() {
        try {
            GatherLog gatherLog = new GatherLog();
            gatherLog.setCreateTime(DateTools.getCreateTime());
            gatherLog.setBeginTime(DateTools.getCreateTime());
            gatherLog.setEndTime(DateTools.getCreateTime());
            gatherLog.setType("手动");
            gatherLog.setResult("成功");
            gatherLog.setDetails("");
            gatherLog.setData("");
            this.gatherLogService.insert(gatherLog);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @GetMapping("/write")
    public void wirte() {
        try {
            jxDataUtils.getEncryptedData();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @GetMapping("/createLog")
    public Result createLog() {
        String beginTime = DateTools.getCreateTime();

        String endTime = DateTools.getCreateTime();

        String data = this.jxDataUtils.getEncryptedData();

        log.info("================sureying gather start ");
        String surveying = this.getSurveyingResult();

        try {
            GatherLog gatherLog = new GatherLog();
            gatherLog.setCreateTime(beginTime);
            gatherLog.setBeginTime(beginTime);
            gatherLog.setEndTime(endTime);
            gatherLog.setType("手动");
            gatherLog.setResult("成功");
            gatherLog.setDetails(surveying);
            gatherLog.setData(data);
            this.gatherLogService.insert(gatherLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseUtil.ok();
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



    @GetMapping("/device/scan")
    public void deviceScan() {
        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather(Global.DEVICE_SCAN);
        gather.executeMethod();
    }
}
