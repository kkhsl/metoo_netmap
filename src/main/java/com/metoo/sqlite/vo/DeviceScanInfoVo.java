package com.metoo.sqlite.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors
@NoArgsConstructor
public class DeviceScanInfoVo {

    private String ipv4Addr;
    private String ipv6Addr;
    private String os;

    public DeviceScanInfoVo(String ipv4Addr, String ipv6Addr, String os) {
        this.ipv4Addr = ipv4Addr;
        this.ipv6Addr = ipv6Addr;
        this.os = os;
    }
}
