package com.metoo.sqlite.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors
@NoArgsConstructor
public class DeviceInfoVo {

    private String model;
    private String type;
    private String brand;
    private String version;
    private String deviceName;
    private String ipv4Addr;
    private String ipv6Addr;
    private String ipv6Forward;
    private String ipv6Keywords;
    private String ipv6Addrcount;

    public DeviceInfoVo(String model, String type, String brand, String version, String deviceName, String ipv4Addr,
                        String ipv6Addr, String ipv6Forward, String ipv6Keywords, String ipv6Addrcount) {
        this.model = model;
        this.type = type;
        this.brand = brand;
        this.version = version;
        this.deviceName = deviceName;
        this.ipv4Addr = ipv4Addr;
        this.ipv6Addr = ipv6Addr;
        this.ipv6Forward = ipv6Forward;
        this.ipv6Keywords = ipv6Keywords;
        this.ipv6Addrcount = ipv6Addrcount;
    }
}
