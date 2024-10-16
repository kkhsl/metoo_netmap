package com.metoo.sqlite.manager.utils.gather;

import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.Arp;
import com.metoo.sqlite.entity.DeviceScan;
import com.metoo.sqlite.entity.Terminal;
import com.metoo.sqlite.manager.utils.MacUtils;
import com.metoo.sqlite.service.IDeviceScanService;
import com.metoo.sqlite.service.ITerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class VerifyVendorUtils {

    @Autowired
    private ITerminalService terminalService;
    @Autowired
    private IDeviceScanService deviceScanService;

    public void finalTerminal(){
        List<Terminal> terminalList = this.terminalService.selectObjByMap(null);
        if(terminalList.size() > 0){
            for (Terminal terminal : terminalList) {
                if(StringUtil.isNotEmpty(terminal.getMacvendor())){
                    if(StringUtil.isNotEmpty(terminal.getMacvendor())){
                        String vendor = VerifyMacVendorUtils.toDeviceScan(terminal.getMacvendor());
                        if(StringUtil.isNotEmpty(vendor)){
                            insertDeviceScan(terminal, vendor);
                        }

                    }
                }
            }
        }
    }

    public void insertDeviceScan(Terminal terminal, String macVendor){
        DeviceScan deviceScan = new DeviceScan();
        deviceScan.setDevice_ipv4(terminal.getIpv4addr());
        deviceScan.setDevice_ipv6(terminal.getIpv6addr());
        deviceScan.setDevice_product(macVendor);
        deviceScan.setMac(terminal.getMac());
        deviceScan.setMacVendor(terminal.getMacvendor());
        deviceScan.setOs(terminal.getOs());
        try {
            Map params = new HashMap();
            params.put("device_ipv4", terminal.getIpv4addr());
            List<DeviceScan> deviceScanList = this.deviceScanService.selectObjByMap(params);
            if(deviceScanList.size() <= 0){
                this.deviceScanService.insert(deviceScan);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.terminalService.delete(terminal.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
