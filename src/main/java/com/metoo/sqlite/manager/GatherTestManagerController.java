package com.metoo.sqlite.manager;

import com.metoo.sqlite.gather.factory.gather.thread.Gather;
import com.metoo.sqlite.gather.factory.gather.thread.GatherFactory;
import com.metoo.sqlite.gather.self.SelfTerminalUtils;
import com.metoo.sqlite.manager.utils.ProbeUtils;
import com.metoo.sqlite.manager.utils.gather.ProbeToTerminalAndDeviceScan;
import com.metoo.sqlite.manager.utils.gather.VerifyVendorUtils;
import com.metoo.sqlite.service.*;
import com.metoo.sqlite.service.impl.PublicService;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/admin/gather/test")
@RestController
public class GatherTestManagerController {

    @Autowired
    private ProbeUtils probeUtils;
    @Autowired
    private IProbeService probeService;
    @Autowired
    private IArpService arpService;
    @Autowired
    private IMacVendorService macVendorService;
    @Autowired
    private IDeviceScanService deviceScanService;
    @Autowired
    private ITerminalService terminalService;

    @Autowired
    private SelfTerminalUtils selfTerminalUtils;
    @Autowired
    private ProbeToTerminalAndDeviceScan probeToTerminalAndDeviceScan;
    @Autowired
    private VerifyVendorUtils verifyVendorUtils;

    @GetMapping("/terminal/self")
    public Result selfTerminalUtils() {
        this.selfTerminalUtils.main();
        return ResponseUtil.ok();
    }

    @Autowired
    private PublicService publicService;

    public void gatherDeviceScan() throws Exception {
        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather(Global.DEVICE_SCAN);
        gather.executeMethod();
    }

    @GetMapping("/terminal")
    public void gatherTerminal() {
        String beginTime = DateTools.getCreateTime();
        int temLogId = publicService.createSureyingLog("终端分析", beginTime, 1, null);
        try {
            //this.gatherArp();
            this.gatherDeviceScan();
            GatherFactory factory = new GatherFactory();
            Gather gather = factory.getGather(Global.TERMINAL);
            gather.executeMethod();
            this.probeToTerminalAndDeviceScan.finalProbe();
            this.verifyVendorUtils.finalTerminal();
            publicService.updateSureyingLog(temLogId, 2);
        } catch (Exception e) {
            e.printStackTrace();
            publicService.updateSureyingLog(temLogId, 3);
        }
    }

    public void executeGatherTask(GatherFactory factory, String gatherType) throws Exception {
        Gather gather = factory.getGather(gatherType);
        gather.executeMethod();
    }


    @GetMapping("/ipv4_pana2")
    public Result ipv4_pana() {
        this.ipv4_pana2();
        return ResponseUtil.ok();
    }


    public void ipv4_pana2(){
        try {
            GatherFactory factory = new GatherFactory();
            Gather gather = factory.getGather(Global.IPV4_PANABIT);
            gather.executeMethod();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/arp")
    public Result arpTest() {
        this.arpService.gather();
        return ResponseUtil.ok();
    }


}
