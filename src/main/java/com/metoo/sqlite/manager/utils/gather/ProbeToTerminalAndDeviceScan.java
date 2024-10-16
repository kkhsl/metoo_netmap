package com.metoo.sqlite.manager.utils.gather;

import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.*;
import com.metoo.sqlite.manager.utils.MacUtils;
import com.metoo.sqlite.manager.utils.ProbeUtils;
import com.metoo.sqlite.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProbeToTerminalAndDeviceScan {

    @Autowired
    private IDeviceScanService deviceScanService;
    @Autowired
    private IProbeService probeService;
    @Autowired
    private ProbeUtils probeUtils;
    @Autowired
    private ITerminalService terminalService;
    @Autowired
    private IMacVendorService macVendorService;



    public void insertDeviceScan(Probe probe, String macVendor, String mac, String os){
        DeviceScan deviceScan = new DeviceScan();
        deviceScan.setDevice_ipv4(probe.getIp_addr());
        deviceScan.setDevice_ipv6(probe.getIpv6());
        deviceScan.setDevice_product(macVendor);
        deviceScan.setMac(mac);
        deviceScan.setMacVendor(macVendor);
        deviceScan.setOs(os);
        try {
            Map params = new HashMap();
            params.put("device_ipv4", probe.getIp_addr());
            List<DeviceScan> deviceScanList = this.deviceScanService.selectObjByMap(params);
            if(deviceScanList.size() <= 0){
                this.deviceScanService.insert(deviceScan);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
//            this.deleteProbe(probe.getIp_addr());
            this.probeService.delete(probe.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void finalProbe() {
        Map params = new HashMap();
        // probe最终表数据
        List<Probe> probes = this.probeService.selectObjByMap(null);
        if (probes.size() > 0) {
            for (Probe probe : probes) {
                if (StringUtil.isNotEmpty(probe.getIp_addr())) {
                        if (StringUtil.isNotEmpty(probe.getMac())) {
                            if (!MacUtils.getMac(probe.getMac()).equals("") && MacUtils.isValidMACAddress(probe.getMac())) {
                                params.clear();
                                params.put("mac", MacUtils.getMac(probe.getMac()));
                                List<MacVendor> macVendorList = macVendorService.selectObjByMap(params);
                                if (macVendorList.size() > 0) {
                                    MacVendor macVendor = macVendorList.get(0);
                                    if (macVendor.getVendor() != null){
                                        String vendor = VerifyMacVendorUtils.toTerminal(macVendor.getVendor());
                                        if(StringUtil.isNotEmpty(vendor)){
                                            insertTerminal(probe, vendor);
                                        }
                                        vendor = VerifyMacVendorUtils.toDeviceScan(macVendor.getVendor());
                                        if(StringUtil.isNotEmpty(vendor)){
                                            insertDeviceScan(probe, macVendor.getVendor(), probe.getMac(), vendor);
                                        }
                                    }
                                }
                            }

                        }
                    }
                // probe表中只有ipv6地址没有ipv4地址的条目放入terminal_detail，os填mac_vendor的内容
                if(StringUtil.isEmpty(probe.getIp_addr()) && StringUtil.isNotEmpty(probe.getIpv6())){
//                    insertTerminal(probe); 直接插入终端表
                    v6ToTerminal(probe);
                }
            }
            // 剩余probe写入终端
            this.probeUtils.insertTerminal();
        }

    }

    public void insertTerminal(Probe probe, String os){
        Map params = new HashMap();
        Terminal terminal = new Terminal();
        terminal.setOs(os);
        if(StringUtil.isNotEmpty(probe.getIp_addr()) || StringUtil.isNotEmpty(probe.getIpv6())){
            terminal.setIpv4addr(probe.getIp_addr());
            terminal.setIpv6addr(probe.getIpv6());
            terminal.setMac(probe.getMac());
            terminal.setMacvendor(probe.getMac_vendor());
        }
        params.clear();
        List<Terminal> terminals = new ArrayList<>();
        if(StringUtil.isNotEmpty(probe.getIp_addr())){
            params.put("ipv4addr", probe.getIp_addr());
            terminals = this.terminalService.selectObjByMap(params);
        }
        if(terminals.size() <= 0){
            params.put("ipv6addr", probe.getIpv6());
            terminals = this.terminalService.selectObjByMap(params);
        }

        if(terminals.size() <= 0){
            this.terminalService.insert(terminal);
        }else{
            this.terminalService.update(terminal);
        }
//        this.deleteProbe(probe.getIp_addr());
        this.probeService.delete(probe.getId());
    }

    public void v6ToTerminal(Probe probe){
        Map params = new HashMap();
        if(StringUtil.isNotEmpty(probe.getMac())){
            if(!MacUtils.getMac(probe.getMac()).equals("") && MacUtils.isValidMACAddress(probe.getMac())){
                params.clear();
                params.put("mac", MacUtils.getMac(probe.getMac()));
                List<MacVendor> macVendorList = macVendorService.selectObjByMap(params);
                if(macVendorList.size() > 0){
                    MacVendor macVendor = macVendorList.get(0);
                    if (macVendor.getVendor() != null){
                        String vendor = VerifyMacVendorUtils.toTerminal(macVendor.getVendor());
                        if(StringUtil.isNotEmpty(vendor)){
                            insertTerminal(probe, vendor);
                        }
                        vendor = VerifyMacVendorUtils.toDeviceScan(macVendor.getVendor());
                        if(StringUtil.isNotEmpty(vendor)){
                            insertDeviceScan(probe, macVendor.getVendor(), probe.getMac(), vendor);
                        }
                    }
                }else{
                    insertV6Terminal(probe);
                }
            }
        }
    }

    public void insertV6Terminal(Probe probe) {
        Map params = new HashMap();
        Terminal terminal = new Terminal();
        if (StringUtil.isNotEmpty(probe.getIpv6())) {
            terminal.setIpv6addr(probe.getIpv6());
            terminal.setMac(probe.getMac());
            terminal.setMacvendor(probe.getMac_vendor());
        }
        params.clear();
        params.put("ipv6addr", terminal.getIpv6addr());
        List<Terminal> terminals = this.terminalService.selectObjByMap(params);
        if (terminals.size() <= 0) {
            this.terminalService.insert(terminal);
        } else {
            this.terminalService.update(terminal);
        }
    }


    public void deleteProbe(String ip_addr) {
        if (StringUtil.isNotEmpty(ip_addr)) {
            Map params = new HashMap();
            params.put("ip_addr", ip_addr);
            List<Probe> deleteProbes = probeService.selectObjByMap(params);
            if (deleteProbes.size() > 0) {
                for (Probe deleteProbe : deleteProbes) {
                    try {
                        probeService.delete(deleteProbe.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
