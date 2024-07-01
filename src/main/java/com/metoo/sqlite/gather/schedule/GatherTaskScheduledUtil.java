package com.metoo.sqlite.gather.schedule;

import com.metoo.sqlite.gather.factory.gather.Gather;
import com.metoo.sqlite.gather.factory.gather.GatherFactory;
import com.metoo.sqlite.gather.pool.ThreadPoolUtils;
import com.metoo.sqlite.utils.Global;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

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
