package com.metoo.sqlite.utils.license;

import com.metoo.sqlite.utils.Global;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

@Component
public class AesEncryptUtils {

    //可配置到Constant中，并读取配置文件注入,16位,自己定义
//    private static final String KEY = "@NPzwDvPmCJvpYuE";

    //参数分别代表 算法名称/加密模式/数据填充方式
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

//    AES支持三种长度的密钥：128位、192位、256位。
    /**
     * 加密
     * @param content 加密的字符串
     * @param encryptKey key值
     * @return
     * @throws Exception
     */
    public static String encrypt(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));
        byte[] b = cipher.doFinal(content.getBytes("UTF-8"));
        // 采用base64算法进行转码,避免出现中文乱码
        return Base64.encodeBase64String(b);

    }

    /**
     * 解密
     * @param encryptStr 解密的字符串
     * @param decryptKey 解密的key值
     * @return
     * @throws Exception
     */
//    public static String decrypt(String encryptStr, String decryptKey) throws Exception {
//        KeyGenerator kgen = KeyGenerator.getInstance("AES");
//        kgen.init(128);
//        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
//        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
//        // 采用base64算法进行转码,避免出现中文乱码
//        byte[] encryptBytes = Base64.decodeBase64(encryptStr.getBytes("UTF-8"));
//        byte[] decryptBytes = cipher.doFinal(encryptBytes);
//        return new String(decryptBytes);
//    }

    public static String decrypt(String encryptStr, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));

        // 采用base64算法进行转码,避免出现中文乱码
        byte[] encryptBytes = Base64.decodeBase64(encryptStr.getBytes("UTF-8"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);

        return new String(decryptBytes);
    }


    public String encrypt(String content) throws Exception {
        return encrypt(content, Global.AES_KEY);
    }
    public String decrypt(String encryptStr) throws Exception {
        return decrypt(encryptStr, Global.AES_KEY);
    }

    public static String encrypt1(String content) throws Exception {
        return encrypt(content, Global.AES_KEY);
    }
    public static String decrypt1(String encryptStr) throws Exception {
        return decrypt(encryptStr, Global.AES_KEY);
    }

}
