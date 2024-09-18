package com.metoo.sqlite.gather.schedule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-24 20:51
 */
@Configuration
public class SchedulingConfiguration {

    /**
     *任务调度线程池配置
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(15);
        taskScheduler.setThreadNamePrefix("scheduled-gather-");
        return taskScheduler;
    }
}
