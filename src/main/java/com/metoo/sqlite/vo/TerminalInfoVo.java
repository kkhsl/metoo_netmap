package com.metoo.sqlite.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

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

    private String manufacturer;
    private String model;
    private String os1;
    private String os_name;
    private List<String> cpu = new ArrayList<>();
    private List<String> mac_addresses = new ArrayList<>(); // 改为驼峰命名


//    public TerminalInfoVo(String mac, String service, String activePort, String os, String ipv4Addr, String ipv6Addr, String macVender) {
//        this.mac = mac;
//        this.service = service;
//        this.activePort = activePort;
//        this.os = os;
//        this.ipv4Addr = ipv4Addr;
//        this.ipv6Addr = ipv6Addr;
//        this.macVender = macVender;
//    }


    public TerminalInfoVo(String mac, String service, String activePort, String os, String ipv4Addr, String ipv6Addr, String macVender, String manufacturer, String model, String os1, String os_name, List<String> cpu, List<String> mac_addresses) {
        this.mac = mac;
        this.service = service;
        this.activePort = activePort;
        this.os = os;
        this.ipv4Addr = ipv4Addr;
        this.ipv6Addr = ipv6Addr;
        this.macVender = macVender;
        this.manufacturer = manufacturer;
        this.model = model;
        this.os1 = os1;
        this.os_name = os_name;
        this.cpu = cpu;
        this.mac_addresses = mac_addresses;
    }
}
