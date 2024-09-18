package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.DeviceModel;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-22 14:54
 */
public interface IDeviceModelService {

    DeviceModel selectObjById(Integer id);

    List<DeviceModel> selectObjByMap(Map params);

}
