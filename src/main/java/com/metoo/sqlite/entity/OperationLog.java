package com.metoo.sqlite.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-26 17:28
 */
@ApiModel("")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class OperationLog {

    @ApiModelProperty("Id")
    private Integer id;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("操作用户账号")
    private String account;
    @ApiModelProperty("操作用户姓名")
    private String name;
    @ApiModelProperty("操作动作 例如：访问某个页面、CRUD等")
    private String action;
    @ApiModelProperty("行为描述")
    private String desc;
    @ApiModelProperty("日志类型 默认0：操作日志 1：访问日志 2：登录日志")
    private String type;
    @ApiModelProperty("ip")
    private String ip;
}
