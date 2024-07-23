package com.metoo.sqlite.manager;

import com.alibaba.fastjson.JSONObject;
import com.metoo.sqlite.entity.GatherLog;
import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.gather.factory.gather.Gather;
import com.metoo.sqlite.gather.factory.gather.GatherFactory;
import com.metoo.sqlite.gather.ssh.SSHUtils;
import com.metoo.sqlite.service.IGatherLogService;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.utils.file.FileToDatabase;
import com.metoo.sqlite.vo.Result;
import io.swagger.annotations.Api;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-27 11:18
 */
@Api("测试按钮接口")
@RequestMapping("/admin/test")
@RestController
public class TestManagerController {

    public static void main(String[] args) {
        List list = new ArrayList();
        list.add(1);
        list.add("a");
        System.out.println(list);

        list.removeAll(list);

        System.out.println(list);
    }

    @Test
    public void test23(){
        List<Probe> probes = new ArrayList<>();
        Probe probe = new Probe();
        probe.setPort_service_product("switch1");

        Probe probe4 = new Probe();
        probe4.setPort_service_product("switch2");

        Probe probe2 = new Probe();
        probe2.setPort_service_product("router1");

        Probe probe3 = new Probe();
        probe3.setPort_service_product("router2");

        Probe probe5 = new Probe();
        probe5.setMac_addr("router2");

        probes.add(probe);
        probes.add(probe2);
        probes.add(probe3);
        probes.add(probe4);
        probes.add(probe5);


        List<Probe> probes_switch = probes.stream()
                .filter(e -> e.getPort_service_product() != null && (e.getPort_service_product().contains("switch")
                        || e.getPort_service_product().contains("router")))
                .collect(Collectors.toList());
        System.out.println(JSONObject.toJSONString(probes_switch));
    }



    @Autowired
    private FileToDatabase fileToDatabase;

    @GetMapping("/fileToProbe")
    public Result test(){

        this.fileToDatabase.write();


        return ResponseUtil.ok();
    }

    @GetMapping("/fileToProbe2")
    public Result test2(){

        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather("fileToProbe");
        gather.executeMethod();

        return ResponseUtil.ok();
    }

    @Autowired
    private IGatherLogService gatherLogService;

    @GetMapping("/gatherLog")
    public void test3(){
        try {
            GatherLog gatherLog = new GatherLog();
            gatherLog.setCreateTime(DateTools.getCreateTime());
            gatherLog.setBeginTime(DateTools.getCreateTime());
            gatherLog.setEndTime(DateTools.getCreateTime());
            gatherLog.setType("手动");
            gatherLog.setResult("成功");
            gatherLog.setDetails("");
            gatherLog.setData("");
            this.gatherLogService.insert(gatherLog);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
