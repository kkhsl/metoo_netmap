package com.metoo.sqlite.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors
@NoArgsConstructor
public class TerminalInfoVo {

    private String mac;
    private String service;
    private String activePort;
    private String os;
    private String ipv4Addr;
    private String ipv6Addr;
    private String macVender;

    public TerminalInfoVo(String mac, String service, String activePort, String os, String ipv4Addr, String ipv6Addr, String macVender) {
        this.mac = mac;
        this.service = service;
        this.activePort = activePort;
        this.os = os;
        this.ipv4Addr = ipv4Addr;
        this.ipv6Addr = ipv6Addr;
        this.macVender = macVender;
    }
}
