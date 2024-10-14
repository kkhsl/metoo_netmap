package com.metoo.sqlite.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel("测绘日志输出子模块vo")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurveyingSubLogStatusVo {


    @ApiModelProperty("模块名称")
    private String name;

    @ApiModelProperty("采集状态 默认0：未采集 1：正在采集 2：采集完成 3：采集失败")
    private Integer status;

    @ApiModelProperty("错误图片地址")
    private String errorImgUrl;

}
