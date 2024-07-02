package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.GatherSemaphore;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-07-01 17:40
 */
public interface IGatherSemaphoreService {

    GatherSemaphore selectObjByOne();

    List<GatherSemaphore> selectObjByMap(Map params);

    int update(GatherSemaphore result);
}
