package com.metoo.sqlite.manager;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.Version;
import com.metoo.sqlite.manager.api.ApiService;
import com.metoo.sqlite.manager.utils.file.FileVersionUtils;
import com.metoo.sqlite.service.IVersionService;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.utils.version.DownloadAndExecuteBatFromZip;
import com.metoo.sqlite.vo.Result;
import lombok.ToString;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;


@RestController
@RequestMapping("/admin/application/version")
public class VersionManagerController {

    @Autowired
    private IVersionService versionService;

    @GetMapping
    public Result get(){
        Version currentVersion = this.versionService.selectObjByOne();
        String version = this.getVersionInfo();
        Map data = new HashMap();
        data.put("currentVersion", "");
        if(currentVersion != null){
            data.put("currentVersion", currentVersion.getVersion());
        }
        data.put("version", version);
        return ResponseUtil.ok(data);
    }


    private final ReentrantLock lock = new ReentrantLock();

    private Thread threadVersion;

    /**
     * 手动下载
     *
     * 手动写入文件记录下载信息
     *   下载状态
     *   下载时间戳
     *
     */
    @GetMapping("/maual/update")
    public Result maualUpdat() throws InterruptedException {

        if (lock.tryLock()) {

            // 保存当前线程的引用
            threadVersion = Thread.currentThread();

           // 初始为1，开始下载
            boolean state = FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state_name, "1");

            if(!state){
                return ResponseUtil.error("更新失败");
            }

            // 读取本地版本，以及远程版本信息
            try {
                String version = this.getVersionInfo();

                Version obj = this.versionService.selectObjByOne();

                if(obj != null){
                    String localVersion = obj.getVersion();
                    if(StringUtil.isNotEmpty(localVersion) && localVersion.compareTo(version) >= 0){
                        return ResponseUtil.ok("已是最新版本");
                    }
                }

                // 判断版本号是否以下载
                boolean updateFlag = true;
                String readState = readUpdateState();
                String downVersion = readUpdateVersion();
                if(StringUtil.isNotEmpty(readState)
                        && Math.abs(Integer.parseInt(readState)) >= 1){
                    if(StringUtil.isNotEmpty(downVersion) && downVersion.compareTo(version) >= 0){
                        // 不在执行下载
                        updateFlag = false;
                    }
                }


                // 写入文件，开始下载：0
//                state = writeUpdateState("0");

                if(updateFlag){

                    if(!state){
                        return ResponseUtil.badArgument("更新失败");
                    }

                    state = FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state_name, "1");

                    // 开始下载
                    try {

                        DownloadAndExecuteBatFromZip.versionUpdate();

                        state = FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state_name, "2");// 下载完成

                    } catch (IOException e) {
                        e.printStackTrace();
                        state = FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state_name, "-2");
                        return ResponseUtil.badArgument(-2,"更新失败");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        state = FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state_name, "-2");
                        return ResponseUtil.badArgument(-2,"更新失败");
                    }

                }else{

                    state = FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state_name, "1");
                }

//        if(StringUtil.isNotEmpty(readState)
//                && Math.abs(Integer.parseInt(readState)) >= 3){
// 开始执行脚本


//            String extractDirectory = Global.versionUnzip;
//            DownloadAndExecuteBatFromZip.execVBS(extractDirectory, Global.versionScriptName);

                try {
                    // 执行
                    String extractDirectory = Global.versionUnzip;
                    // 执行 .bat 文件
                    String batFilePath = extractDirectory + File.separator + Global.versionScriptName;

                    DownloadAndExecuteBatFromZip.executeBatchFile(batFilePath);

                    state = writeUpdateState("3");
                    writeUpdateVersion(version);

                } catch (IOException e) {
                    e.printStackTrace();

                    state = FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state, "-3");
                    return ResponseUtil.badArgument(-3,"更新失败");
                } catch (InterruptedException e) {
                    e.printStackTrace();

                    state = FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state, "-3");
                    return ResponseUtil.badArgument(-3,"更新失败");
                }
//        }

//        Map data = new HashMap();
//        data.put("state", state);
                return ResponseUtil.ok(200,"更新完成");
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return ResponseUtil.error(1003, "更新失败");
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
                if(threadVersion != null){
                    // 清空线程引用
                    threadVersion = null;
                }
            }
        }else{
            return ResponseUtil.ok(1002, "正在更新");
        }
    }

    @GetMapping("/cancel")
    public Result cancel() {
        if (threadVersion != null) {
            threadVersion.interrupt();
            return ResponseUtil.ok();
        } else {

            return ResponseUtil.error();
        }
    }


    @GetMapping("/version/info")
    public Result versionInfo(){

        String version = getVersionInfo();

        return ResponseUtil.ok(version);

    }

    /**
     * 获取下载状态
     *
     */
    @GetMapping("/update/state")
    public Result getUpdateState(){
        Map data = new HashMap();
        String state = readUpdateState();
        if(StringUtil.isNotEmpty(state)){
            data.put("state", state);
            return ResponseUtil.ok(data);
        }
        return ResponseUtil.ok();
    }


    /**
     * 查询版本信息
     *
     */
    public String getVersionInfo(){
        String url = Global.versionNumUrl;
        RestTemplate restTemplate = new RestTemplate();
        ApiService apiService = new ApiService(restTemplate);
        String result = apiService.callApi(url);
        JSONObject json = JSONObject.parseObject(result, JSONObject.class);
        if(json != null){
            String version = json.getString("version");
            return version;
        }
        return "";
    }

    /**
     * 手动写入文件状态
     *
     */
    public static boolean writeUpdateState(String state) {
        String data = state;

        Path projectDir = Paths.get("").toAbsolutePath();

        Path uploadDir = projectDir.resolve("C:\\patch\\vs\\state");

        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            System.err.println("创建目录失败: " + e.getMessage());
            return false;
        }

        Path filePath = uploadDir.resolve("stt.txt");
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), StandardCharsets.UTF_8))) {
        // 使用 BufferedWriter 和 OutputStreamWriter 写入文件
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath.toFile()), StandardCharsets.UTF_8))) {
            writer.write(data);
            return true;
        } catch (IOException e) {
            System.err.println("写入文件失败: " + e.getMessage());
            return false;
        }
    }

    public static boolean writeUpdateVersion(String state) {
        String data = state;

        Path projectDir = Paths.get("").toAbsolutePath();

        Path uploadDir = projectDir.resolve("C:\\patch\\vs\\state");

        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            System.err.println("创建目录失败: " + e.getMessage());
            return false;
        }

        Path filePath = uploadDir.resolve("vsn.txt");
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), StandardCharsets.UTF_8))) {
        // 使用 BufferedWriter 和 OutputStreamWriter 写入文件
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath.toFile()), StandardCharsets.UTF_8))) {
            writer.write(data);
            return true;
        } catch (IOException e) {
            System.err.println("写入文件失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 读取文件信息
     */
    public String readUpdateState(){

//        Path projectDir = Paths.get("").toAbsolutePath();
//
//        Path uploadDir = projectDir.resolve("C:\\netmap\\vs\\state\\ver.txt");

//        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\netmap\\vs\\state\\ver.txt"))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
//            return line;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "";

        String filePath = "C:\\patch\\vs\\state\\stt.txt";
        File file = new File(filePath);

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
            System.out.println("File does not exist: " + filePath);
        }
        return "";
    }

    /**
     * 读取文件信息
     */
    public String readUpdateVersion(){
        String filePath = "C:\\patch\\vs\\state\\vsn.txt";
        File file = new File(filePath);

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
            System.out.println("File does not exist: " + filePath);
        }
        return "";
    }


    public static void main(String[] args) throws IOException, InterruptedException {
//        Path projectDir = Paths.get("").toAbsolutePath();
//        Path uploadDir = projectDir.resolve("C:\\patch\\vs\\state");
//        System.out.println(uploadDir.toString());

        System.out.println(1 / 0);
//        DownloadAndExecuteBatFromZip.executeBatchFile("D:\\metoo\\project\\metoo\\metoo_netmap_monitor\\files\\1725472890723\\update.vbs");
    }



}
