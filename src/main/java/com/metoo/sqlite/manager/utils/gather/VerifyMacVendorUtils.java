package com.metoo.sqlite.manager.utils.gather;

import java.util.HashMap;
import java.util.Map;

public class VerifyMacVendorUtils {

    private static final Map<String, String> VENDOR_MAP_DEVICESCAN = new HashMap<>();
    static {
        VENDOR_MAP_DEVICESCAN.put("Tenda", "Tenda");
        VENDOR_MAP_DEVICESCAN.put("h3c", "h3c");
        VENDOR_MAP_DEVICESCAN.put("TP-LINK", "TP-LINK");
        VENDOR_MAP_DEVICESCAN.put("Lite-On", "Lite-On");
        VENDOR_MAP_DEVICESCAN.put("mercury", "mercury");
        VENDOR_MAP_DEVICESCAN.put("Huawei Device", "Huawei Device");
        VENDOR_MAP_DEVICESCAN.put("ruijie", "ruijie");
        VENDOR_MAP_DEVICESCAN.put("PUTIAN", "PUTIAN");
        VENDOR_MAP_DEVICESCAN.put("Hikvision", "Hikvision");
        VENDOR_MAP_DEVICESCAN.put("Dahua", "Dahua");
        VENDOR_MAP_DEVICESCAN.put("Ezviz", "Ezviz");
        VENDOR_MAP_DEVICESCAN.put("Panasonic", "Panasonic");
        VENDOR_MAP_DEVICESCAN.put("Logitech", "Logitech");
        VENDOR_MAP_DEVICESCAN.put("Tiandy", "Tiandy");
        VENDOR_MAP_DEVICESCAN.put("Cannon", "Cannon");
        VENDOR_MAP_DEVICESCAN.put("Samsung", "Samsung");
        VENDOR_MAP_DEVICESCAN.put("RICOH", "RICOH");
        VENDOR_MAP_DEVICESCAN.put("Epson", "Epson");
        VENDOR_MAP_DEVICESCAN.put("Brother", "Brother");
        VENDOR_MAP_DEVICESCAN.put("KONICA", "KONICA");
        VENDOR_MAP_DEVICESCAN.put("MINOLTA", "MINOLTA");
        VENDOR_MAP_DEVICESCAN.put("LEXMARK", "LEXMARK");
        VENDOR_MAP_DEVICESCAN.put("Sundray", "Sundray");
        VENDOR_MAP_DEVICESCAN.put("IEIT SYSTEMS", "IEIT SYSTEMS");
        VENDOR_MAP_DEVICESCAN.put("Topsec", "Topsec");
        VENDOR_MAP_DEVICESCAN.put("Sangfor", "Sangfor");
        VENDOR_MAP_DEVICESCAN.put("Jiangxi", "Jiangxi");
        VENDOR_MAP_DEVICESCAN.put("Xunte", "Xunte");
        VENDOR_MAP_DEVICESCAN.put("DPtech", "DPtech");
        VENDOR_MAP_DEVICESCAN.put("D-Link", "D-Link");
        VENDOR_MAP_DEVICESCAN.put("NETGEAR", "NETGEAR");
        VENDOR_MAP_DEVICESCAN.put("NETCORE", "NETCORE");
        VENDOR_MAP_DEVICESCAN.put("zte", "zte");
        VENDOR_MAP_DEVICESCAN.put("Fiberhome", "Fiberhome");
        VENDOR_MAP_DEVICESCAN.put("Ericsson", "Ericsson");
        VENDOR_MAP_DEVICESCAN.put("Cisco", "Cisco");
        VENDOR_MAP_DEVICESCAN.put("Juniper", "Juniper");
        VENDOR_MAP_DEVICESCAN.put("Brocade", "Brocade");
        VENDOR_MAP_DEVICESCAN.put("Extreme", "Extreme");
        VENDOR_MAP_DEVICESCAN.put("ProCurve", "ProCurve");
        VENDOR_MAP_DEVICESCAN.put("Maipu", "Maipu");
        VENDOR_MAP_DEVICESCAN.put("Venustech", "Venustech");
        VENDOR_MAP_DEVICESCAN.put("Shenou", "Shenou");
        VENDOR_MAP_DEVICESCAN.put("Communication", "Communication");
        VENDOR_MAP_DEVICESCAN.put("REALTEK", "REALTEK");
        VENDOR_MAP_DEVICESCAN.put("Lite-On", "LINK");
        VENDOR_MAP_DEVICESCAN.put("Sharp", "INK");
        VENDOR_MAP_DEVICESCAN.put("Tiger NetCom", "LINK");
        VENDOR_MAP_DEVICESCAN.put("AI-Link", "AI-Link");
        VENDOR_MAP_DEVICESCAN.put("Century Xinyang", "Century Xinyang");
        VENDOR_MAP_DEVICESCAN.put("BaoLun", "BaoLun");
        VENDOR_MAP_DEVICESCAN.put("Shiyuan Electronic", "Shiyuan Electronic");
        VENDOR_MAP_DEVICESCAN.put("SHENZHEN FAST", "SHENZHEN FAST");
        VENDOR_MAP_DEVICESCAN.put("AMPAK", "AMPAK");
        VENDOR_MAP_DEVICESCAN.put("Midea", "Midea");
        VENDOR_MAP_DEVICESCAN.put("ASIX ELECTRONICS", "ASIX ELECTRONICS");
        VENDOR_MAP_DEVICESCAN.put("Shanghai WDK Industrial", "Shanghai WDK Industrial");
        VENDOR_MAP_DEVICESCAN.put("Zhejiang Uniview", "Zhejiang Uniview");
        VENDOR_MAP_DEVICESCAN.put("Zhejiang Dahua", "Zhejiang Dahua");
        VENDOR_MAP_DEVICESCAN.put("SUNIX", "SUNIX");
        VENDOR_MAP_DEVICESCAN.put("YEALINK", "YEALINK");
        VENDOR_MAP_DEVICESCAN.put("Uniview", "Uniview");
        VENDOR_MAP_DEVICESCAN.put("Camille Bauer", "Camille Bauer");
        VENDOR_MAP_DEVICESCAN.put("Microchip", "Microchip");
        VENDOR_MAP_DEVICESCAN.put("Electronics", "Electronics");
        VENDOR_MAP_DEVICESCAN.put("Shenzhen Seavo", "Shenzhen Seavo");
        VENDOR_MAP_DEVICESCAN.put("Shenzhen WiSiYiLink", "Shenzhen WiSiYiLink");
        VENDOR_MAP_DEVICESCAN.put("Realme Chongqing Mobile", "Realme Chongqing Mobile");
        VENDOR_MAP_DEVICESCAN.put("ELECTRONIC", "ELECTRONIC");
        VENDOR_MAP_DEVICESCAN.put("Alcatel-Lucent", "Alcatel-Lucent");
        VENDOR_MAP_DEVICESCAN.put("Toshiba Teli", "Toshiba Teli");
        VENDOR_MAP_DEVICESCAN.put("Shenzhen Sunchip Technology", "Shenzhen Sunchip Technology");
        VENDOR_MAP_DEVICESCAN.put("DAVICOM SEMICONDUCTOR", "DAVICOM SEMICONDUCTOR");
        VENDOR_MAP_DEVICESCAN.put("openbsd", "openbsd");
        VENDOR_MAP_DEVICESCAN.put("Hangzhou Hikivison", "Hangzhou Hikivison");
        VENDOR_MAP_DEVICESCAN.put("Dahua", "Dahua");
    }
    public static String toDeviceScan(String vendor){

        String lowerVendor = vendor.toLowerCase();
        for (Map.Entry<String, String> entry : VENDOR_MAP_DEVICESCAN.entrySet()) {
            if (lowerVendor.contains(entry.getKey().toLowerCase())) {
                return entry.getKey();
            }
        }
        return "";
    }

    private static final Map<String, String> VENDOR_MAP_TERMINAL = new HashMap<>();
    static {
        VENDOR_MAP_TERMINAL.put("hewlett packard", "Hewlett Packard");
        VENDOR_MAP_TERMINAL.put("lcfc(hefei) electronics", "LCFC(Hefei) Electronics");
        VENDOR_MAP_TERMINAL.put("cloud", "CLOUD");
        VENDOR_MAP_TERMINAL.put("intel", "Intel");
        VENDOR_MAP_TERMINAL.put("vivo", "vivo");
        VENDOR_MAP_TERMINAL.put("xiaomi", "xiaomi");
        VENDOR_MAP_TERMINAL.put("oppo", "oppo");
        VENDOR_MAP_TERMINAL.put("apple", "apple");
        VENDOR_MAP_TERMINAL.put("honor", "Honor");
        VENDOR_MAP_TERMINAL.put("chicony", "Chicony");
        VENDOR_MAP_TERMINAL.put("liteon", "Liteon");
        VENDOR_MAP_TERMINAL.put("huawei technologies", "HUAWEI TECHNOLOGIES");
        VENDOR_MAP_TERMINAL.put("IEEE Registration", "IEEE Registration");
        VENDOR_MAP_TERMINAL.put("Shenzhen Decnta", "Shenzhen Decnta");
    }

    public static String toTerminal(String vendor) {
        if (vendor == null || vendor.isEmpty()) {
            return "";
        }

        String lowerCaseVendor = vendor.toLowerCase();

        for (Map.Entry<String, String> entry : VENDOR_MAP_TERMINAL.entrySet()) {
            if (lowerCaseVendor.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return "";
    }

}
