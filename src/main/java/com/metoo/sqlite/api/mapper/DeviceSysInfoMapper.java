package com.metoo.sqlite.api.mapper;

import com.metoo.sqlite.api.dto.DeviceSysInfoDTO;
import com.metoo.sqlite.core.config.mybatis.config.json.ListStringToJsonTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DeviceSysInfoMapper {

    @Select("SELECT * FROM metoo_device_sys_info WHERE mac_addresses LIKE '%' || '\"' || #{pattern} || '\"' || '%'")
    @Results({
            @Result(property = "mac_addresses", column = "mac_addresses", typeHandler = ListStringToJsonTypeHandler.class),
            @Result(property = "cpu", column = "cpu", typeHandler = ListStringToJsonTypeHandler.class)
    })
    List<DeviceSysInfoDTO> query(@Param("pattern") String mac);

    int insert(DeviceSysInfoDTO list);

    int update(DeviceSysInfoDTO list);

}
