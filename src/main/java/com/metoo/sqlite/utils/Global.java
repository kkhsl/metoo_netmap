package com.metoo.sqlite.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Global {

    private static Global global = new Global();

    public Global() {
    }

    public static Global getInstance() {
        return global;
    }


    public final static String AES_KEY = "@NPzwDvPmCJvpYuE";

    public static String env;

    @Value("${spring.profiles.active}")
    public void setEnv(String env) {
        Global.env = env;
    }

    public static String PYPATH;

    @Value("${PYPATH}")
    public void setPYPATH(String PYPATH) {
        Global.PYPATH = PYPATH;
    }


    public static String cf_scanner;
    @Value("${cf_scanner}")
    public void setCf_scanner(String cf_scanner) {
        Global.cf_scanner = cf_scanner;
    }

    public static String os_scanner;
    @Value("${os_scanner}")
    public void setOs_scanner(String os_scanner) {
        Global.os_scanner = os_scanner;
    }


    public static final String PY_SUFFIX_ALIVEINT = "aliveint"; // portIpv4
    public static final String PY_SUFFIX_IPV4 = "arp";// ipv4
    public static final String PY_SUFFIX_IPV6_NEIGHBORS = "ipv6_neighbors";// ipv6
    public static final String PY_SUFFIX_PING = "ping";// ping
    public static final String PY_SUFFIX_GET_SWITCH = "get_switch";// 设备表
    public static final String PY_SUFFIX_GET_FIREWALL = "get_firewall";// metoo_gateway_info

    public static final String SUBNET_IPV6 = "subnetIpv6";// ipv6网段

    public static final String ARP = "ipv4/ipv6";// arp

    public static final String DEVICE_SCAN = "device_scan";// device_scan表

    public static final String TERMINAL = "terminal";// 终端表


    public static final String GATEWAY_OPERATOR = "gateway_operator";// 终端表


}
