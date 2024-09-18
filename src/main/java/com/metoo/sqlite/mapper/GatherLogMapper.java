package com.metoo.sqlite.mapper;

import com.metoo.sqlite.dto.GatherLogDTO;
import com.metoo.sqlite.entity.GatherLog;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-07-01 15:57
 */
public interface GatherLogMapper {

    List<GatherLog> selectObjByMap(Map params);

    List<GatherLog> selectObjConditionQuery(GatherLogDTO dto);

    int insert(GatherLog instance);

    int deleteTable();
}
