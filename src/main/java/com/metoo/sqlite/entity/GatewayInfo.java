package com.metoo.sqlite.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-27 22:59
 */
@Data
@Accessors
@AllArgsConstructor
@NoArgsConstructor
public class GatewayInfo {

    @ApiModelProperty("Id")
    private Integer id;
    @ApiModelProperty("创建时间")
    private String createTime;

    private String port;
    private String ip_address;
    private String description;
    private String operator;
    private String ipv6_address;
    private String ipv6_subnet;

    private String deviceName;
}
