package com.metoo.sqlite.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.metoo.sqlite.core.config.shiro.ShiroUserHolder;
import com.metoo.sqlite.dto.OperationLogDTO;
import com.metoo.sqlite.entity.OperationLog;
import com.metoo.sqlite.entity.User;
import com.metoo.sqlite.mapper.OperationLogMapper;
import com.metoo.sqlite.service.IOperationLogService;
import com.metoo.sqlite.utils.date.DateTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-26 17:45
 */
@Service
@Transactional
public class OperationLogServiceImpl implements IOperationLogService {

    @Resource
    private OperationLogMapper operationLogMapper;

    @Override
    public OperationLog selectObjById(Integer id) {
        return this.operationLogMapper.selectObjById(id);
    }

    @Override
    public Page<OperationLog> selectObjConditionQuery(OperationLogDTO instance) {
        if(instance == null){
            instance = new OperationLogDTO();
        }
        Page<OperationLog> page = PageHelper.startPage(instance.getCurrentPage(), instance.getPageSize());
        this.operationLogMapper.selectObjConditionQuery(instance);
        return page;
    }

    @Override
    public List<OperationLog> selectObjByMap(Map params) {
        return this.operationLogMapper.selectObjByMap(params);
    }

    @Override
    public int insert(OperationLog instance) {
        if(instance.getId() == null || instance.getId().equals("")){
            instance.setCreateTime(DateTools.getCreateTime());
            User user = ShiroUserHolder.currentUser();
            instance.setAccount(user.getUsername());
        }
        if(instance.getId() == null || instance.getId().equals("")){
            try {
                return this.operationLogMapper.insert(instance);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }else{
            try {
                return this.operationLogMapper.update(instance);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    @Override
    public int update(OperationLog instance) {
        try {
            return this.operationLogMapper.update(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int delete(Integer id) {
        try {
            return this.operationLogMapper.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
