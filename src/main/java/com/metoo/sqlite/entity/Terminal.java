package com.metoo.sqlite.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-28 17:50
 */
@ApiModel("")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Terminal {

    @ApiModelProperty("Id")
    private Integer id;
    @ApiModelProperty("创建时间")
    private String createTime;

    private String mac;
    private String ipv4addr;
    private String ipv6addr;
    private String active_port;
    private String macvendor;
    private String service;
    private String os;

}
