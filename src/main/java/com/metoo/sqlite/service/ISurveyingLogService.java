package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.SurveyingLog;
import com.metoo.sqlite.vo.Result;
import com.metoo.sqlite.vo.SurveyingLogVo;

import java.util.List;
import java.util.Map;

public interface ISurveyingLogService {

    List<SurveyingLog> selectObjByMap(Map params);

    Result insert(SurveyingLog instance);

    int update(SurveyingLog instance);

    Result delete(Integer id);

    int deleteTable();

    /**
     * 获取测绘采集日志
     * @return
     */
    List<SurveyingLogVo> queryLogInfo();
}
