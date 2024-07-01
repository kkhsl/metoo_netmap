package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.DeviceType;
import com.metoo.sqlite.entity.DeviceVendor;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-22 14:54
 */
public interface IDeviceVendorService {

    DeviceVendor selectObjById(Integer id);

    List<DeviceVendor> selectObjByMap(Map params);

}
