package com.metoo.sqlite.utils.encryption;

import java.util.Base64;

public class Base64Utils {

    public static void main(String[] args) {
        // 原始数据
        String originalString = "apiuser:ufAPI@9588";

        // 编码
        String encodedString = Base64.getEncoder().encodeToString(originalString.getBytes());
        System.out.println("Encoded: " + encodedString);

        // 解码
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String decodedString = new String(decodedBytes);
        System.out.println("Decoded: " + decodedString);
    }

    public static String encode(String str){
        String encodedString = Base64.getEncoder().encodeToString(str.getBytes());
        return encodedString;
    }
}
