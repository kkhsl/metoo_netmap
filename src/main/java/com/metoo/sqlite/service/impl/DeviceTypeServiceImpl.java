package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.DeviceType;
import com.metoo.sqlite.mapper.DeviceTypeMapper;
import com.metoo.sqlite.service.IDeviceTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-22 14:57
 */
@Service
@Transactional
public class DeviceTypeServiceImpl implements IDeviceTypeService {

    @Resource
    private DeviceTypeMapper deviceTypeMapper;

    @Override
    public DeviceType selectObjById(Integer id) {
        return this.deviceTypeMapper.selectObjById(id);
    }

    @Override
    public List<DeviceType> selectObjByMap(Map params) {
        return this.deviceTypeMapper.selectObjByMap(params);
    }
}
