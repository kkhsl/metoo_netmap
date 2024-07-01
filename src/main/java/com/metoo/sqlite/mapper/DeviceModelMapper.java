package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.DeviceModel;
import com.metoo.sqlite.entity.DeviceVendor;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-22 14:57
 */
public interface DeviceModelMapper {

    DeviceModel selectObjById(Integer id);

    List<DeviceModel> selectObjByMap(Map params);

}
