package com.metoo.sqlite.mapper;


import com.metoo.sqlite.entity.LogstashConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LogstashConfigMapper {
    /**
     * 根据设备类型获取配置文件路径
     * @return
     */
    List<LogstashConfig> queryByName(@Param("names") List<String> names);

}
