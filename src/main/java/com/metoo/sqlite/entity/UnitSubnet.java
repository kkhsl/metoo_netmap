package com.metoo.sqlite.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * 根据单位，生成多个加密文件
 */
@ApiModel("单位-网段")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class UnitSubnet {

    @ApiModelProperty("Id")
    private Integer id;
    @ApiModelProperty("创建时间")
    private String createTime;

    private String name;
    private Integer unitId;

    private String ipv4Subnet;

    private String ipv6Subnet;

}
