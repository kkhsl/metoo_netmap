package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.core.config.enums.LogStatusType;
import com.metoo.sqlite.entity.GatherLog;
import com.metoo.sqlite.entity.SurveyingLog;
import com.metoo.sqlite.gather.common.GatherCacheManager;
import com.metoo.sqlite.service.IGatherLogService;
import com.metoo.sqlite.service.ISurveyingLogService;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.utils.file.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 公共服务
 *
 * @author zhaozhiyuan
 * @version 1.0
 * @date 2024/10/12 21:46
 */
@Service
@Slf4j
public class PublicService {
    @Autowired
    private ISurveyingLogService surveyingLogService;

    @Autowired
    private IGatherLogService gatherLogService;

    public void logGatheringResults(String beginTime, String endTime, String data, String surveying) {
        try {
            GatherLog gatherLog = new GatherLog();
            gatherLog.setCreateTime(beginTime);
            gatherLog.setBeginTime(beginTime);
            gatherLog.setEndTime(endTime);
            gatherLog.setType("手动");
            gatherLog.setResult("成功");
            gatherLog.setDetails(surveying);
            gatherLog.setData(data);
            gatherLogService.insert(gatherLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearLogs() {
        try {
            surveyingLogService.deleteTable();
            gatherLogService.deleteTable();
            // 清空错误图片
            FileUtils.clearDirectoryWithFileType(Global.errorImageUrl, FileUtils.getFileTypeName(Global.errorImageFileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int createSureyingLog(String name, String beginTime, Integer status, Integer parentId) {
        if(!GatherCacheManager.running) {
            throw new RuntimeException("测绘已手动中止");
        }
        SurveyingLog surveyingLog = new SurveyingLog()
                .createTime(DateTools.getCreateTime())
                .name(name).beginTime(beginTime)
                .status(status);
        if (null != parentId) {
            surveyingLog.setParentId(parentId);
        }
        if (null != status && status.equals(LogStatusType.FAIL.getCode())) {
            surveyingLog.setEndTime(DateTools.getCreateTime());
        }
        this.surveyingLogService.insert(surveyingLog);
        return surveyingLog.getId();
    }

    public void updateSureyingLog(Integer id, Integer status) {
        if(!GatherCacheManager.running) {
            throw new RuntimeException("测绘已手动中止");
        }
        SurveyingLog surveyingLog = new SurveyingLog();
        surveyingLog.setId(id);
        surveyingLog.endTime(DateTools.getCreateTime()).status(status);
        this.surveyingLogService.update(surveyingLog);
    }
}
