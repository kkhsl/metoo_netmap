package com.metoo.sqlite.service;

import com.metoo.sqlite.dto.DeviceDTO;
import com.metoo.sqlite.dto.UserDto;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-22 14:54
 */
public interface IDeviceService {

    Device selectObjById(Integer id);

    List<Device> selectObjByMap(Map params);

    boolean verifyLogDevice();

    int update(Device instance);

    Result selectObjConditionQuery(DeviceDTO dto);

    Result save(Device instance);

    Result batchSave(List<Device> devices);

    Result modify(Integer id);

    Result delete(String ids);

}
