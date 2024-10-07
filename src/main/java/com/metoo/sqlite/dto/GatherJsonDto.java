package com.metoo.sqlite.dto;

import com.metoo.sqlite.entity.Ipv4;
import com.metoo.sqlite.entity.Ipv6;
import com.metoo.sqlite.entity.PortIpv4;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@ApiModel("采集中间json对象DTO")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class GatherJsonDto {

    @ApiModelProperty("arp信息列表")
    private List<Ipv4> arp;

    @ApiModelProperty("aliveint信息列表")
    private List<PortIpv4AndIpv6Dto> aliveint;

    @ApiModelProperty("ipv6neighbors设备列表")
    private List<Ipv6> ipv6neighbors;
}