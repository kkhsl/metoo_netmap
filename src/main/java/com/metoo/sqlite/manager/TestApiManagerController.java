package com.metoo.sqlite.manager;

import com.metoo.sqlite.gather.ssh.SSHUtils;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.vo.Result;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-27 11:18
 */
@Api("测试按钮接口")
@RequestMapping("/admin/py/test")
@Controller
public class TestApiManagerController {

    @Autowired
    private SSHUtils sshUtils;
//    @Value("${py.path}")
    private String path;

    @GetMapping
    public Result test(){
        String result = this.sshUtils.executeCommand("cd " + path + " && python3 main.py h3c switch 192.168.100.3 ssh 22 metoo metoo8974500 test");
        return ResponseUtil.ok(result);
    }

}
