package com.metoo.sqlite.utils.version;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class UnzipUtility {

    public static void main(String[] args) throws IOException {
        String batFilePath = "C:\\Users\\hkk\\Desktop\\cehui\\install\\update.zip";
        unzip(batFilePath, "C:\\Users\\hkk\\Desktop\\cehui\\install");
    }

    // 解压 ZIP 文件
    public static void unzip(String zipFilePath, String extractDirectory) throws IOException {
        log.info("开始解压");

        Path extractPath = Paths.get(extractDirectory);
        if (!Files.exists(extractPath)) {
            // 如果解压目录不存在，则创建
            Files.createDirectories(extractPath);
        }

        // 使用 UTF-8 解码 ZIP 文件
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath), StandardCharsets.UTF_8)) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                // 使用 Paths.get() 动态生成文件路径，适应不同操作系统
                Path filePath = Paths.get(extractDirectory, entry.getName());

                // 防止 Zip Slip 漏洞，确保文件路径不跳出目标目录
                if (!filePath.normalize().startsWith(extractPath)) {
                    throw new IOException("非法的 ZIP 条目: " + entry.getName());
                }

                if (!entry.isDirectory()) {
                    // 解压文件
                    extractFile(zipIn, filePath);
                } else {
                    // 如果是目录，则创建对应的目录
                    Files.createDirectories(filePath);
                }

                // 关闭当前 ZIP 条目，继续处理下一个
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
        log.info("解压完成");
    }

    // 解压单个文件
    private static void extractFile(ZipInputStream zipIn, Path filePath) throws IOException {
        // 确保父目录存在
        Files.createDirectories(filePath.getParent());

        try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(filePath))) {
            byte[] buffer = new byte[4096];
            int read;
            // 将 ZIP 中的文件数据写入目标文件
            while ((read = zipIn.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
        }
    }
}
