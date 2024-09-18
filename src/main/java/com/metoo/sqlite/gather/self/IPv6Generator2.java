package com.metoo.sqlite.gather.self;

import java.net.InetAddress;
import java.util.Random;

public class IPv6Generator2 {

    public static InetAddress generateRandomIPv6(String cidr) {
        // 解析CIDR表示法来获取子网前缀
        String[] parts = cidr.split("/");
        String prefix = parts[0];
        int prefixLength = Integer.parseInt(parts[1]);

        // 将子网前缀转换为位掩码
        long[] mask = new long[2];
        for (int i = 0; i < 64; i++) {
            if (i < prefixLength) {
                mask[i / 64] |= 1L << (63 - i % 64);
            }
        }

        // 随机生成一个IPv6地址
        Random random = new Random();
        long[] address = new long[2];
        address[0] = random.nextLong();
        address[1] = random.nextLong();

        // 应用子网掩码
        address[0] &= mask[0];
        address[1] &= mask[1];

        // 将前缀添加到随机生成的地址
        address[0] |= Long.parseLong(prefix.substring(0, 16), 16);
        address[1] |= Long.parseLong(prefix.substring(16), 16);

        try {
            // 将长整数转换为IPv6地址字符串
            String ipv6Str = String.format(
                    "%016x%016x",
                    address[0],
                    address[1]
            );
            return InetAddress.getByName("0:" + ipv6Str);
        } catch (Exception e) {
            throw new RuntimeException("Error generating IPv6 address", e);
        }
    }

    public static void main(String[] args) {
        // 假设我们的子网是 240b:8160:3008:700::/64
        String cidr = "240b:8160:3008:700::/64";
        InetAddress ipv6Address = generateRandomIPv6(cidr);
        System.out.println("Random IPv6 address in subnet: " + ipv6Address.getHostAddress());
    }
}
