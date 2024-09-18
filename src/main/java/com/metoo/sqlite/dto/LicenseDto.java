package com.metoo.sqlite.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LicenseDto {

    @ApiModelProperty("申请码,系统唯一序列号")
    private String systemSN;

    @ApiModelProperty("开始时间")
    private long startTime;

    @ApiModelProperty("结束时间")
    private long endTime;

    @ApiModelProperty("License类型 0：试用版 1，授权版 2：终身版")
    private String type;

    @ApiModelProperty("License版本号")
    private String licenseVersion;

    @ApiModelProperty("过期时间")
    private long expireTime;

    //#################################
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
