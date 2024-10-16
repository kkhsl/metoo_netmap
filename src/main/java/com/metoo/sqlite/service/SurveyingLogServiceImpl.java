package com.metoo.sqlite.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.metoo.sqlite.core.config.enums.LogStatusType;
import com.metoo.sqlite.entity.SurveyingLog;
import com.metoo.sqlite.mapper.SurveyingLogMapper;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.utils.file.FileUtils;
import com.metoo.sqlite.vo.Result;
import com.metoo.sqlite.vo.SurveyingLogVo;
import com.metoo.sqlite.vo.SurveyingSubLogStatusVo;
import com.metoo.sqlite.vo.SurveyingSubLogVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        if (instance.getId() == null || instance.getId().equals("")) {
            instance.setCreateTime(DateTools.getCreateTime());
            try {
                int i = this.surveyingLogMapper.insert(instance);
                return i >= 0 ? ResponseUtil.ok() : ResponseUtil.saveError();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseUtil.saveError();
            }
        } else {
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

    @Override
    public List<SurveyingLogVo> queryLogInfo() {
        List<SurveyingLogVo> result = new ArrayList<>();
        List<SurveyingLog> surveyingLogList = this.surveyingLogMapper.selectObjByMap(null);
        if (CollUtil.isNotEmpty(surveyingLogList)) {
            // 查找所有一级地址
            for (SurveyingLog surveyingLog : surveyingLogList) {
                if (null == surveyingLog.getParentId()) {
                    // 父级模块
                    result.add(Convert.convert(SurveyingLogVo.class, surveyingLog));
                }
            }
            //获取子级数据
            if (result.size() > 0) {
                // 使用递归，查出下级数据
                for (SurveyingLogVo subTemp : result) {
                    subTemp.setSubLogs(findSublogs(subTemp.getId(), surveyingLogList));
                }
            }
        }
        return result;
    }

    /**
     * 通过递归查找下级数据
     *
     * @param parentId
     * @param surveyingLogList
     * @return
     */
    public List<SurveyingSubLogVo> findSublogs(Integer parentId, List<SurveyingLog> surveyingLogList) {
        List<SurveyingSubLogVo> result = new ArrayList<>();
        List<SurveyingLog> subLogs = surveyingLogList.stream()
                .filter(temp -> parentId.equals(temp.getParentId()))
                .collect(Collectors.toList());
        // 根据设备分组
        if (CollUtil.isNotEmpty(subLogs)) {
            // 子集存在，则继续查询
            subLogs.forEach(temp -> {
                SurveyingSubLogVo subLogVo = Convert.convert(SurveyingSubLogVo.class, temp);
                if (null != subLogVo && LogStatusType.FAIL.getCode().equals(subLogVo.getStatus())) {
                    //设置图片地址
                    subLogVo.setErrorImgUrl(Global.errorImageUrl + File.separator + subLogVo.getId() + "." + FileUtils.getFileTypeName(Global.errorImageFileName));
                }
                result.add(subLogVo);
            });
        }
        return result;
    }
}
