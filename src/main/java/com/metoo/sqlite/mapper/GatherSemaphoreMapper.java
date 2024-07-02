package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.GatherSemaphore;
import com.metoo.sqlite.entity.ProbeResult;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-07-01 17:39
 */
public interface GatherSemaphoreMapper {

    GatherSemaphore selectObjByOne();

    List<GatherSemaphore> selectObjByMap(Map params);

    int update(GatherSemaphore result);
}
