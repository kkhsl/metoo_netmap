package com.metoo.sqlite.utils.file;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataFileWrite {


    public static void main(String[] args) {
        // 要写入的字符串数据
        String data = "test";


//        String uploadDir = Paths.get("").toAbsolutePath().toString() + File.separator + "files" + File.separator + System.currentTimeMillis();
        // 项目所在目录的相对路径
        Path projectDir = Paths.get("").toAbsolutePath();

        // files/upload 文件夹的路径
        Path uploadDir = projectDir.resolve("files/upload");

        // 确保目录存在
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            System.err.println("创建目录失败: " + e.getMessage());
            return;
        }

        // 要写入的文件路径
        Path filePath = uploadDir.resolve("unencrypt.txt");

        // 将字符串数据写入文件
        try {
            Files.write(filePath, data.getBytes());
            System.out.println("数据写入成功: " + filePath);
        } catch (IOException e) {
            System.err.println("写入文件失败: " + e.getMessage());
        }
    }


    public static void write(String args, String fileName) {
        // 要写入的字符串数据
        String data = args;


//        String uploadDir = Paths.get("").toAbsolutePath().toString() + File.separator + "files" + File.separator + System.currentTimeMillis();
        // 项目所在目录的相对路径
        Path projectDir = Paths.get("").toAbsolutePath();

        // files/upload 文件夹的路径
        Path uploadDir = projectDir.resolve("files/upload");

        // 确保目录存在
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            System.err.println("创建目录失败: " + e.getMessage());
            return;
        }

        // 要写入的文件路径
//        Path filePath = uploadDir.resolve("unencrypt.txt");
        Path filePath = uploadDir.resolve(fileName);

       // 将字符串数据写入文件

        // 将字符串数据写入文件
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), StandardCharsets.UTF_8))) {
        // 使用 BufferedWriter 和 OutputStreamWriter 写入文件
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath.toFile()), StandardCharsets.UTF_8))) {
            writer.write(data);
            System.out.println("数据写入成功: " + filePath);
        } catch (IOException e) {
            System.err.println("写入文件失败: " + e.getMessage());
        }

//        try {
//            Files.write(filePath, data.getBytes());
//            System.out.println("数据写入成功: " + filePath);
//        } catch (IOException e) {
//            System.err.println("写入文件失败: " + e.getMessage());
//        }
    }
}
