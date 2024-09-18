package com.metoo.sqlite.manager;


import com.metoo.sqlite.entity.SurveyingLog;
import com.metoo.sqlite.service.ISurveyingLogService;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.vo.Result;
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
        List<SurveyingLog> surveyingLogList = this.surveyingLogService.selectObjByMap(null);
        Map result = new HashMap();
        boolean finish = false;
        result.put("data", surveyingLogList);
//        if(surveyingLogList.size() > 0){
//            if(surveyingLogList.size() == 8){
//                result.put("finish", true);
//                for (SurveyingLog surveyingLog : surveyingLogList) {
//                    if(surveyingLog.getStatus() == 2 || surveyingLog.getStatus() == 3){
//                        finish = true;
//                    }else{
//                        finish = false;
//                        break;
//                    }
//                }
//            }
//        }

        if(surveyingLogList.size() > 0){
            if(surveyingLogList.size() == 7){
                result.put("finish", true);
                for (SurveyingLog surveyingLog : surveyingLogList) {
                    if(surveyingLog.getStatus() == 2 || surveyingLog.getStatus() == 3){
                        finish = true;
                    }else{
                        finish = false;
                        break;
                    }
                }
            }
        }

        result.put("finish", finish);
        return ResponseUtil.ok(result);
    }

    @PostMapping("/save")
    public Result save(@RequestBody SurveyingLog instance){
        return this.surveyingLogService.insert(instance);
    }
}
