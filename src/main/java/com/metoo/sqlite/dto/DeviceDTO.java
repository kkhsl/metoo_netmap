package com.metoo.sqlite.dto;

import com.metoo.sqlite.dto.page.PageDto;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@ApiModel("用户DTO")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDTO extends PageDto<Device> {

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
    @ApiModelProperty("设备品牌Id")
    private Integer deviceVendorId;
    @ApiModelProperty("设备型号Id")
    private Integer deviceModelId;
}