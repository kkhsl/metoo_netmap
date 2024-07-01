package com.metoo.sqlite.mapper;

import com.metoo.sqlite.dto.OperationLogDTO;
import com.metoo.sqlite.entity.OperationLog;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-26 17:45
 */
public interface OperationLogMapper {

    OperationLog selectObjById(Integer id);

    List<OperationLog> selectObjConditionQuery(OperationLogDTO instance);

    List<OperationLog> selectObjByMap(Map params);

    int insert(OperationLog instance);

    int update(OperationLog instance);

    int delete(Integer id);
}
