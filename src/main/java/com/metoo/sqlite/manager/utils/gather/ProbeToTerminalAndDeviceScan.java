package com.metoo.sqlite.manager.utils.gather;

import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.*;
import com.metoo.sqlite.gather.common.ConcatenationCharacter;
import com.metoo.sqlite.manager.utils.MacUtils;
import com.metoo.sqlite.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProbeToTerminalAndDeviceScan {

    @Autowired
    private IDeviceScanService deviceScanService;
    @Autowired
    private IProbeService probeService;
    @Autowired
    private ITerminalService terminalService;

    public void finalProbe() {
        // probe最终表数据
        List<Probe> probes = this.probeService.selectDeduplicationByIp(null);
        if (probes.size() > 0) {
            for (Probe probe : probes) {
                if (StringUtil.isNotEmpty(probe.getIp_addr()) || StringUtil.isNotEmpty(probe.getIpv6())) {
                    if (StringUtil.isNotEmpty(probe.getMac())) {
                        if (!MacUtils.getMac(probe.getMac()).equals("") && MacUtils.isValidMACAddress(probe.getMac())) {
                                if (probe.getMac_vendor() != null){
                                    String os = ConcatenationCharacter
                                            .concatenateWithSpace(":",
                                                    ConcatenationCharacter.disassembleWithSpace(",", probe.getVendor()),
                                                    ConcatenationCharacter.disassembleWithSpace(",", probe.getOs_gen()),
                                                    ConcatenationCharacter.disassembleWithSpace(",", probe.getOs_family()));
                                    String vendor = VerifyMacVendorUtils.toTerminal(probe.getMac_vendor());
                                    if(StringUtil.isNotEmpty(vendor)){
                                        insertTerminal(probe, os);
                                    }
                                    vendor = VerifyMacVendorUtils.toDeviceScan(probe.getMac_vendor());
                                    if(StringUtil.isNotEmpty(vendor)){
                                        insertDeviceScan(probe, os);
                                    }
                                }
                            }
                        }
                    }
                }
            // 剩余probe写入终端
            finalProbeToTerminal();
        }
    }

    public void insertDeviceScan(Probe probe, String os){
        DeviceScan deviceScan = new DeviceScan();
        deviceScan.setDevice_ipv4(probe.getIp_addr());
        deviceScan.setDevice_ipv6(probe.getIpv6());
        deviceScan.setMac(probe.getMac());
        deviceScan.setMacVendor(probe.getMac_vendor());
        deviceScan.setDevice_product(os);
        try {
            List<DeviceScan> deviceScanList = deviceScanService.selectObjByIpv4OrIpv6(probe.getIp_addr(), probe.getIpv6());
            if(deviceScanList.size() <= 0){
                this.deviceScanService.insert(deviceScan);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.probeService.deleteProbeByIp(probe.getIp_addr(), probe.getIpv6());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertTerminal(Probe probe, String os){
        Terminal terminal = new Terminal();
        terminal.setOs(os);
        if(StringUtil.isNotEmpty(probe.getIp_addr()) || StringUtil.isNotEmpty(probe.getIpv6())){
            terminal.setIpv4addr(probe.getIp_addr());
            terminal.setIpv6addr(probe.getIpv6());
            terminal.setMac(probe.getMac());
            terminal.setMacvendor(probe.getMac_vendor());
        }
        List<Terminal> terminals = this.terminalService.selectObjByIpv4OrIpv6(probe.getIp_addr(), probe.getIpv6());
        if(terminals.size() <= 0){
            this.terminalService.insert(terminal);
        }else{
            this.terminalService.update(terminal);
        }
        this.probeService.deleteProbeByIp(probe.getIp_addr(), probe.getIpv6());
    }

    public void finalProbeToTerminal(){
        List<Probe> probeList = probeService.selectDeduplicationByIp(null);
        if (!probeList.isEmpty()) {
            for (Probe probe : probeList) {
                String os = ConcatenationCharacter
                        .concatenateWithSpace(":",
                                ConcatenationCharacter.disassembleWithSpace(",", probe.getVendor()),
                                ConcatenationCharacter.disassembleWithSpace(",", probe.getOs_gen()),
                                ConcatenationCharacter.disassembleWithSpace(",", probe.getOs_family()));
                insertTerminal(probe, os);
            }
        }
    }

}
