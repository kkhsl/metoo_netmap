package com.metoo.sqlite.gather.self;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPv6AddressNormalizer {

    public static void main(String[] args) {
        String[] addresses = {
                "240e:670:3e02:6",  // 缺少部分块和前导零
                "240e:670:3e02:6::", // 缩写形式
                "240e:670:3e02:006", // 完整形式（手动补全）
        };

        for (String address : addresses) {
        String normalizedAddress = normalizeIPv6(address);
        System.out.println("Original address: " + address);
        System.out.println("Normalized address: " + normalizedAddress);
    }
}

    /**
     * 标准化 IPv6 地址
     */
    private static String normalizeIPv6(String address) {
        try {
            InetAddress inetAddress = InetAddress.getByName(address);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            // 如果地址格式不正确，则手动补全
            return expandIPv6(address);
        }
    }

    /**
     * 手动扩展 IPv6 地址
     */
    private static String expandIPv6(String address) {
        // 替换 "::" 为合适的零块
        if (address.contains("::")) {
            String[] parts = address.split("::");
            String left = parts[0];
            String right = parts.length > 1 ? parts[1] : "";

            String[] leftParts = left.split(":");
            String[] rightParts = right.split(":");

            String[] expanded = new String[8];
            int leftLength = leftParts.length;
            int rightLength = rightParts.length;
            int zerosToFill = 8 - (leftLength + rightLength);

            // 填充左边和右边
            for (int i = 0; i < leftLength; i++) {
                expanded[i] = leftParts[i];
            }
            for (int i = 0; i < zerosToFill; i++) {
                expanded[leftLength + i] = "0000";
            }
            for (int i = 0; i < rightLength; i++) {
                expanded[leftLength + zerosToFill + i] = rightParts[i];
            }

            // 格式化为完整的 IPv6 地址
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < expanded.length; i++) {
                if (i > 0) sb.append(":");
                sb.append(expanded[i]);
            }
            return sb.toString();
        }

        // 处理没有 "::" 的地址
        String[] parts = address.split(":");
        String[] expanded = new String[8];
        int length = parts.length;

        for (int i = 0; i < 8; i++) {
            if (i < 8 - length) {
                expanded[i] = "0000";
            } else {
                expanded[i] = parts[i - (8 - length)];
            }
        }

        // 格式化为完整的 IPv6 地址
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expanded.length; i++) {
            if (i > 0) sb.append(":");
            sb.append(expanded[i]);
        }
        return sb.toString();
    }
}
