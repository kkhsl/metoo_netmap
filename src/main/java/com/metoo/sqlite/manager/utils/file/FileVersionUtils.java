package com.metoo.sqlite.manager.utils.file;

import com.github.pagehelper.Page;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileVersionUtils {

    private static String PATH = "C:\\patch\\vs\\state";
    private static String FILE_NAME = "stt.txt";
    private static String FILE_NAME_VSN = "vsn.txt";

    public static void main(String[] args) {
        readState(PATH, FILE_NAME);
        writeUpdateVersion(PATH, FILE_NAME_VSN, "0");
    }
    /**
     * 读取文件信息
     */
    public static String readState(String path, String fileName){
        File file = createFile(path, fileName);
        if(file != null) {
            // 检查文件是否存在
            if (file.exists() && file.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        return line;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("File does not exist: " + path);
            }
        }
        return "";
    }



    public static boolean writeUpdateVersion(String path, String fileName, String data) {
        File file = createFile(path, fileName);
        if(file != null){

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), StandardCharsets.UTF_8))) {
                writer.write(data);
                return true;
            } catch (IOException e) {
                System.err.println("写入文件失败: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    public static File createFile(String path, String fileName){
        Path projectDir = Paths.get("").toAbsolutePath();
        Path uploadDir = projectDir.resolve(path);
        if (!Files.exists(uploadDir)) {
            try {
                Files.createDirectories(uploadDir);
            } catch (IOException e) {
                System.err.println("创建目录失败: " + e.getMessage());
            }

        }

        Path filePath = uploadDir.resolve(fileName);

        File file = new File(filePath.toString());

        if (!file.exists() || !file.isFile()) {
            // 文件不存在，则创建文件
            try {
                boolean fileCreated = file.createNewFile();
                if (fileCreated) {
                    System.out.println("文件成功创建");
                    return file;
                } else {
                    System.out.println("文件创建失败");
                }
            } catch (IOException e) {
                System.err.println("创建文件失败: " + e.getMessage());
            }
        }else{
            return file;
        }
        return null;
    }
}
