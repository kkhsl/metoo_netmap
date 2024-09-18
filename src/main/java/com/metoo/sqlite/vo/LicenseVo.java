package com.metoo.sqlite.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Licens验证方式
 *  第一种方式：系统文件
 *  第二种方式：注册表 *
 *  第三种方式：远程服务端
 */
@Data
@Accessors
@AllArgsConstructor
@NoArgsConstructor
public class LicenseVo {

    @ApiModelProperty("开始时间")
    private Long startTime;
    @ApiModelProperty("结束时间")
    private Long endTime;
    @ApiModelProperty("授权天数")
    private int licenseDay;
    @ApiModelProperty("License版本号")
    private String licenseVersion;

    @ApiModelProperty("已使用")
    private int useDay;
    @ApiModelProperty("未使用")
    private int surplusDay;

    //############################

    private String unit_id;

    private Integer area_id;

    private String version;

    @ApiModelProperty("城市")
    private String city;

    @ApiModelProperty("区/县")
    private String area;

    @ApiModelProperty("单位名称")
    private String unit;


}
