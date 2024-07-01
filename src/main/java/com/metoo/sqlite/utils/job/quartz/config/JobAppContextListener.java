//package com.metoo.sqlite.utils.job.quartz.config;
//
//import com.metoo.sqlite.utils.job.quartz.CaptchaCleanerJob;
//import org.quartz.*;
//import org.quartz.impl.StdSchedulerFactory;
//
//import javax.servlet.ServletContextEvent;
//import javax.servlet.ServletContextListener;
//import javax.servlet.annotation.WebListener;
//import javax.servlet.http.HttpSession;
//
///**
// * @author HKK
// * @version 1.0
// * @date 2024-06-19 15:00
// */
//@WebListener
//public class JobAppContextListener implements ServletContextListener {
//
//    private Scheduler scheduler;
//
//    @Override
//    public void contextInitialized(ServletContextEvent sce) {
//        try {
//            HttpSession session = sce.getServletContext().getSession(true);
//
//            JobDetail job = JobBuilder.newJob(CaptchaCleanerJob.class)
//                    .withIdentity("captchaCleanerJob", "group1")
//                    .usingJobData("session", session)
//                    .build();
//
//            Trigger trigger = TriggerBuilder.newTrigger()
//                    .withIdentity("captchaCleanerTrigger", "group1")
//                    .startNow()
//                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
//                            .withIntervalInMinutes(5) // 每 5 分钟执行一次
//                            .repeatForever())
//                    .build();
//
//            scheduler = StdSchedulerFactory.getDefaultScheduler();
//            scheduler.start();
//            scheduler.scheduleJob(job, trigger);
//        } catch (SchedulerException se) {
//            se.printStackTrace();
//        }
//    }
//
//    @Override
//    public void contextDestroyed(ServletContextEvent sce) {
//        try {
//            if (scheduler != null) {
//                scheduler.shutdown();
//            }
//        } catch (SchedulerException se) {
//            se.printStackTrace();
//        }
//    }
//}
