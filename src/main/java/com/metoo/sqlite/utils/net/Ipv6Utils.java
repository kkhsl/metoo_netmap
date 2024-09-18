package com.metoo.sqlite.utils.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class Ipv6Utils {

    public static void main(String[] args) {

        String ip = "192.168.1.1";
        System.out.println(ip = "[" + ip.concat("]"));
        try {
            String[] addresses = {
                    "240e:670:3e02:6"
            };

            for (String address : addresses) {
                if (isValidIPv6(address)) {
                    InetAddress inetAddress = InetAddress.getByName(address);
                    System.out.println("Standardized address: " + inetAddress.getHostAddress());
                } else {
                    System.out.println("Invalid IPv6 address: " + address);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }



    private static final Pattern IPV6_PATTERN = Pattern.compile(
            "([0-9a-fA-F]{1,4}:){7}([0-9a-fA-F]{1,4}|:)|" +
                    "([0-9a-fA-F]{1,4}:){1,7}:|" +
                    "([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|:" +
                    "([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|:" +
                    "([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|:" +
                    "([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|:" +
                    "([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|:" +
                    ":((:[0-9a-fA-F]{1,4}){1,6}|:)|" +
                    "fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}" +
                    "((25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.){1,3}" +
                    "(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)|" +
                    "([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,4}"
    );

    /**
     * 验证 IPv6 地址格式
     */
    public static boolean isValidIPv6(String address) {
        return IPV6_PATTERN.matcher(address).matches();
    }
}
