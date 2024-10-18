package com.metoo.sqlite.gather.schedule;

import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.Version;
import com.metoo.sqlite.gather.factory.gather.thread.Gather;
import com.metoo.sqlite.gather.factory.gather.thread.GatherFactory;
import com.metoo.sqlite.manager.GatherManagerController;
import com.metoo.sqlite.manager.utils.file.FileVersionUtils;
import com.metoo.sqlite.service.IVersionService;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.version.DownloadAndExecuteBatFromZip;
import com.metoo.sqlite.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-01-18 16:03
 */
@Slf4j
@Configuration
public class GatherTaskScheduledUtil {

    @Value("${task.switch.is-open}")
    private boolean flag;
    @Value("${version.switch.is-open}")
    private boolean versionFlag;
    @Autowired
    private IVersionService versionService;

    private static boolean VRESION = true;

    @Scheduled(cron = "0 */1 * * * ?")
    public void verifyVersion() {
        if(VRESION){
            try {
                String state = FileVersionUtils.readState(Global.version_state, Global.version_state_name);
                if(StringUtil.isNotEmpty(state)){
                    if(state.trim().equals("4")){
                        Version obj = null;
                        String version = FileVersionUtils.readState(Global.version_state, Global.version_info_name);
                        if(StringUtil.isNotEmpty(version)){
                            obj = this.versionService.selectObjByOne();
                            if(obj != null){
                                if(obj.getVersion().compareTo(version) < 0){
                                    obj.setVersion(version);
                                    this.versionService.update(obj);
                                    FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_info_name, "0");
                                    // 修改VERSION

                                }
                            }else{
                                obj = new Version();
                                obj.setVersion(version);
                                this.versionService.save(obj);
                                FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_info_name, "0");
                                // 修改VERSION

                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private final GatherManagerController gatherManagerController;

    // 使用构造函数注入 GatherManagerController
    public GatherTaskScheduledUtil(GatherManagerController gatherManagerController) {
        this.gatherManagerController = gatherManagerController;
    }

//    @Scheduled(cron = "0 0 * * * ?")
//    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void update2(){

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

        Result result = this.gatherManagerController.main(1);
        if(result.getCode() != null && result.getCode() == 1002){
            System.out.println("正在采集,请勿更新");
        }else if(result.getCode() != null && result.getCode() == 1001){
            System.out.println("正在采集,请勿更新");

            if(versionFlag){
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
                                e.printStackTrace();
                            }

                            try {
                                // 执行
                                String extractDirectory = Global.versionUnzip;
                                // 执行 .bat 文件
                                String batFilePath = extractDirectory + File.separator + Global.versionScriptName;
                                DownloadAndExecuteBatFromZip.executeBatchFile(batFilePath);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        log.info("update version exec end =====================");

                    }
                }
            }
        }
        log.info("update version end =====================");
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void gatherPort() {
        if(flag){
            try {
                GatherFactory factory = new GatherFactory();
                Gather gather = factory.getGather(Global.PY_SUFFIX_ALIVEINT);
                gather.executeMethod();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void gatherIpv4() {
        if(flag){
            try {
                GatherFactory factory = new GatherFactory();
                Gather gather = factory.getGather(Global.PY_SUFFIX_IPV4);
                gather.executeMethod();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void gatherIpv6() {
        if(flag){
            try {
                GatherFactory factory = new GatherFactory();
                Gather gather = factory.getGather(Global.PY_SUFFIX_IPV6_NEIGHBORS);
                gather.executeMethod();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void gatherPingAndArp() {
        if(flag){
            try {
                GatherFactory factory = new GatherFactory();
                Gather gather = factory.getGather(Global.PY_SUFFIX_PING);
                gather.executeMethod();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    @Scheduled(cron = "0 */1 * * * ?")
//    public void gatherSubnetIpv6() {
//        if(flag){
//            try {
//                GatherFactory factory = new GatherFactory();
//                Gather gather = factory.getGather(Global.PY_SUFFIX_PING);
//                gather.executeMethod();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }


    // 关闭线程池
//    private static void shutdownScheduler() {
//
//        ExecutorService scheduler = ThreadPoolUtils.getInstance();
//
//        scheduler.shutdown();
//        try {
//            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
//                scheduler.shutdownNow();
//                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
//                    System.err.println("线程池未能完全关闭");
//                }
//            }
//        } catch (InterruptedException e) {
//            scheduler.shutdownNow();
//            Thread.currentThread().interrupt();
//        }
//    }
}
