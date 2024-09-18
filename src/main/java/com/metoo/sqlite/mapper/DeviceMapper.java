package com.metoo.sqlite.mapper;

import com.metoo.sqlite.dto.DeviceDTO;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-22 14:57
 */
public interface DeviceMapper {

    Device selectObjById(Integer id);

    List<Device> selectObjByMap(Map params);

    List<Device> selectObjConditionQuery(DeviceDTO dto);

    int save(Device instance);

    int update(Device instance);

    int delete(Integer id);
}
