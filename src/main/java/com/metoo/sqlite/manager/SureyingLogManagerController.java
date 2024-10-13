package com.metoo.sqlite.manager;


import com.metoo.sqlite.entity.SurveyingLog;
import com.metoo.sqlite.gather.common.GatherCacheManager;
import com.metoo.sqlite.service.ISurveyingLogService;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.vo.Result;
import com.metoo.sqlite.vo.SurveyingLogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/admin/sureying/log")
@RestController
public class SureyingLogManagerController {

    @Autowired
    private ISurveyingLogService surveyingLogService;

    // false true结束轮询
    @GetMapping
    public Result logs(){
        // 获取测绘日志
        List<SurveyingLogVo> surveyingLogList=surveyingLogService.queryLogInfo();
        Map result = new HashMap();
        boolean finish = false;
        result.put("data", surveyingLogList);

        if(surveyingLogList.size() > 0){
            result.put("finish", true);
            for (SurveyingLogVo surveyingLog : surveyingLogList) {
                if(surveyingLog.getStatus() == 3){
                    finish = false;
                    break;
                }
            }
        }
        if(!GatherCacheManager.running){
            // 任务手动终止，采集失败
            finish = false;
        }
        result.put("finish", finish);
        return ResponseUtil.ok(result);
    }

    @PostMapping("/save")
    public Result save(@RequestBody SurveyingLog instance){
        return this.surveyingLogService.insert(instance);
    }
}
