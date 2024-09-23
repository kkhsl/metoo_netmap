package com.metoo.sqlite.gather.schedule;

import com.metoo.sqlite.service.impl.UpdateVersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 客户端定时任务更新
 *
 * @author zzy
 * @version 1.0
 * @date 2024/9/20 17:02
 */
@Component
@Slf4j
public class UpdateVersionTask {

    @Value("${version.switch.is-open}")
    private boolean versionFlag;
    @Autowired
    private UpdateVersionService updateVersionService;



    /**
     * 定时自动下载定时任务
     */
    @Scheduled(cron = "${scheduler.autoUpdateVersionSchedule:0 0 */1 * * ?}")
    public void updateVersion() {
        // 判断版本更新开关是否开启
        if (versionFlag) {
            log.info("====================================客户端定时任务更新定时任务开始执行==========================");
            updateVersionService.updateVersion();
            log.info("====================================客户端定时任务更新定时任务结束==========================");
        }
    }


}
