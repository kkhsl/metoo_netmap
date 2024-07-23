package com.metoo.sqlite.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors
@NoArgsConstructor
public class GatewayInfoVo {

    private String operate;
    private String port;
    private String ipv4Addr;
    private String ipv6Addr;
    private String device;

    public GatewayInfoVo(String operate, String port, String ipv4Addr, String ipv6Addr, String device) {
        this.operate = operate;
        this.port = port;
        this.ipv4Addr = ipv4Addr;
        this.ipv6Addr = ipv6Addr;
        this.device = device;
    }
}
