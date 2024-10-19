package com.metoo.sqlite.utils.file;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

/**
 * 文件工具类
 * @author zhaozhiyuan
 * @version 1.0
 * @date 2024/10/13 11:31
 */
@UtilityClass
@Slf4j
public class FileUtils {
    /**
     * 删除指定目录下的所有文件和子目录，但保留目录本身。
     *
     * @param dirPath 要清空的目录路径
     * @throws IOException 如果发生 I/O 错误
     */
    public static void clearDirectory(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        // 确保目录存在
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("当前路径不是目录: " + path);
        }

        // 遍历目录下的文件和子目录
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // 删除文件
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                // 删除目录
                if (!dir.equals(dirPath)) {
                    Files.delete(dir);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 删除目录下指定的文件类型
     * @param dirPath
     * @param fileType
     * @throws IOException
     */
    public static void clearDirectoryWithFileType(String dirPath,String fileType)  {
        Path path = Paths.get(dirPath);
        // 确保目录存在
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("当前路径不是目录: " + dirPath);
        }
        try (Stream<Path> paths = Files.walk(path)) {
            paths
                    .filter(Files::isRegularFile)
                    // 是否是同类型文件
                    .filter(filePath -> isImageFile(filePath.getFileName().toString(),fileType))
                    .forEach(filePath -> {
                        try {
                            Files.delete(filePath);
                            log.info("删除之前的图片文件: " + filePath);
                        } catch (IOException e) {
                            log.info("删除之前的图片文件失败: {}，{}",filePath,e);
                        }
                    });
        } catch (IOException e) {
            log.info("删除之前的图片文件失败: {}",e);
        }
    }

    /**
     * 判断文件是否为图片文件
     * @param fileName
     * @param fileType
     * @return
     */
    public static boolean isImageFile(String fileName,String fileType) {
        String lowerCaseFileName = fileName.toLowerCase();
        if (lowerCaseFileName.endsWith("." + fileType)) {
            return true;
        }
        return false;
    }
    /**
     * 复制文件并重命名-删除源文件
     * @param path
     * @param oldName
     * @param newName
     */
    public static void copyFileAndNewName(String path,String oldName,String newName){
        try {
            // 源文件路径
            Path sourcePath = Paths.get(path+ File.separator+oldName);
            // 目标文件路径（包括新名称）
            Path targetPath = Paths.get(path+ File.separator+newName);
            // 复制文件并覆盖（如果目标文件已存在）
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            // 删除源文件
            Files.delete(sourcePath);
        } catch (IOException e) {
            log.error("文件复制并重命名失败: " + e.getMessage());
        }
    }
    public static String getFileTypeName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }

    /**
     * 清空文件
     * @param filePath
     */
    public void clearFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath, false)) {
            writer.write("");
        } catch (IOException e) {
            log.info("清空{}文件错误：{}",filePath,e);
        }
    }

    public static void main(String[] args) {
        String path = "C:\\netmap\\logs\\output.json";
        clearFile(path);
    }
}
