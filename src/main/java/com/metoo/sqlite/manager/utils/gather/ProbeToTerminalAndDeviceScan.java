package com.metoo.sqlite.manager.utils.gather;

import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.*;
import com.metoo.sqlite.manager.utils.MacUtils;
import com.metoo.sqlite.manager.utils.ProbeUtils;
import com.metoo.sqlite.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            this.deleteProbe(probe.getIp_addr());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void finalProbe() {
        Map params = new HashMap();
        // probe最终表数据
        List<Probe> probes = this.probeService.selectObjByMap(null);
        if (probes.size() > 0) {
            List<Probe> uniqueDevices = probes.stream()
                    .collect(Collectors.toMap(
                            Probe::getIp_addr,
                            probe -> probe,
                            (existing, replacement) -> existing
                    ))
                    .values()
                    .stream()
                    .collect(Collectors.toList());
            for (Probe probe : uniqueDevices) {
                if (StringUtil.isNotEmpty(probe.getIp_addr())) {
                        if (StringUtil.isNotEmpty(probe.getMac())) {
                            if (!MacUtils.getMac(probe.getMac()).equals("") && MacUtils.isValidMACAddress(probe.getMac())) {
                                params.clear();
                                params.put("mac", MacUtils.getMac(probe.getMac()));
                                List<MacVendor> macVendorList = macVendorService.selectObjByMap(params);
                                if (macVendorList.size() > 0) {
                                    MacVendor macVendor = macVendorList.get(0);
                                    if (macVendor.getVendor() != null) {
                                        String vendor = macVendor.getVendor();
                                        if (vendor.toLowerCase().contains("Tenda".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Tenda");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("h3c".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "h3c");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("TP-LINK".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "TP-LINK");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("Lite-On".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Lite-On");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("mercury".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "mercury");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("Huawei Device".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Huawei Device");
                                        } else if (vendor.toLowerCase().contains("ruijie".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "ruijie");
                                        } else if (vendor.toLowerCase().contains("PUTIAN".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "PUTIAN");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("Hikvision".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Hikvision");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("Dahua".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Dahua");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("Ezviz".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Ezviz");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("Panasonic".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Panasonic");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("Logitech".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Logitech");
                                        } else if (vendor.toLowerCase().contains("Tiandy".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Tiandy");
                                        } else if (vendor.toLowerCase().contains("Cannon".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Cannon");
                                        } else if (vendor.toLowerCase().contains("Samsung".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Samsung");
                                        } else if (vendor.toLowerCase().contains("RICOH".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "RICOH");
                                        } else if (vendor.toLowerCase().contains("Epson".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Epson");
                                        } else if (vendor.toLowerCase().contains("Brother".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Brother");
                                        } else if (vendor.toLowerCase().contains("KONICA".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "KONICA");
                                        } else if (vendor.toLowerCase().contains("MINOLTA".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "MINOLTA");
                                        } else if (vendor.toLowerCase().contains("LEXMARK".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "LEXMARK");
                                        } else if (vendor.toLowerCase().contains("Sundray".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Sundray");
                                        } else if (vendor.toLowerCase().contains("IEIT SYSTEMS".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "IEIT SYSTEMS");
                                        } else if (vendor.toLowerCase().contains("Topsec".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Topsec");
                                        } else if (vendor.toLowerCase().contains("Sangfor".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Sangfor");
                                        } else if (vendor.toLowerCase().contains("Jiangxi".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Jiangxi");
                                        } else if (vendor.toLowerCase().contains("Xunte".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Xunte");
                                        } else if (vendor.toLowerCase().contains("DPtech".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "DPtech");
                                        } else if (vendor.toLowerCase().contains("D-Link".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "D-Link");
                                        } else if (vendor.toLowerCase().contains("Tenda".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Tenda");
                                        } else if (vendor.toLowerCase().contains("NETGEAR".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "NETGEAR");
                                        } else if (vendor.toLowerCase().contains("NETCORE".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "NETCORE");
                                        } else if (vendor.toLowerCase().contains("MERCURY".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "MERCURY");
                                        } else if (vendor.toLowerCase().contains("zte".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "zte");
                                        } else if (vendor.toLowerCase().contains("Fiberhome".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Fiberhome");
                                        } else if (vendor.toLowerCase().contains("Ericsson".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Ericsson");
                                        } else if (vendor.toLowerCase().contains("Cisco".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Cisco");
                                        } else if (vendor.toLowerCase().contains("Juniper".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Juniper");
                                        } else if (vendor.toLowerCase().contains("Brocade".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Brocade");
                                        } else if (vendor.toLowerCase().contains("Extreme".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Extreme");
                                        } else if (vendor.toLowerCase().contains("ProCurve".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "ProCurve");
                                        } else if (vendor.toLowerCase().contains("Maipu".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Maipu");
                                        } else if (vendor.toLowerCase().contains("Ruijie".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Ruijie");
                                        } else if (vendor.toLowerCase().contains("Venustech".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Venustech");
                                        } else if (vendor.toLowerCase().contains("Shenou".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Shenou");
                                        } else if (vendor.toLowerCase().contains("Communication".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Communication");
                                        } else if (vendor.toLowerCase().contains("REALTEK".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "REALTEK");
                                        } else if (vendor.toLowerCase().contains("Lite-On".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "LINK");
                                        } else if (vendor.toLowerCase().contains("Shprobe".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "INK");
                                        } else if (vendor.toLowerCase().contains("Tiger NetCom".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "LINK");
                                        } else if (vendor.toLowerCase().contains("AI-Link".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "AI-Link");
                                        } else if (vendor.toLowerCase().contains("Century Xinyang".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Century Xinyang");
                                        } else if (vendor.toLowerCase().contains("BaoLun".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "BaoLun");
                                        } else if (vendor.toLowerCase().contains("Shiyuan Electronic".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Shiyuan Electronic");
                                        } else if (vendor.toLowerCase().contains("SHENZHEN FAST".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "SHENZHEN FAST");
                                        } else if (vendor.toLowerCase().contains("AMPAK".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "AMPAK");
                                        } else if (vendor.toLowerCase().contains("Midea".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Midea");
                                        } else if (vendor.toLowerCase().contains("ASIX ELECTRONICS".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "ASIX ELECTRONICS");
                                        } else if (vendor.toLowerCase().contains("Shanghai WDK Industrial".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Shanghai WDK Industrial");
                                        } else
                                        if (vendor.toLowerCase().contains("Zhejiang Uniview".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Zhejiang Uniview");
                                        } else if (vendor.toLowerCase().contains("Zhejiang Dahua".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Zhejiang Dahua");
                                        } else if (vendor.toLowerCase().contains("SUNIX".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "SUNIX");
                                        } else if (vendor.toLowerCase().contains("YEALINK".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "YEALINK");
                                        }  else if (vendor.toLowerCase().contains("Uniview".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Uniview");
                                        }  else if (vendor.toLowerCase().contains("Camille Bauer".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Camille Bauer");
                                        }  else if (vendor.toLowerCase().contains("Microchip  ".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Microchip");
                                        }  else if (vendor.toLowerCase().contains("AMPAK".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "AMPAK");
                                        }  else if (vendor.toLowerCase().contains("Electronics ".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Electronics");
                                        }  else if (vendor.toLowerCase().contains("Shenzhen Decnta".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Shenzhen Decnta");
                                        }  else if (vendor.toLowerCase().contains("Shenzhen Seavo".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Shenzhen Seavo");
                                        }  else if (vendor.toLowerCase().contains("Shenzhen WiSiYiLink".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Shenzhen WiSiYiLink");
                                        }  else if (vendor.toLowerCase().contains("Realme Chongqing Mobile".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Realme Chongqing Mobile");
                                        }  else if (vendor.toLowerCase().contains("ELECTRONIC".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "ELECTRONIC");
                                        }  else if (vendor.toLowerCase().contains("IEEE Registration".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "IEEE Registration");
                                        }  else if (vendor.toLowerCase().contains("Alcatel-Lucent".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Alcatel-Lucent");
                                        }  else if (vendor.toLowerCase().contains("Toshiba Teli".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Toshiba Teli");
                                        }  else if (vendor.toLowerCase().contains("Shenzhen Sunchip Technology".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "Shenzhen Sunchip Technology");
                                        }  else if (vendor.toLowerCase().contains("DAVICOM SEMICONDUCTOR".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "DAVICOM SEMICONDUCTOR");
                                        }else if(vendor.toLowerCase().toLowerCase().contains("openbsd".toLowerCase())){
                                            insertDeviceScan(probe, vendor, probe.getMac(),"openbsd");
                                        } if (vendor.toLowerCase().contains("LCFC(Hefei) Electronics".toLowerCase())) {
                                            insertDeviceScan(probe, vendor, probe.getMac(), "LCFC(Hefei) Electronics");
                                        }else

                                        if (vendor.toLowerCase().contains("Hewlett Packard".toLowerCase())) {
                                           insertTerminal(probe, "Hewlett Packard");
                                       } else if(vendor.toLowerCase().toLowerCase().contains("LCFC(Hefei) Electronics".toLowerCase())){
                                            insertTerminal(probe, "LCFC(Hefei) Electronics");
                                        }else if(vendor.toLowerCase().toLowerCase().contains("CLOUD".toLowerCase())){
                                            insertTerminal(probe, "CLOUD");
                                        }else if(vendor.toLowerCase().toLowerCase().contains("Intel".toLowerCase())){
                                            insertTerminal(probe, "Intel");
                                        }else if(vendor.toLowerCase().toLowerCase().contains("vivo".toLowerCase())){
                                            insertTerminal(probe, "vivo");
                                        }else if(vendor.toLowerCase().toLowerCase().contains("xiaomi".toLowerCase())){
                                            insertTerminal(probe, "xiaomi");
                                        }else if(vendor.toLowerCase().toLowerCase().contains("oppo".toLowerCase())){
                                            insertTerminal(probe, "oppo");
                                        }else if(vendor.toLowerCase().toLowerCase().contains("apple".toLowerCase())){
                                            insertTerminal(probe, "apple");
                                        }else if(vendor.toLowerCase().toLowerCase().contains("Honor".toLowerCase())){
                                            insertTerminal(probe, "Honor");
                                        }else if(vendor.toLowerCase().toLowerCase().contains("Chicony".toLowerCase())){
                                            insertTerminal(probe, "Chicony");
                                        }else if(vendor.toLowerCase().toLowerCase().contains("Liteon".toLowerCase())){
                                            insertTerminal(probe, "Liteon");
                                        }else if(vendor.toLowerCase().toLowerCase().contains("HUAWEI TECHNOLOGIES".toLowerCase())){
                                            insertTerminal(probe, "HUAWEI TECHNOLOGIES");
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
        if(StringUtil.isNotEmpty(probe.getIp_addr())){
            terminal.setIpv4addr(probe.getIp_addr());
            terminal.setIpv6addr(probe.getIpv6());
            terminal.setMac(probe.getMac());
            terminal.setMacvendor(probe.getMacVendor());
        }
        params.clear();
        params.put("ipv4addr", probe.getIp_addr());
        List<Terminal> terminals = this.terminalService.selectObjByMap(params);
        if(terminals.size() <= 0){

            this.terminalService.insert(terminal);
        }else{

            this.terminalService.update(terminal);
        }
        this.deleteProbe(probe.getIp_addr());
//        this.probeService.delete(probe.getId());
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
                    if(macVendor.getVendor() != null){
                        String vendor = macVendor.getVendor();
                        if (vendor.toLowerCase().contains("Tenda".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Tenda");
                        } else if (vendor.toLowerCase().toLowerCase().contains("h3c".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "h3c");
                        } else if (vendor.toLowerCase().toLowerCase().contains("TP-LINK".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "TP-LINK");
                        } else if (vendor.toLowerCase().toLowerCase().contains("Lite-On".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Lite-On");
                        } else if (vendor.toLowerCase().toLowerCase().contains("mercury".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "mercury");
                        } else if (vendor.toLowerCase().toLowerCase().contains("Huawei Device".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Huawei Device");
                        } else if (vendor.toLowerCase().contains("ruijie".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "ruijie");
                        } else if (vendor.toLowerCase().contains("PUTIAN".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "PUTIAN");
                        } else if (vendor.toLowerCase().toLowerCase().contains("Hikvision".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Hikvision");
                        } else if (vendor.toLowerCase().toLowerCase().contains("Dahua".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Dahua");
                        } else if (vendor.toLowerCase().toLowerCase().contains("Ezviz".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Ezviz");
                        } else if (vendor.toLowerCase().toLowerCase().contains("Panasonic".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Panasonic");
                        } else if (vendor.toLowerCase().toLowerCase().contains("Logitech".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Logitech");
                        } else if (vendor.toLowerCase().contains("Tiandy".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Tiandy");
                        } else if (vendor.toLowerCase().contains("Cannon".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Cannon");
                        } else if (vendor.toLowerCase().contains("Samsung".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Samsung");
                        } else if (vendor.toLowerCase().contains("RICOH".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "RICOH");
                        } else if (vendor.toLowerCase().contains("Epson".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Epson");
                        } else if (vendor.toLowerCase().contains("Brother".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Brother");
                        } else if (vendor.toLowerCase().contains("KONICA".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "KONICA");
                        } else if (vendor.toLowerCase().contains("MINOLTA".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "MINOLTA");
                        } else if (vendor.toLowerCase().contains("LEXMARK".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "LEXMARK");
                        } else if (vendor.toLowerCase().contains("Sundray".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Sundray");
                        } else if (vendor.toLowerCase().contains("IEIT SYSTEMS".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "IEIT SYSTEMS");
                        } else if (vendor.toLowerCase().contains("Topsec".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Topsec");
                        } else if (vendor.toLowerCase().contains("Sangfor".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Sangfor");
                        } else if (vendor.toLowerCase().contains("Jiangxi".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Jiangxi");
                        } else if (vendor.toLowerCase().contains("Xunte".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Xunte");
                        } else if (vendor.toLowerCase().contains("DPtech".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "DPtech");
                        } else if (vendor.toLowerCase().contains("D-Link".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "D-Link");
                        } else if (vendor.toLowerCase().contains("Tenda".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Tenda");
                        } else if (vendor.toLowerCase().contains("NETGEAR".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "NETGEAR");
                        } else if (vendor.toLowerCase().contains("NETCORE".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "NETCORE");
                        } else if (vendor.toLowerCase().contains("MERCURY".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "MERCURY");
                        } else if (vendor.toLowerCase().contains("zte".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "zte");
                        } else if (vendor.toLowerCase().contains("Fiberhome".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Fiberhome");
                        } else if (vendor.toLowerCase().contains("Ericsson".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Ericsson");
                        } else if (vendor.toLowerCase().contains("Cisco".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Cisco");
                        } else if (vendor.toLowerCase().contains("Juniper".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Juniper");
                        } else if (vendor.toLowerCase().contains("Brocade".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Brocade");
                        } else if (vendor.toLowerCase().contains("Extreme".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Extreme");
                        } else if (vendor.toLowerCase().contains("ProCurve".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "ProCurve");
                        } else if (vendor.toLowerCase().contains("Maipu".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Maipu");
                        } else if (vendor.toLowerCase().contains("Ruijie".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Ruijie");
                        } else if (vendor.toLowerCase().contains("Venustech".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Venustech");
                        } else if (vendor.toLowerCase().contains("Shenou".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Shenou");
                        } else if (vendor.toLowerCase().contains("REALTEK".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "REALTEK");
                        } else if (vendor.toLowerCase().contains("Lite-On".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "LINK");
                        } else if (vendor.toLowerCase().contains("Shprobe".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "INK");
                        } else if (vendor.toLowerCase().contains("Tiger NetCom".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "LINK");
                        } else if (vendor.toLowerCase().contains("AI-Link".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "AI-Link");
                        } else if (vendor.toLowerCase().contains("Century Xinyang".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Century Xinyang");
                        } else if (vendor.toLowerCase().contains("BaoLun".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "BaoLun");
                        } else if (vendor.toLowerCase().contains("Shiyuan Electronic".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Shiyuan Electronic");
                        } else if (vendor.toLowerCase().contains("SHENZHEN FAST".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "SHENZHEN FAST");
                        } else if (vendor.toLowerCase().contains("AMPAK".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "AMPAK");
                        } else if (vendor.toLowerCase().contains("Midea".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Midea");
                        } else if (vendor.toLowerCase().contains("ASIX ELECTRONICS".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "ASIX ELECTRONICS");
                        } else if (vendor.toLowerCase().contains("Shanghai WDK Industrial".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Shanghai WDK Industrial");
                        }  else if (vendor.toLowerCase().contains("IEEE Registration".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "IEEE Registration");
                        }  else if (vendor.toLowerCase().contains("Alcatel-Lucent".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Alcatel-Lucent");
                        }  else if (vendor.toLowerCase().contains("Toshiba Teli".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Toshiba Teli");
                        }  else if (vendor.toLowerCase().contains("Shenzhen Sunchip Technology".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Shenzhen Sunchip Technology");
                        }  else if (vendor.toLowerCase().contains("DAVICOM SEMICONDUCTOR".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "DAVICOM SEMICONDUCTOR");
                        }else if (vendor.toLowerCase().contains("Zhejiang Uniview".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Zhejiang Uniview");
                        } else if (vendor.toLowerCase().contains("Zhejiang Dahua".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Zhejiang Dahua");
                        } else if (vendor.toLowerCase().contains("SUNIX".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "SUNIX");
                        } else if (vendor.toLowerCase().contains("YEALINK".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "YEALINK");
                        }  else if (vendor.toLowerCase().contains("Uniview".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Uniview");
                        }  else if (vendor.toLowerCase().contains("Camille Bauer".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Camille Bauer");
                        }  else if (vendor.toLowerCase().contains("Microchip  ".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Microchip");
                        }  else if (vendor.toLowerCase().contains("AMPAK".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "AMPAK");
                        }  else if (vendor.toLowerCase().contains("Electronics ".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Electronics");
                        }  else if (vendor.toLowerCase().contains("Shenzhen Decnta".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Shenzhen Decnta");
                        }  else if (vendor.toLowerCase().contains("Shenzhen Seavo".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Shenzhen Seavo");
                        }  else if (vendor.toLowerCase().contains("Shenzhen WiSiYiLink".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Shenzhen WiSiYiLink");
                        }  else if (vendor.toLowerCase().contains("Shenzhen WiSiYiLink".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Shenzhen WiSiYiLink");
                        }   else if (vendor.toLowerCase().contains("SHENZHEN BILIAN ELECTRONIC".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "SHENZHEN BILIAN ELECTRONIC");
                        }   else if (vendor.toLowerCase().contains("Mobile".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Mobile");
                        }  else if (vendor.toLowerCase().contains("ELECTRONIC".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "ELECTRONIC");
                        }  else if (vendor.toLowerCase().contains("Electronics".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "Electronics");
                        }  else if (vendor.toLowerCase().contains("HUAWEI TECHNOLOGIES".toLowerCase())) {
                            insertDeviceScan(probe, vendor, probe.getMac(), "HUAWEI TECHNOLOGIES");
                        } else if(vendor.toLowerCase().toLowerCase().contains("openbsd".toLowerCase())){
                            insertDeviceScan(probe, vendor, probe.getMac(), "openbsd");
                        } else{
                            insertV6Terminal(probe);
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
            terminal.setMacvendor(probe.getMacVendor());
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
