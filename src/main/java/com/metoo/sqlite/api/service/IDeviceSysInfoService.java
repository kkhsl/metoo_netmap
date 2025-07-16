package com.metoo.sqlite.api.service;

import com.metoo.sqlite.api.dto.DeviceSysInfoDTO;

import java.util.List;

public interface IDeviceSysInfoService {

    List<DeviceSysInfoDTO> query(String mac);

    int insert(DeviceSysInfoDTO list);

    int update(DeviceSysInfoDTO list);
}
