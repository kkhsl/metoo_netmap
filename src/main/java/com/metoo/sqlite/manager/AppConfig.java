package com.metoo.sqlite.manager;

import com.alibaba.fastjson.JSONObject;
import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.gather.common.PyCommand;
import com.metoo.sqlite.service.IDeviceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-24 19:45
 */
@Component
@RestController
public class AppConfig {

//    @Value("${py.path}")
    private String path;

    @GetMapping("/admin/properties")
    public String properties(){

        PyCommand pyCommand = (PyCommand) ApplicationContextUtils.getBean("pyCommand");
        return JSONObject.toJSONString(pyCommand);
    }
}
