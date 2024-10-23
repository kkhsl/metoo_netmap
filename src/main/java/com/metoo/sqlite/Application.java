package com.metoo.sqlite;

import cn.hutool.core.thread.ThreadUtil;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.SurveyingLog;
import com.metoo.sqlite.entity.Version;
import com.metoo.sqlite.gather.utils.VersionUtils;
import com.metoo.sqlite.manager.utils.file.FileVersionUtils;
import com.metoo.sqlite.service.ISurveyingLogService;
import com.metoo.sqlite.service.IVersionService;
import com.metoo.sqlite.service.impl.UpdateVersionService;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@EnableSwagger2
@MapperScan("com.metoo.sqlite.mapper")
@EnableScheduling // 开启定时任务（启动类增加该注解，使项目启动后执行定时任务）
@SpringBootApplication
public class Application  implements CommandLineRunner {

    public static void main(String[] args) {
        Long time=System.currentTimeMillis();
        SpringApplication.run(Application.class);
        System.out.println("===应用启动耗时："+(System.currentTimeMillis()-time)+"===");
        log.info(("===应用启动耗时："+(System.currentTimeMillis()-time)+"==="));
    }


    @Autowired
    private ISurveyingLogService surveyingLogService;
    @Autowired
    private IVersionService versionService;
    @Autowired
    private UpdateVersionService updateVersionService;
    @Override
    public void run(String... args) throws Exception {
        try {
            List<SurveyingLog> surveyingLogList = this.surveyingLogService.selectObjByMap(null);
            for (SurveyingLog surveyingLog : surveyingLogList) {
                if(surveyingLog.getStatus() == 1){
                    surveyingLog.setStatus(3);
                    this.surveyingLogService.update(surveyingLog);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            String state = FileVersionUtils.readState(Global.version_state, Global.version_state_name);

            if(StringUtil.isNotEmpty(state)){
                if(state.trim().equals("4")){
                    Version obj = null;
                    String version = FileVersionUtils.readState(Global.version_state, Global.version_info_name);
                    if(StringUtil.isNotEmpty(version)){
                        obj = this.versionService.selectObjByOne();
                        if(obj != null){
                            int matchResult = VersionUtils.compare(version, obj.getVersion());
                            if (matchResult > 0) {
                                obj.setVersion(version);
                                FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state_name, "0");
                                this.versionService.update(obj);
                            }
                        }else{
                            obj = new Version();
                            obj.setVersion(version);
                            this.versionService.save(obj);
                            FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state_name, "0");
                        }
                    }
                }else{
                    // 检测是否更新完毕，
                    FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state_name, "0");
                }
            }else{
                FileVersionUtils.writeUpdateVersion(Global.version_state, Global.version_state_name, "0");
            }
            //启动完成后更新数据状态
           ThreadUtil.execAsync(() -> {updateVersionService.updateVersionToServer();});
        } catch (Exception e) {
            e.printStackTrace();
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

        Path filePath = uploadDir.resolve("stt.txt");

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath.toFile()), StandardCharsets.UTF_8))) {
            writer.write(data);
            return true;
        } catch (IOException e) {
            System.err.println("写入文件失败: " + e.getMessage());
            return false;
        }
    }
}
