package com.metoo.sqlite.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-24 18:01
 */
@ApiModel("ipv4网段")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Subnet {

    @ApiModelProperty("Id")
    private Integer id;
    @ApiModelProperty("创建时间")
    private String createTime;
    @ApiModelProperty("设备ip")
    private String ip;
    @ApiModelProperty("掩码位")
    private Integer mask;
    @ApiModelProperty("父级Id")
    private Integer parentId;
    @ApiModelProperty("父级Ip")
    private String parentIp;
    @ApiModelProperty("描述")
    private String description;

    private List<Subnet> subnetList;

    public Subnet(String ip) {
        this.ip = ip;
    }
}
