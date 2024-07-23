package com.metoo.sqlite.entity;

import io.swagger.annotations.Api;
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
public class GatherLog {

    @ApiModelProperty("Id")
    private Integer id;
    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("开始时间")
    private String beginTime;
    @ApiModelProperty("结束时间")
    private String endTime;
    @ApiModelProperty("采集方式 自动/手动")
    private String type;
    @ApiModelProperty("采集结果 成功/失败")
    private String result;
    @ApiModelProperty("采集数据，分析之后的数据")
    private String details;

    private String data;



}
