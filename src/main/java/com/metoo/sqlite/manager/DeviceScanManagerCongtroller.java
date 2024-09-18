package com.metoo.sqlite.manager;

import com.metoo.sqlite.gather.factory.gather.thread.Gather;
import com.metoo.sqlite.gather.factory.gather.thread.GatherFactory;
import com.metoo.sqlite.service.IArpService;
import com.metoo.sqlite.service.IDeviceScanService;
import com.metoo.sqlite.service.IProbeService;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-28 17:17
 */
@Slf4j
@RequestMapping("/admin/device/scan")
@RestController
public class DeviceScanManagerCongtroller {

    @Autowired
    private IProbeService probeService;
    @Autowired
    private IDeviceScanService deviceScanService;
    @Autowired
    private IArpService arpService;

    @GetMapping("/gather")
    public Result gather(){
        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather(Global.DEVICE_SCAN);
        gather.executeMethod();
        return ResponseUtil.ok();
    }
}
