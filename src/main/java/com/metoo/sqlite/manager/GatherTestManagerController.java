package com.metoo.sqlite.manager;

import com.metoo.sqlite.gather.factory.gather.thread.Gather;
import com.metoo.sqlite.gather.factory.gather.thread.GatherFactory;
import com.metoo.sqlite.gather.self.SelfTerminalUtils;
import com.metoo.sqlite.manager.utils.ProbeUtils;
import com.metoo.sqlite.manager.utils.gather.ProbeToTerminalAndDeviceScan;
import com.metoo.sqlite.manager.utils.gather.VerifyVendorUtils;
import com.metoo.sqlite.service.*;
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

    @GetMapping("/terminal")
    public void gatherTerminal() {

        this.gatherDeviceScan();

        GatherFactory factory = new GatherFactory();

        try {
            executeGatherTask(factory, Global.TERMINAL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.probeToTerminalAndDeviceScan.finalProbe();
            this.verifyVendorUtils.finalTerminal();

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

    @GetMapping("/deviceScan")
    public Result deviceScanTest() {
        this.gatherDeviceScan();
        return ResponseUtil.ok();
    }


    public void gatherDeviceScan() {
        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather(Global.DEVICE_SCAN);
        gather.executeMethod();
    }


}
