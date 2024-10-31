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
 * @date 2024-06-22 14:47
 */
@ApiModel("设备")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Device {

    @ApiModelProperty("Id")
    private Integer id;
    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("设备名称")
    private String name;
    @ApiModelProperty("设备ip")
    private String ip;

    @ApiModelProperty("登录方式")
    private String loginType;
    @ApiModelProperty("登录端口")
    private String loginPort;
    @ApiModelProperty("登录名称")
    private String loginName;
    @ApiModelProperty("登录密码")
    private String loginPassword;

    @ApiModelProperty("设备类型Id")
    private Integer deviceTypeId;
    private String deviceTypeName;
    private String deviceTypeAlias;

    @ApiModelProperty("设备品牌Id")
    private Integer deviceVendorId;
    private String deviceVendorName;
    private String deviceVendorAlias;
    private Integer deviceVendorSequence;
    @ApiModelProperty("设备型号Id")
    private Integer deviceModelId;
    private String deviceModelName;

    @ApiModelProperty("设备UUID")
    private String uuid;
    @ApiModelProperty("设备类型 0：普通设备 1：日志采集设备")
    private Integer type;


    private String model;
    private String version;
    private String ipv6_keyword;
    private String ipv6_address;

    private String ipv6Addrcount;
    private String ipv6Forward;


    private String sentlocally;
    private String neighboradverts;

    private boolean state;


}
