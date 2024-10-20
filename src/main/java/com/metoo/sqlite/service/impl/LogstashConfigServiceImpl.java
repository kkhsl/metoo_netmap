package com.metoo.sqlite.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.DeviceType;
import com.metoo.sqlite.entity.DeviceVendor;
import com.metoo.sqlite.entity.LogstashConfig;
import com.metoo.sqlite.mapper.LogstashConfigMapper;
import com.metoo.sqlite.service.IDeviceService;
import com.metoo.sqlite.service.IDeviceTypeService;
import com.metoo.sqlite.service.IDeviceVendorService;
import com.metoo.sqlite.service.ILogstashConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * logstash配置文件实现类
 *
 * @author zzy
 * @version 1.0
 * @date 2024-10-18 21:31
 */
@Service
@Transactional
public class LogstashConfigServiceImpl implements ILogstashConfigService {
    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private IDeviceTypeService deviceTypeService;
    @Autowired
    private IDeviceVendorService deviceVendorService;
    @Autowired
    private LogstashConfigMapper loggerMapper;

    @Override
    public List<String> queryByName() {
        // 获取当前所有的设备数据
        Map params = new HashMap();
        params.put("type", 1);
        List<Device> deviceList = deviceService.selectObjByMap(params);

        if (CollUtil.isNotEmpty(deviceList)) {
            List<String> deviceTypeList = new ArrayList<>();
            deviceList.forEach(device -> {
//                DeviceType deviceType = this.deviceTypeService.selectObjById(device.getDeviceTypeId());
//                deviceTypeList.add(deviceType.getAlias());
                DeviceVendor deviceVendor = this.deviceVendorService.selectObjById(device.getDeviceVendorId());
                deviceTypeList.add(deviceVendor.getAlias());
            });
            // 根据设备获取logstash配置文件名称
            List<LogstashConfig> list = loggerMapper.queryByName(deviceTypeList);
            if (CollUtil.isNotEmpty(list)) {
                return list.stream().map(LogstashConfig::getConfPath).collect(Collectors.toList());
            }
        }
        return null;
    }
}
