package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.SurveyingLog;
import com.metoo.sqlite.mapper.SurveyingLogMapper;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.vo.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SurveyingLogServiceImpl implements ISurveyingLogService {

    @Resource
    private SurveyingLogMapper surveyingLogMapper;

    @Override
    public List<SurveyingLog> selectObjByMap(Map params) {
        List<SurveyingLog> logs = this.surveyingLogMapper.selectObjByMap(params);
        return logs;
    }

    @Override
    public Result insert(SurveyingLog instance) {
        if(instance.getId() == null || instance.getId().equals("")){
            instance.setCreateTime(DateTools.getCreateTime());
            try {
                int i = this.surveyingLogMapper.insert(instance);
                return i >= 0 ? ResponseUtil.ok() : ResponseUtil.saveError();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseUtil.saveError();
            }
        }else{
            try {
                int i = this.surveyingLogMapper.update(instance);
                return i >= 0 ? ResponseUtil.ok() : ResponseUtil.saveError();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseUtil.saveError();
            }
        }
    }

    @Override
    public int update(SurveyingLog instance) {
        try {
            return this.surveyingLogMapper.update(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Result delete(Integer id) {
        try {
            int i = this.surveyingLogMapper.delete(id);
            return i >= 0 ? ResponseUtil.ok() : ResponseUtil.deleteError();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.deleteError();
        }
    }

    @Override
    public int deleteTable() {
        try {
            return this.surveyingLogMapper.deleteTable();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
