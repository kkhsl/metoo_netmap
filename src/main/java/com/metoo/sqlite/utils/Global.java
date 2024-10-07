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

    public static String cf_scanner_name;
    @Value("${cf_scanner_name}")
    public void setCf_scanner_name(String cf_scanner_name) {
        Global.cf_scanner_name = cf_scanner_name;
    }

    public static String os_scanner;
    @Value("${os_scanner}")
    public void setOs_scanner(String os_scanner) {
        Global.os_scanner = os_scanner;
    }

    public static String os_scanner_name;
    @Value("${os_scanner_name}")
    public void setOs_scanner_name(String os_scanner_name) {
        Global.os_scanner_name = os_scanner_name;
    }

    public static String os_scanner_result_path;
    @Value("${os_scanner_result_path}")
    public void setOs_scanner_result_path(String os_scanner_result_path) {
        Global.os_scanner_result_path = os_scanner_result_path;
    }


    public static String os_scanner_result_name;
    @Value("${os_scanner_result_name}")
    public void setOs_scanner_result_name(String os_scanner_result_name) {
        Global.os_scanner_result_name = os_scanner_result_name;
    }

    public static String encrypt_path;
    @Value("${encrypt_path}")
    public void setEncrypt_path(String encrypt_path) {
        Global.encrypt_path = encrypt_path;
    }

    public static String py_name;
    @Value("${py_name}")
    public void setPy_name(String py_name) {
        Global.py_name = py_name;
    }

    public static String versionUrl;
    @Value("${versionUrl}")
    public void setVersionUrl(String versionUrl) {
        Global.versionUrl = versionUrl;
    }

    public static String versionNumUrl;
    @Value("${versionNumUrl}")
    public void setVersionNumUrl(String versionNumUrl) {
        Global.versionNumUrl = versionNumUrl;
    }


    public static String versionPath;
    @Value("${version.path}")
    public void setVersionPath(String versionPath) {
        Global.versionPath = versionPath;
    }

    public static String versionName;
    @Value("${version.name}")
    public void setVersionName(String versionName) {
        Global.versionName = versionName;
    }

    public static String versionUnzip;
    @Value("${version.unZip}")
    public void setVersionUnzip(String versionUnzip) {
        Global.versionUnzip = versionUnzip;
    }

    public static String versionScriptName;
    @Value("${version.script.name}")
    public void setVersionScriptName(String versionScriptName) {
        Global.versionScriptName = versionScriptName;
    }

    public static String muyun_scheme;
    @Value("${muyun.scheme}")
    public void setMuyun_scheme(String muyun_scheme) {
        Global.muyun_scheme = muyun_scheme;
    }

    public static String muyun_username;
    @Value("${muyun.username}")
    public void setMuyun_username(String muyun_username) {
        Global.muyun_username = muyun_username;
    }

    public static String muyun_password;
    @Value("${muyun.password}")
    public void setMuyun_password(String muyun_password) {
        Global.muyun_password = muyun_password;
    }

    public static String muyun_prefix;
    @Value("${muyun.prefix}")
    public void setMuyun_prefix(String muyun_prefix) {
        Global.muyun_prefix = muyun_prefix;
    }


    public static String version_state;
    @Value("${version.state}")
    public void setVersion_stateversion_state(String version_state) {
        Global.version_state = version_state;
    }


    public static String version_state_name;
    @Value("${version.state.name}")
    public void setVersion_state_name(String version_state_name) {
        Global.version_state_name = version_state_name;
    }

    public static String version_info_name;
    @Value("${version.info.name}")
    public void setVersion_info_name(String version_info_name) {
        Global.version_info_name = version_info_name;
    }

    public static boolean isConcurrent;
    @Value("${isConcurrent}")
    public void setIsConcurrent(boolean isConcurrent) {
        Global.isConcurrent = isConcurrent;
    }

    public static String resultFile;
    @Value("${result.file}")
    public void setResultFile(String resultFile) {
        Global.resultFile=resultFile;
    }

    public static final String PY_SUFFIX_ALIVEINT = "aliveint"; // 端口ipv4
    public static final String PY_SUFFIX_IPV4 = "arp";// ipv4
    public static final String PY_SUFFIX_IPV6_NEIGHBORS = "ipv6_neighbors";// ipv6
    public static final String PY_SUFFIX_PING = "ping";// ping
    public static final String PY_SUFFIX_GET_SWITCH = "get_switch";// 设备表
    public static final String PY_SUFFIX_GET_FIREWALL = "get_firewall";// metoo_gateway_info


    public static final String IPV4_PANABIT = "IPV4_PANABIT";// ipv4
    public static final String IPV6_PANABIT = "IPV6_PANABIT";// ipv6

    public static final String SUBNET_IPV6 = "subnetIpv6";// ipv6网段

    public static final String ARP = "ipv4/ipv6";// arp

    public static final String DEVICE_SCAN = "device_scan";// device_scan表


    public static final String TERMINAL = "terminal";// 终端表

    public static final String GATEWAY_OPERATOR = "gateway_operator";// 终端表

    public static final String IPV4_ARP = "fw_arp";
    public static final String IPV6_ARP = "fw_ipv6_neighbors";
    /**
     * arp，ipv6 neighbors，alivein 所有采集入口
     */
    public static final String ALL_IN_ONE = "0";
}
