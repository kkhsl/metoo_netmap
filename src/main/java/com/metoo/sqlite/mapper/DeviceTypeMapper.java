package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.DeviceType;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-22 14:57
 */
public interface DeviceTypeMapper {

    DeviceType selectObjById(Integer id);

    List<DeviceType> selectObjByMap(Map params);

}
