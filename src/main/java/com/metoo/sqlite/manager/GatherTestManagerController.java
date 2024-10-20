package com.metoo.sqlite.manager;

import com.alibaba.fastjson.JSONObject;
import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.gather.factory.gather.thread.Gather;
import com.metoo.sqlite.gather.factory.gather.thread.GatherFactory;
import com.metoo.sqlite.gather.self.SelfTerminalUtils;
import com.metoo.sqlite.manager.utils.gather.ProbeToTerminalAndDeviceScan;
import com.metoo.sqlite.manager.utils.gather.VerifyVendorUtils;
import com.metoo.sqlite.service.*;
import com.metoo.sqlite.service.impl.PublicService;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RequestMapping("/admin/gather/test")
@RestController
public class GatherTestManagerController {

    @Autowired
    private ProbeToTerminalAndDeviceScan probeToTerminalAndDeviceScan;

    @Autowired
    private PublicService publicService;

    @GetMapping("/terminal")
    public void gatherTerminal() {
        String beginTime = DateTools.getCreateTime();
        int temLogId = publicService.createSureyingLog("终端分析", beginTime, 1, null, 8);
        try {
            //this.gatherArp();
            this.gatherDeviceScan();
            GatherFactory factory = new GatherFactory();
            Gather gather = factory.getGather(Global.TERMINAL);
            gather.executeMethod();
            this.probeToTerminalAndDeviceScan.finalProbe();
//            this.verifyVendorUtils.finalTerminal();
            publicService.updateSureyingLog(temLogId, 2);
        } catch (Exception e) {
            e.printStackTrace();
            publicService.updateSureyingLog(temLogId, 3);
        }
    }

    @GetMapping("/device_scan")
    public void device_scan() {
        try {
            this.gatherDeviceScan();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gatherDeviceScan() throws Exception {
        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather(Global.DEVICE_SCAN);
        gather.executeMethod();
    }

}
