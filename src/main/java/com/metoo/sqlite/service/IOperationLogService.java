package com.metoo.sqlite.service;

import com.github.pagehelper.Page;
import com.metoo.sqlite.dto.OperationLogDTO;
import com.metoo.sqlite.entity.OperationLog;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-26 17:44
 */
public interface IOperationLogService {

    OperationLog selectObjById(Integer id);

    Page<OperationLog> selectObjConditionQuery(OperationLogDTO instance);

    List<OperationLog> selectObjByMap(Map params);

    int insert(OperationLog instance);

    int update(OperationLog instance);

    int delete(Integer id);
}
