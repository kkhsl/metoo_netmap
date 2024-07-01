package com.metoo.sqlite.manager;

import com.metoo.sqlite.gather.Process.PythonScriptRunner;
import com.metoo.sqlite.gather.ssh.SSHUtils;
import com.metoo.sqlite.gather.common.PyCommand;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-23 22:53
 */
@RequestMapping("/admin")
@RestController
public class PyManagerController {

//    @Autowired
////    private PythonExecUtils pythonExecUtils;
////
////    @GetMapping("/py")
////    public String py(){
////        String[] params = {"h3c", "switch 192.168.100.1", "ssh",
////                "22", "metoo metoo89745000", "aliveint"};
////        String result = pythonExecUtils.exec2("/opt/sqlite/script/main.py", params);
////        return result;
////    }

    @Autowired
    private SSHUtils sshUtils;
    @Autowired
    private PyExecUtils pyExecUtils;

    @GetMapping("/ssh")
    public String ssh(){
        PyCommand pyCommand = new PyCommand();
        pyCommand.setVersion("python3");
//        pyCommand.setPath("/opt/sqlite/script/");
        pyCommand.setName("main.py");
        pyCommand.setParams(new String[]{"h3c", "switch", "192.168.100.1", "ssh", "22", "metoo", "metoo89745000", "aliveint"});

        String result = pyExecUtils.exec(pyCommand);

        return result;
    }

    @Autowired
    private PythonScriptRunner pythonScriptRunner;

//    @Autowired
//    private PyCommand pyCommand;


    @GetMapping("/Process")
    public String Process(){

        PyCommand pyCommand = new PyCommand();
        pyCommand.setName("main.py");
        pyCommand.setParams(new String[]{"h3c", "switch", "192.168.100.1", "ssh", "22", "metoo", "metoo89745000", "aliveint"});
        String[] command = pyCommand.toStringArray();

        String result = this.pythonScriptRunner.runPythonScript(command);

        return result;
    }

    @GetMapping("/Process2")
    public String Process2(){

        PyCommand pyCommand = new PyCommand();
        pyCommand.setName("main.py");
        pyCommand.setParams(new String[]{"h3c", "switch", "192.168.100.1", "ssh", "22", "metoo", "metoo89745000", "aliveint"});
        List command = pyCommand.toArray();

        String result = this.pythonScriptRunner.runPythonScript(command);

        return result;
    }

}
