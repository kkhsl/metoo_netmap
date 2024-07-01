package com.metoo.sqlite.gather.Process;

import com.metoo.sqlite.gather.common.PyCommand;
import org.junit.Test;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-24 11:40
 */
@Component
public class PythonScriptRunner {

    @Test
    public void test(){
        PyCommand pyCommand = new PyCommand();
        pyCommand.setName("main.py");
        pyCommand.setParams(new String[]{"h3c", "switch", "192.168.100.1", "ssh", "22", "metoo", "metoo89745000", "aliveint"});
        List command = pyCommand.toArray();

        String result = this.runPythonScript(command);

        System.out.println(result);;
    }

    public String runPythonScript(String... args) {
        try {
            // 构建命令行参数，包括 Python 解释器和脚本路径
            ProcessBuilder pb = new ProcessBuilder();
            pb.redirectErrorStream(true);

            // 将参数添加到命令行参数中
            pb.command().addAll(Arrays.asList(args));

            // 启动进程并等待其完成
            Process process = pb.start();
            process.waitFor();

            // 读取进程输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output = reader.lines().collect(Collectors.joining("\n"));

            // 关闭输入流
            reader.close();

            // 返回执行结果
            return output;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ""; // 或者抛出异常，根据需求处理
        }
    }

    public String runPythonScript(List<String> params) {
        try {
            // 构建命令行参数，包括 Python 解释器和脚本路径
            ProcessBuilder pb = new ProcessBuilder();
            pb.redirectErrorStream(true);

            // 将参数添加到命令行参数中
            pb.command().addAll(params);

            // 启动进程并等待其完成
            Process process = pb.start();
            process.waitFor();

            // 读取进程输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output = reader.lines().collect(Collectors.joining("\n"));

            // 关闭输入流
            reader.close();

            // 返回执行结果
            return output;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ""; // 或者抛出异常，根据需求处理
        }
    }

    public static void main(String[] args) {

        // python3 /opt/sqlite/script/main.py h3c switch 192.168.100.1 ssh 22 metoo metoo89745000 aliveint

        PythonScriptRunner runner = new PythonScriptRunner();

        String result = runner.runPythonScript("python3","/opt/sqlite/script/main.py",
                "h3c", "switch", "192.168.100.1",
                "ssh", "22", "metoo", "metoo89745000", "aliveint");

        System.out.println("Python script output:\n" + result);
    }
}
