package com.metoo.sqlite.api.mapper;

import com.metoo.sqlite.api.dto.DeviceSysInfoDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DeviceSysInfoMapper {

    List<DeviceSysInfoDTO> query(@Param("pattern") String mac);

    int insert(DeviceSysInfoDTO list);

    int update(DeviceSysInfoDTO list);

}
