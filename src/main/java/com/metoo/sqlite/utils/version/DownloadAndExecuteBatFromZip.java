package com.metoo.sqlite.utils.version;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.Version;
import com.metoo.sqlite.manager.api.ApiService;
import com.metoo.sqlite.utils.Global;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.concurrent.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@Slf4j
public class DownloadAndExecuteBatFromZip {


    public static void main(String[] args) {
        // 启动你的 Java 应用程序逻辑
        // ... 你的应用程序代码 ...

        // 当你需要重启时，调用 restart() 方法
//        restart();

        String currentVersion = "0.0.4.9";
        String version = "1.1.0.0";
        if(currentVersion.compareTo(version) < 0){
            System.out.println(1);
        }
    }

    public static void restart() {
        try {
            // 调用 restart.bat 脚本
            String batFilePath = "C:\\Users\\hkk\\Desktop\\metoo\\restart\\restart.bat";
            Process process = Runtime.getRuntime().exec("cmd /c start /B" + batFilePath);

            // 不需要等待脚本执行完成，直接退出当前 Java 进程
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//
//    @Value("${version.url}")
//    public static String versionUrl;
//
//    @Value("${version,path}")
//    public static String versionPath;
//
//    @Value("${version.name}")
//    public static String versionName;
//
//    @Value("${version.unZip}")
//    public static String versionUnzip;

//    public static void main(String[] args) throws IOException, InterruptedException {
//
//
//        String zipUrl = Global.versionUrl;
//        String fileName = Global.versionName;
//        String zipFilePath = Global.versionPath + File.separator + fileName;
//        String extractDirectory = Global.versionUnzip;
//
////        String zipUrl = "http://127.0.0.1:8930/api/soft/version/download";
////        String fileName = "version.zip";
////        String zipFilePath = "C:\\Users\\hkk\\Desktop\\metoo\\update" + File.separator + fileName;
////        String filePath = "";
////        String extractDirectory = "C:\\Users\\hkk\\Desktop\\metoo\\update\\unzip\\";
//
//        // 下载压缩包
//        downloadFile(zipUrl, zipFilePath);
//
//        // 解压压缩包
//        unzip(zipFilePath, extractDirectory);
//
//        // 执行 .bat 文件
//        String batFilePath = extractDirectory + "update.bat";
//        executeBatchFile(batFilePath);
//
//        // 清理解压的文件（可选）
////        cleanup(extractDirectory);
//    }

    public static void executeBatchFile() throws IOException, InterruptedException {

        String extractDirectory = Global.versionUnzip;

        String batFilePath = extractDirectory + File.separator + Global.versionScriptName;
        executeBatchFile(batFilePath);
    }

    public static String verifyVersion(String currentVersion){
        String url = Global.versionNumUrl;
        RestTemplate restTemplate = new RestTemplate();
        ApiService apiService = new ApiService(restTemplate);
        String result = apiService.callApi(url);
        JSONObject json = JSONObject.parseObject(result, JSONObject.class);
        if(json != null){
            String version = json.getString("version");
            if(version != null && !version.isEmpty()){
                if(StringUtil.isEmpty(currentVersion)){
                    return version;
                }
                if(currentVersion.compareTo(version) < 0){
                    return version;
                }
            }
        }
        return "";
    }

    public static void versionUpdate() throws IOException, InterruptedException {

        String zipUrl = Global.versionUrl;
        String fileName = Global.versionName;
        String zipFilePath = Global.versionPath + File.separator + fileName;
        String extractDirectory = Global.versionUnzip;

        try {
            ensureDirectoryExists(Global.versionPath);
            System.out.println("目录已确认存在或创建成功：" + Global.versionPath);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("目录创建失败：" + ex.getMessage());
        }

        try {
            ensureDirectoryExists(Global.versionUnzip);
            System.out.println("目录已确认存在或创建成功：" + Global.versionUnzip);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("目录创建失败：" + ex.getMessage());
        }

        // 下载压缩包
        downloadFile(zipUrl, zipFilePath);

        // 解压压缩包
        unzip(zipFilePath, extractDirectory);



//        // 执行 .bat 文件
//        String batFilePath = extractDirectory + File.separator + Global.versionScriptName;
//        executeBatchFile(batFilePath);

        //        // 清理解压的文件（可选）
//        cleanup(extractDirectory);
    }

    public static void ensureDirectoryExists(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            System.out.println("目录不存在，已创建：" + dirPath);
        } else {
            // 如果存在，则删除目录及其内容
//            deleteDirectory(path);
            clearDirectory(path);
            Files.createDirectories(path);
            System.out.println("目录已存在：" + dirPath);
        }
    }


    /**
     * 删除指定目录下的所有文件和子目录，但保留目录本身。
     *
     * @param dirPath 要清空的目录路径
     * @throws IOException 如果发生 I/O 错误
     */
    public static void clearDirectory(Path dirPath) throws IOException {
        // 确保目录存在
        if (!Files.isDirectory(dirPath)) {
            throw new IllegalArgumentException("The specified path is not a directory: " + dirPath);
        }

        // 遍历目录下的文件和子目录
        Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
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

    // 递归删除目录及其内容
    private static void deleteDirectory(Path directory) throws IOException {
        if (Files.isDirectory(directory)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(directory)) {
                for (Path entry : entries) {
                    deleteDirectory(entry);
                }
            }
        }
        Files.delete(directory);
    }

//    private static void downloadFile(String fileUrl, String filePath) throws IOException {
//        URL url = new URL(fileUrl);
//        //在 Java 中使用 URLConnection 下载文件时，默认的连接超时时间是无限的，即程序会一直等待直到连接建立或超时。
//        // 然而，为了确保程序的健壮性和避免长时间的等待，可以通过设置连接超时时间来控制连接建立的最大时间。
//
//        //你可以使用 URLConnection 的 setConnectTimeout 方法来设置连接超时时间。以下是如何修改你的代码来设置连接超时时间的示例：
//        URLConnection connection = url.openConnection();
////        connection.setConnectTimeout(timeoutMillis); // 设置连接超时时间
//        try (InputStream inputStream = connection.getInputStream();
//            FileOutputStream outputStream = new FileOutputStream(filePath)) {
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                try {
////                    Thread.sleep(1000);
//                    Thread.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                outputStream.write(buffer, 0, bytesRead);
//            }
//        }
//    }

    private static void downloadFile(String fileUrl, String filePath) throws IOException {
        URL url = new URL(fileUrl);
        URLConnection connection = url.openConnection();
//        connection.setConnectTimeout(timeoutMillis); // 设置连接超时时间
        connection.setConnectTimeout(10000); // 设置连接超时时间
        connection.setReadTimeout(10000);    // 设置读取超时时间

        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[4096]; // 4KB 缓冲区
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
//
//        try (InputStream inputStream = connection.getInputStream();
//             FileOutputStream outputStream = new FileOutputStream(filePath)) {
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
////                try {
//////                    Thread.sleep(1000);
////                    Thread.sleep(1);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
//                outputStream.write(buffer, 0, bytesRead);
//            }
//        }
    }


    private static void unzip(String zipFilePath, String extractDirectory) throws IOException {

        log.info("unzip start");

        Path extractPath = Paths.get(extractDirectory);
        if (!Files.exists(extractPath)) {
            Files.createDirectories(extractPath);
        }

//        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath, StandardCharsets.UTF_8))
//        new InputStreamReader(new FileInputStream("file.txt"), StandardCharsets.UTF_8))
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath), StandardCharsets.UTF_8)) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                String filePath = extractDirectory + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    extractFile(zipIn, filePath);
                } else {
                    Files.createDirectories(Paths.get(filePath));
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
//        try (ZipFile zip = new ZipFile(zipFilePath, StandardCharsets.UTF_8)) {
//            Enumeration<? extends ZipEntry> entries = zip.entries();
//            while (entries.hasMoreElements()) {
//                ZipEntry entry = entries.nextElement();
////                System.out.println("File: " + entry.getName());
//                // Process the entry (e.g., read its content if it's a file)
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        log.info("unzip end");
}

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }


    public static void execVBS(String batFilePath, String name){
        try {
            ProcessBuilder pb = new ProcessBuilder("cscript", batFilePath, name);
            Process process = pb.start();

            // 读取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 等待脚本执行完成
            process.waitFor();

            // 获取退出值
            int exitValue = process.exitValue();
            System.out.println("Exit value: " + exitValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void executeBatchFile(String batFilePath) throws IOException, InterruptedException {
//        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", batFilePath);
//        processBuilder.redirectErrorStream(true);
//
//        Process process = processBuilder.start();

//        Process process = Runtime.getRuntime().exec("cmd /c start " + batFilePath);

        // 使用 Runtime.getRuntime().exec() 执行 .vbs 文件
//        Process process = Runtime.getRuntime().exec("cscript " + batFilePath);
//
//
//
//        System.exit(0); // 不需要等待脚本执行完成，直接退出当前 Java 进程

//        Process process = Runtime.getRuntime().exec("cmd /c start " + batFilePath);

//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
//        }
//
//        int exitCode = process.waitFor();
//        System.out.println("Batch file executed with exit code: " + exitCode);


//        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", batFilePath);
        ProcessBuilder processBuilder = new ProcessBuilder("cscript", batFilePath);

        try {
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Process exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test() {
//        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c",
//                "D:\\metoo\\install\\version2\\10\\update.bat");

        ProcessBuilder processBuilder = new ProcessBuilder("cscript",
                "D:\\metoo\\install\\version2\\10\\update.vbs");


        try {
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Process exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2(){
        String batFilePath = "D:\\metoo\\install\\version2\\10\\update.bat";
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", batFilePath);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                InputStream inputStream = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                int exitCode = process.waitFor();
                System.out.println("Process exited with code: " + exitCode);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        try {
            future.get(10, TimeUnit.MINUTES); // Set timeout for the process
        } catch (TimeoutException e) {
            System.err.println("Process timed out.");
            future.cancel(true);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    private static void cleanup(String directory) throws IOException {
        Files.walk(Paths.get(directory))
                .sorted(java.util.Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }
}
