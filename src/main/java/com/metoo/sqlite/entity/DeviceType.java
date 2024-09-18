package com.metoo.sqlite.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-22 14:51
 */
@ApiModel("设备类型")
@Data
@Accessors
@AllArgsConstructor
@NoArgsConstructor
public class DeviceType {

    @ApiModelProperty("Id")
    private Integer id;
    @ApiModelProperty("设备类型名称")
    private String name;
    @ApiModelProperty("设备类型别名")
    private String alias;
    private String alias2;
    @ApiModelProperty("排序")
    private Integer sequence;

    private List<DeviceVendor> deviceVendorList = new ArrayList<>();


}
