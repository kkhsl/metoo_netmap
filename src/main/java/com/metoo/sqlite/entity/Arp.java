package com.metoo.sqlite.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-25 9:41
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Arp {
    @ApiModelProperty("Id")
    private Integer id;
    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("ip地址")
    private String ip;
    private String ipv6;
    @ApiModelProperty("mac地址")
    private String mac;
    @ApiModelProperty("端口")
    private String port;
    private String deviceUuid;
    private String macVendor;

    public Arp(String ipv6, String mac, String port, String deviceUuid) {
        this.ipv6 = ipv6;
        this.mac = mac;
        this.port = port;
        this.deviceUuid = deviceUuid;
    }

    public Arp(String ip, String mac, String port, String deviceUuid, String macVendor) {
        this.ip = ip;
        this.mac = mac;
        this.port = port;
        this.deviceUuid = deviceUuid;
        this.macVendor = macVendor;
    }
}
