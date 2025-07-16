package com.metoo.sqlite.api.service.impl;

import com.metoo.sqlite.api.dto.DeviceSysInfoDTO;
import com.metoo.sqlite.api.mapper.DeviceSysInfoMapper;
import com.metoo.sqlite.api.service.IDeviceSysInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class DeviceSysInfoServiceImpl implements IDeviceSysInfoService {

    private DeviceSysInfoMapper deviceSysInfoMapper;
    @Autowired
    public void setDeviceSysInfoMapper(DeviceSysInfoMapper deviceSysInfoMapper) {
        this.deviceSysInfoMapper = deviceSysInfoMapper;
    }

    @Autowired  // 在Spring 4.3+可以省略，如果有单个构造器
    public DeviceSysInfoServiceImpl(DeviceSysInfoMapper deviceSysInfoMapper) {
        this.deviceSysInfoMapper = deviceSysInfoMapper;
    }

    @Override
    public List<DeviceSysInfoDTO> query(String mac) {
        return this.deviceSysInfoMapper.query(mac);
    }

    @Override
    public int insert(DeviceSysInfoDTO deviceSysInfoDTO) {
        deviceSysInfoDTO.setAddTime(new Date());
        deviceSysInfoDTO.setUpdateTime(new Date());
        return deviceSysInfoMapper.insert(deviceSysInfoDTO);
    }

    @Override
    public int update(DeviceSysInfoDTO deviceSysInfoDTO) {
        return this.deviceSysInfoMapper.update(deviceSysInfoDTO);
    }
}
