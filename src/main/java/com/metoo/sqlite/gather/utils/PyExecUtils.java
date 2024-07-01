package com.metoo.sqlite.gather.utils;

import com.metoo.sqlite.gather.Process.PythonScriptRunner;
import com.metoo.sqlite.gather.common.PyCommand;
import com.metoo.sqlite.gather.ssh.SSHUtils;
import com.metoo.sqlite.utils.Global;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-24 12:00
 */
@Component
public class PyExecUtils {

    @Autowired
    private SSHUtils sshUtils;
    @Autowired
    private PythonScriptRunner pythonScriptRunner;

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
}
