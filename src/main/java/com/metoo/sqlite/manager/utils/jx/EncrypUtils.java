package com.metoo.sqlite.manager.utils.jx;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

/**
 * SM4是一种对称加密算法，也称为国密算法，由中国国家密码管理局设计。
 * 它是一种轻量级块密码算法，适用于多种加密场景，包括数据传输和存储安全等。
 * SM4算法具有高效、安全等特点，适合于在各种软硬件环境中使用
 *
 * 实现步骤：
 * 1，导入相应的加密库，比如Bouncy Castle。
 * 2，创建一个SM4加密/解密的实例。
 * 3，设置密钥和初始化向量（IV）。
 * 4，使用实例进行数据加密和解密操作
 *
 * 使用Bouncy Castle库来实现SM4加密算法。
 * 使用了128位的密钥和初始化向量（IV）。
 * 使用CBC模式进行加密，并且采用了填充方式（PaddedBufferedBlockCipher）来处理数据块大小不足的情况
 */

@Slf4j
public class EncrypUtils {

//    // 128位密钥
////    private static byte[] keyHex = Hex.decode("8521479630bacefc9517463820aaffef");
////    // 128位初始化向量
////    private static byte[] ivHex = Hex.decode("badefc96374185207abcdef357984610");

//    private static String keyHex = "3451276098bacefc7723456789aaffef";
//    private static String ivHex = "badefc33214568704abcdef564980721";

    public static String encrypt(String plainText) throws Exception {

        byte[] keyBytes = Hex.decode("3451276098bacefc7723456789aaffef");

        byte[] ivBytes = Hex.decode("badefc33214568704abcdef564980721");

        // 创建SM4算法实例
        SM4Engine engine = new SM4Engine();

        // CBC模式
        PaddedBufferedBlockCipher cipher=new PaddedBufferedBlockCipher(new CBCBlockCipher(engine));

        CipherParameters params = new ParametersWithIV(new KeyParameter(keyBytes), ivBytes);

        // 初始化加密
        cipher.init(true, params);

        // 加密数据
        /**
         * StandardCharsets.UTF_8：UTF-8编码，适合于多语言环境和大部分文本数据。
         * StandardCharsets.UTF_16：UTF-16编码，用于处理Unicode字符。
         * StandardCharsets.ISO_8859_1：ISO Latin-1编码，适合于西欧语言。
         */
        byte[] input = plainText.getBytes(StandardCharsets.UTF_8); // ENCODING

        byte[] output = new byte[cipher.getOutputSize(input.length)];

        int outputLength = cipher.processBytes(input, 0, input.length, output, 0);

        cipher.doFinal(output, outputLength);

        // 加密结果
        return Hex.toHexString(output).toUpperCase();
    }


    public static String decrypt(String encryptedText) throws InvalidCipherTextException {

        byte[] keyBytes = Hex.decode("3451276098bacefc7723456789aaffef");

        byte[] ivBytes = Hex.decode("badefc33214568704abcdef564980721");


//        // 创建SM4算法实例
//        SM4Engine engine = new SM4Engine();
//
//        // CBC模式
//        PaddedBufferedBlockCipher cipher=new PaddedBufferedBlockCipher(new CBCBlockCipher(engine));

        BlockCipher engine = new SM4Engine();
        // CBC模式
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(engine));

        // 初始化解密
        cipher.init(false, new ParametersWithIV(new KeyParameter(keyBytes), ivBytes));

        // 解密数据
        byte[] encryptedBytes = Hex.decode(encryptedText);

        byte[] decryptedBytes = new byte[cipher.getOutputSize(encryptedBytes.length)];
        int decryptedLength = cipher.processBytes(encryptedBytes, 0, encryptedBytes.length, decryptedBytes, 0);
        cipher.doFinal(decryptedBytes, decryptedLength);

        // 解密结果
        String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8).trim();

        return decryptedText;
    }

    // 加密  plaintext:明文信息
    public static String encrypt(String keyHex, String ivHex, String plainText) throws Exception {

        byte[] keyBytes = Hex.decode(keyHex);

        byte[] ivBytes = Hex.decode(ivHex);

        // 创建SM4算法实例
        SM4Engine engine = new SM4Engine();

        // CBC模式
        PaddedBufferedBlockCipher cipher=new PaddedBufferedBlockCipher(new CBCBlockCipher(engine));

        CipherParameters params = new ParametersWithIV(new KeyParameter(keyBytes), ivBytes);

        // 初始化加密
        cipher.init(true, params);

        // 加密数据
        /**
         * StandardCharsets.UTF_8：UTF-8编码，适合于多语言环境和大部分文本数据。
         * StandardCharsets.UTF_16：UTF-16编码，用于处理Unicode字符。
         * StandardCharsets.ISO_8859_1：ISO Latin-1编码，适合于西欧语言。
         */
        byte[] input = plainText.getBytes(StandardCharsets.UTF_8); // ENCODING

        byte[] output = new byte[cipher.getOutputSize(input.length)];

        int outputLength = cipher.processBytes(input, 0, input.length, output, 0);

        cipher.doFinal(output, outputLength);

        // 加密结果
        return Hex.toHexString(output).toUpperCase();
    }

    public static String decrypt(String keyHex, String ivHex, String encryptedText) throws InvalidCipherTextException {

        byte[] keyBytes = Hex.decode(keyHex);

        byte[] ivBytes = Hex.decode(ivHex);


//        // 创建SM4算法实例
//        SM4Engine engine = new SM4Engine();
//
//        // CBC模式
//        PaddedBufferedBlockCipher cipher=new PaddedBufferedBlockCipher(new CBCBlockCipher(engine));

        BlockCipher engine = new SM4Engine();
        // CBC模式
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(engine));

        // 初始化解密
        cipher.init(false, new ParametersWithIV(new KeyParameter(keyBytes), ivBytes));

        // 解密数据
        byte[] encryptedBytes = Hex.decode(encryptedText);

        byte[] decryptedBytes = new byte[cipher.getOutputSize(encryptedBytes.length)];
        int decryptedLength = cipher.processBytes(encryptedBytes, 0, encryptedBytes.length, decryptedBytes, 0);
        cipher.doFinal(decryptedBytes, decryptedLength);

        // 解密结果
        String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8).trim();

        return decryptedText;
    }

}
