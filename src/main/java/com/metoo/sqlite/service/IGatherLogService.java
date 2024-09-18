package com.metoo.sqlite.service;

import com.metoo.sqlite.dto.DeviceDTO;
import com.metoo.sqlite.dto.GatherLogDTO;
import com.metoo.sqlite.entity.GatherLog;
import com.metoo.sqlite.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-07-01 15:53
 */
public interface IGatherLogService {

    List<GatherLog> selectObjByMap(Map params);

    Result selectObjConditionQuery(GatherLogDTO dto);

    int insert(GatherLog instance);

    int deleteTable();
}
