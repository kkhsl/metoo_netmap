package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.DeviceModel;
import com.metoo.sqlite.entity.DeviceVendor;
import com.metoo.sqlite.mapper.DeviceModelMapper;
import com.metoo.sqlite.mapper.DeviceVendorMapper;
import com.metoo.sqlite.service.IDeviceModelService;
import com.metoo.sqlite.service.IDeviceVendorService;
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
public class DeviceModelServiceImpl implements IDeviceModelService {

    @Resource
    private DeviceModelMapper deviceModelMapper;

    @Override
    public DeviceModel selectObjById(Integer id) {
        return this.deviceModelMapper.selectObjById(id);
    }

    @Override
    public List<DeviceModel> selectObjByMap(Map params) {
        return this.deviceModelMapper.selectObjByMap(params);
    }
}
