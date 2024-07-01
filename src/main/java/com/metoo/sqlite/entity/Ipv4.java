package com.metoo.sqlite.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-25 9:44
 */
@ApiModel("ipv4")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Ipv4 {

    @ApiModelProperty("Id")
    private Integer id;
    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("ip地址")
    private String ip;
    @ApiModelProperty("mac地址")
    private String mac;
    @ApiModelProperty("端口")
    private String port;

    private String vlan;
    private String aging;
    private String type;

    private String deviceUuid;

    public Ipv4(String ip, String mac) {
        this.ip = ip;
        this.mac = mac;
        this.port = port;
    }

    public Ipv4(String ip, String mac, String port) {
        this.ip = ip;
        this.mac = mac;
        this.port = port;
    }

    public Ipv4(String ip, String mac, String port, String vlan, String aging, String type, String deviceUuid) {
        this.ip = ip;
        this.mac = mac;
        this.port = port;
        this.vlan = vlan;
        this.aging = aging;
        this.type = type;
        this.deviceUuid = deviceUuid;
    }
}
