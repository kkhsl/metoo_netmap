package com.metoo.sqlite.gather.utils;

import com.metoo.sqlite.gather.Process.PythonScriptRunner;
import com.metoo.sqlite.gather.common.PyCommand;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.ssh.SSHUtils;
import com.metoo.sqlite.utils.Global;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-24 12:00
 */
@Slf4j
@Component
public class PyExecUtils {

    @Autowired
    private SSHUtils sshUtils;
    @Autowired
    private PythonScriptRunner pythonScriptRunner;


    public static void main(String[] args) {
        PythonScriptRunner pythonScriptRunner = new PythonScriptRunner();
//        main.exe h3c switch 192.168.6.1 ssh 22 metoo "metoo89745000" all
        String result = "";
            result = pythonScriptRunner.exec_exe("C:\\netmap\\script", "main.exe", "h3c", "switch", "192.168.6.1",
            "ssh", "22", "metoo", "metoo89745000", "all");
        System.out.println(result);
//        result = pythonScriptRunner.exec("C:\\netmap\\script", "main.exe", "h3c", "switch", "192.168.6.1",
//                "ssh", "22", "metoo", "metoo89745000", "all");
//        System.out.println(result);
    }

    public String exec(PyCommand pyCommand) {
//        String result = "";
//        pyCommand.setVersion("python3");
//        if (Global.env.equals("prod")) {
//            result = this.pythonScriptRunner.runPythonScript(pyCommand.toStringArray());
//        }else if("dev".equals(Global.env)){
//            result = this.sshUtils.executeCommand(pyCommand.toParamsString());
//        }
        String result = this.sshUtils.executeCommand(pyCommand.toParamsString());
        return result;
    }

    public String exec(PyCommandBuilder pyCommand) {
        String result = "";
        if ("dev".equals(Global.env)) {
            result = this.sshUtils.executeCommand(pyCommand.toParamsString());
        }else {
            if (pyCommand.getName().contains(".py")) {
                result = this.pythonScriptRunner.exec(pyCommand.getPath(), pyCommand.toStringArray());
            } else if (pyCommand.getName().contains(".exe")) {
                result = this.pythonScriptRunner.exec_exe(pyCommand.getPath(), pyCommand.getName(), pyCommand.toStringArrayReomveName());
            } else {
                result = this.pythonScriptRunner.exec(pyCommand.getPath(), pyCommand.toStringArray());
            }
        }
        log.info("command: " + pyCommand.toParamsString() + "result【" + result + "】end");


        return result;
    }

    public static String execPy(PyCommandBuilder pyCommand) {
        String result = "";
        result = PythonScriptRunner.execPy(pyCommand.getPath(), pyCommand.toStringArray());
        return result;
    }



    // 仅使用登录ssh方式，执行命令；优化登录方式，避免每次连接带来的性能和时间消耗，注意并发问题
//    public String exec(PyCommandBuilder pyCommand) {
//        String result = this.sshUtils.executeCommand(pyCommand.toParamsString());
//        return result;
//    }

//    public String process(PyCommandBuilder pyCommand) {
//        String result = this.pythonScriptRunner.runPythonScript(pyCommand.toStringArray());
//        return result;
//    }

}
