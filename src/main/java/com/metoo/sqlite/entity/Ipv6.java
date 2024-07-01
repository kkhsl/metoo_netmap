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
@ApiModel("ipv6")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Ipv6 {

    @ApiModelProperty("Id")
    private Integer id;
    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("ip地址")
    private String ipv6_address;
    @ApiModelProperty("mac地址")
    private String ipv6_mac;
    @ApiModelProperty("端口")
    private String port;

    private String vid;
    private String type;
    private String age;
    private String vpninstance;;

    @ApiModelProperty("设备Uuid")
    private String deviceUuid;

    public Ipv6(String ipv6_address, String ipv6_mac, String port) {
        this.ipv6_address = ipv6_address;
        this.ipv6_mac = ipv6_mac;
        this.port = port;
    }

    public Ipv6(String ipv6_address, String ipv6_mac, String port, String vid, String type, String age, String vpninstance, String deviceUuid) {
        this.ipv6_address = ipv6_address;
        this.ipv6_mac = ipv6_mac;
        this.port = port;
        this.vid = vid;
        this.type = type;
        this.age = age;
        this.vpninstance = vpninstance;
        this.deviceUuid = deviceUuid;
    }
}
