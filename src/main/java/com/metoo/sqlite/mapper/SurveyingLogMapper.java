package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.SurveyingLog;

import java.util.List;
import java.util.Map;

public interface SurveyingLogMapper {

    List<SurveyingLog> selectObjByMap(Map params);

    int insert(SurveyingLog instance);

    int update(SurveyingLog instance);

    int delete(Integer id);

    int deleteTable();
}
