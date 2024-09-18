package com.metoo.sqlite.manager.utils;

import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

public class MacUtils {

    public static String getMac(String mac){
        if(isValidMACAddress(mac)){
            // 方法二：使用正则表达式匹配并提取前三段
            String[] sections = mac.split(":|-");
            String firstThreeSectionsRegex = sections[0] + ":" + sections[1] + ":" + sections[2];
            return firstThreeSectionsRegex;
        }
       return "";
    }

    private static final String MAC_ADDRESS_PATTERN =
            "([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2}|[0-9A-Fa-f]{12}";

    private static final Pattern MAC_ADDRESS_REGEX = Pattern.compile(MAC_ADDRESS_PATTERN);

    /**
     * 判断字符串是否为有效的 MAC 地址
     *
     * @param macAddress 要验证的字符串
     * @return 如果是有效的 MAC 地址，则返回 true；否则返回 false
     */
    public static boolean isValidMACAddress(String macAddress) {
        if (macAddress == null) {
            return false;
        }
        return MAC_ADDRESS_REGEX.matcher(macAddress).matches();
    }

    public static String generateRandomMacAddress() {
        Random random = new Random();
        String macAddress = String.format(Locale.ROOT, "%02X:%02X:%02X:%02X:%02X:%02X",
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)
        );
        return macAddress;
    }

    public static String getRandomMac(){
        String randomMac = generateRandomMacAddress();
        return randomMac;
    }

    public static void main(String[] args) {
        String randomMac = generateRandomMacAddress();
        System.out.println("Random MAC Address: " + randomMac);
    }
}
