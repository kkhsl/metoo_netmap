package com.metoo.sqlite.entity;

import lombok.Data;

/**
 * logstash配置表
 * @author zzy
 * @version 1.0
 * @date 2024/10/18 9:32
 */
@Data
public class LogstashConfig {
    /**
     * 设备类型
     */
    private String deviceType;
    /**
     * 设备配置文件路径
     */
    private String confPath;
}
