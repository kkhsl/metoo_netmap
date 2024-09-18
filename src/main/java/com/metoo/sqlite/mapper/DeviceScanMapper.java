package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.DeviceScan;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-28 17:15
 */
public interface DeviceScanMapper {

    List<DeviceScan> selectObjByMap(Map params);

    int insert(DeviceScan instance);

    int update(DeviceScan instance);

    int batchInsert(List<DeviceScan> list);

    int deleteTable();

    int copyToBck();

    int deleteTableBck();
}
