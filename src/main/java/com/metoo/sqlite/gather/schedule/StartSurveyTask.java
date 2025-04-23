package com.metoo.sqlite.gather.schedule;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.metoo.sqlite.manager.utils.file.FileVersionUtils;
import com.metoo.sqlite.service.impl.GatherAllInOneService;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 客户端指定测绘时间执行定时任务
 *
 * @author zzy
 * @version 1.0
 * @date 2024/9/20 17:02
 */
@Component
@Slf4j
public class StartSurveyTask {

    @Value("${survey.switch.is-open}")
    private boolean surveyFlag;
    @Autowired
    private GatherAllInOneService allInOneService;



    /**
     * 客户端指定测绘时间执行定时任务
     */
    @Scheduled(cron = "${scheduler.startSurvey:0 */1 * * * ?}")
    public void startSurvey() {
        // 判断版本更新开关是否开启
        if (surveyFlag) {
            log.info("====================================客户端指定测绘时间执行定时任务开始执行==========================");
            String surveyTime = FileVersionUtils.readState(Global.version_state, Global.survey_time_name);
            if(StrUtil.isNotEmpty(surveyTime)) {
                try {
                    if(DateTools.isDateTimeMatchCurrentMinute(surveyTime)){
                        Result result= allInOneService.startGather(2);
                        log.info("====================================客户端指定测绘时间执行定时任务结果==========================:{}", JSONUtil.toJsonStr(result));
                    }
                } catch (Exception e) {
                    log.error("执行指定测绘时间定时任务失败：{}",e);
                }

            }
            log.info("====================================客户端指定测绘时间执行定时任务结束==========================");
        }
    }


}
