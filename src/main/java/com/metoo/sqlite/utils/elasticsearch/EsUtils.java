package com.metoo.sqlite.utils.elasticsearch;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.metoo.sqlite.model.es.EsQueryService;
import com.metoo.sqlite.utils.Global;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * es相关工具类
 *
 * @author zzy
 * @version 1.0
 * @date 2024/10/17 22:04
 */
@Slf4j
@UtilityClass
public class EsUtils {
    public static List<Process> processList=new ArrayList<>();
    /**
     * 启动logstash服务
     *
     * @param logStashPath
     * @param confName
     */
    public static boolean startLogStash(String logStashPath, List<String> confName) {
        try {
            List<String> cmdList=new ArrayList<>();
            cmdList.add(logStashPath);
            confName.forEach(o->{
                cmdList.add("-f");
                cmdList.add(o);
            });
            // 构建启动Logstash的命令
            String[] command =cmdList.toArray(new String[0]);
            // 执行命令启动Logstash
            log.info("执行命令启动Logstash命令：{}", JSONObject.toJSONString(command));
            Process process = Runtime.getRuntime().exec(command);
            ThreadUtil.execAsync(()->{
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info("logstash=======:{}",line);
                    }
                } catch (IOException e) {
                    log.error("Error reading output from es process:{},{} ", command, e);
                }
            });
            ThreadUtil.execAsync(()->{
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.error("logstash=======:{}",line);
                    }
                } catch (IOException e) {
                    log.error("Error reading output from es process:{},{} ", command, e);
                }
            });
            log.info("启动es服务成功");
            processList.add(process);
            return true;
        } catch (Exception e) {
            log.error("启动logstash服务出错：{}", e);
            return false;
        }
    }

    /**
     * 启动es服务
     *
     */
    public static boolean startEs() {
        try {
            // 构建启动es的命令
            String[] command = {"cscript", "/nologo", Global.esStartPath};
            log.info("执行命令启动es命令：{}", JSONObject.toJSONString(command));
            Process process = Runtime.getRuntime().exec(command);
            ThreadUtil.execAsync(()->{
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info("es=======:{}",line);
                    }
                } catch (IOException e) {
                    log.error("Error reading output from es process:{},{} ", command, e);
                }
            });
            ThreadUtil.execAsync(()->{
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.error("es====error===:{}",line);
                    }
                } catch (IOException e) {
                    log.error("Error reading output from es process:{},{} ", command, e);
                }
            });
            log.info("启动es服务成功");
            processList.add(process);
            return true;
        } catch (Exception e) {
            log.error("启动es服务出错：{}", e);
            return false;
        }
    }

    /**
     * 结束执行命令流程
     */
    public void stopProcess(){
        try {
            // 停止elk服务
            log.info("ELK 服务开始停止");
            String[] command = {"cscript", "/nologo", Global.elkStopPath};
            Process process =  Runtime.getRuntime().exec(command);
            ThreadUtil.execAsync(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info(line);
                    }
                } catch (IOException e) {
                    log.error("Error reading output from es process:{} ", e);
                }
            });
            log.info("开始结束执行命令流程==========={}",processList.size());
            for (Process temp : processList) {
                try {
                    if (temp != null && temp.isAlive()) {
                        temp.destroy();
                    }
                } catch (Exception ex) {
                    log.error("结束执行命令失败：{}", ex);
                }
            }
            log.info("ELK 服务停止完成");
        } catch (Exception e) {
            log.error("ELK 服务停止出现错误：{}", e);
        }
    }

    public static void main(String[] args) {
        stopProcess();
    }
}
