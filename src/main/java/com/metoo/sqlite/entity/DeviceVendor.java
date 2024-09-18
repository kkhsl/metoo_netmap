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
 * @date 2024-06-22 14:51
 */
@ApiModel("设备品牌")
@Data
@Accessors
@AllArgsConstructor
@NoArgsConstructor
public class DeviceVendor {

    @ApiModelProperty("Id")
    private Integer id;
    @ApiModelProperty("品牌名称")
    private String name;
    @ApiModelProperty("品牌别名")
    private String alias;
    @ApiModelProperty("排序")
    private Integer sequence;

    private Integer deviceTypeId;
    private String deviceTypeAlias;
}
