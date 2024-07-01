package com.metoo.sqlite.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@ApiModel("")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SubnetIpv6 {

    @ApiModelProperty("Id")
    private Integer id;
    @ApiModelProperty("创建时间")
    private String createTime;
    private String ip;
    private Integer mask;
    private Integer parentId;
    private String parentIp;
    private List<SubnetIpv6> subnetList;
    private String description;

}
