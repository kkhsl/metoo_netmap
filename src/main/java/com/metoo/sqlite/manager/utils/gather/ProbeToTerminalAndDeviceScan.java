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
    private IArpService arpService;
    @Autowired
    private IMacVendorService macVendorService;



    public void insertDeviceScan(Arp arp, String macVendor, String mac, String os){
        DeviceScan deviceScan = new DeviceScan();
        deviceScan.setDevice_ipv6(arp.getIpv6());
        deviceScan.setDevice_product(macVendor);
        deviceScan.setMac(mac);
        deviceScan.setMacVendor(macVendor);
        deviceScan.setOs(os);
        try {
            Map params = new HashMap();
            params.put("device_ipv4", arp.getIp());
            List<DeviceScan> deviceScanList = this.deviceScanService.selectObjByMap(params);
            if(deviceScanList.size() <= 0){
                this.deviceScanService.insert(deviceScan);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.deleteProbe(arp.getIp());
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
                    params.clear();
                    params.put("ip", probe.getIp_addr());
                    List<Arp> arps = this.arpService.selectObjByMap(params);
                    if (arps.size() > 0) {
                        Arp arp = arps.get(0);
                        if (StringUtil.isNotEmpty(arp.getMac())) {
                            if (!MacUtils.getMac(arp.getMac()).equals("") && MacUtils.isValidMACAddress(arp.getMac())) {
                                params.clear();
                                params.put("mac", MacUtils.getMac(arp.getMac()));
                                List<MacVendor> macVendorList = macVendorService.selectObjByMap(params);
                                if (macVendorList.size() > 0) {
                                    MacVendor macVendor = macVendorList.get(0);
                                    if (macVendor.getVendor() != null) {
                                        String vendor = macVendor.getVendor();
                                        if (vendor.toLowerCase().contains("Tenda".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Tenda");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("h3c".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "h3c");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("TP-LINK".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "TP-LINK");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("Lite-On".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Lite-On");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("mercury".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "mercury");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("Huawei Device".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Huawei Device");
                                        } else if (vendor.toLowerCase().contains("ruijie".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "ruijie");
                                        } else if (vendor.toLowerCase().contains("PUTIAN".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "PUTIAN");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("Hikvision".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Hikvision");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("Dahua".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Dahua");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("Ezviz".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Ezviz");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("Panasonic".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Panasonic");
                                        } else if (vendor.toLowerCase().toLowerCase().contains("Logitech".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Logitech");
                                        } else if (vendor.toLowerCase().contains("Tiandy".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Tiandy");
                                        } else if (vendor.toLowerCase().contains("Cannon".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Cannon");
                                        } else if (vendor.toLowerCase().contains("Samsung".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Samsung");
                                        } else if (vendor.toLowerCase().contains("RICOH".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "RICOH");
                                        } else if (vendor.toLowerCase().contains("Epson".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Epson");
                                        } else if (vendor.toLowerCase().contains("Brother".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Brother");
                                        } else if (vendor.toLowerCase().contains("KONICA".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "KONICA");
                                        } else if (vendor.toLowerCase().contains("MINOLTA".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "MINOLTA");
                                        } else if (vendor.toLowerCase().contains("LEXMARK".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "LEXMARK");
                                        } else if (vendor.toLowerCase().contains("Sundray".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Sundray");
                                        } else if (vendor.toLowerCase().contains("IEIT SYSTEMS".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "IEIT SYSTEMS");
                                        } else if (vendor.toLowerCase().contains("Topsec".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Topsec");
                                        } else if (vendor.toLowerCase().contains("Sangfor".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Sangfor");
                                        } else if (vendor.toLowerCase().contains("Jiangxi".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Jiangxi");
                                        } else if (vendor.toLowerCase().contains("Xunte".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Xunte");
                                        } else if (vendor.toLowerCase().contains("DPtech".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "DPtech");
                                        } else if (vendor.toLowerCase().contains("D-Link".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "D-Link");
                                        } else if (vendor.toLowerCase().contains("Tenda".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Tenda");
                                        } else if (vendor.toLowerCase().contains("NETGEAR".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "NETGEAR");
                                        } else if (vendor.toLowerCase().contains("NETCORE".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "NETCORE");
                                        } else if (vendor.toLowerCase().contains("MERCURY".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "MERCURY");
                                        } else if (vendor.toLowerCase().contains("zte".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "zte");
                                        } else if (vendor.toLowerCase().contains("Fiberhome".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Fiberhome");
                                        } else if (vendor.toLowerCase().contains("Ericsson".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Ericsson");
                                        } else if (vendor.toLowerCase().contains("Cisco".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Cisco");
                                        } else if (vendor.toLowerCase().contains("Juniper".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Juniper");
                                        } else if (vendor.toLowerCase().contains("Brocade".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Brocade");
                                        } else if (vendor.toLowerCase().contains("Extreme".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Extreme");
                                        } else if (vendor.toLowerCase().contains("ProCurve".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "ProCurve");
                                        } else if (vendor.toLowerCase().contains("Maipu".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Maipu");
                                        } else if (vendor.toLowerCase().contains("Ruijie".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Ruijie");
                                        } else if (vendor.toLowerCase().contains("Venustech".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Venustech");
                                        } else if (vendor.toLowerCase().contains("Shenou".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Shenou");
                                        } else if (vendor.toLowerCase().contains("Communication".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Communication");
                                        } else if (vendor.toLowerCase().contains("REALTEK".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "REALTEK");
                                        } else if (vendor.toLowerCase().contains("Lite-On".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "LINK");
                                        } else if (vendor.toLowerCase().contains("Sharp".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "INK");
                                        } else if (vendor.toLowerCase().contains("Tiger NetCom".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "LINK");
                                        } else if (vendor.toLowerCase().contains("AI-Link".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "AI-Link");
                                        } else if (vendor.toLowerCase().contains("Century Xinyang".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Century Xinyang");
                                        } else if (vendor.toLowerCase().contains("BaoLun".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "BaoLun");
                                        } else if (vendor.toLowerCase().contains("Shiyuan Electronic".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Shiyuan Electronic");
                                        } else if (vendor.toLowerCase().contains("SHENZHEN FAST".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "SHENZHEN FAST");
                                        } else if (vendor.toLowerCase().contains("AMPAK".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "AMPAK");
                                        } else if (vendor.toLowerCase().contains("Midea".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Midea");
                                        } else if (vendor.toLowerCase().contains("ASIX ELECTRONICS".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "ASIX ELECTRONICS");
                                        } else if (vendor.toLowerCase().contains("Shanghai WDK Industrial".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Shanghai WDK Industrial");
                                        } else
                                        if (vendor.toLowerCase().contains("Zhejiang Uniview".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Zhejiang Uniview");
                                        } else if (vendor.toLowerCase().contains("Zhejiang Dahua".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Zhejiang Dahua");
                                        } else if (vendor.toLowerCase().contains("SUNIX".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "SUNIX");
                                        } else if (vendor.toLowerCase().contains("YEALINK".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "YEALINK");
                                        }  else if (vendor.toLowerCase().contains("Uniview".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Uniview");
                                        }  else if (vendor.toLowerCase().contains("Camille Bauer".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Camille Bauer");
                                        }  else if (vendor.toLowerCase().contains("Microchip  ".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Microchip");
                                        }  else if (vendor.toLowerCase().contains("AMPAK".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "AMPAK");
                                        }  else if (vendor.toLowerCase().contains("Electronics ".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Electronics");
                                        }  else if (vendor.toLowerCase().contains("Shenzhen Decnta".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Shenzhen Decnta");
                                        }  else if (vendor.toLowerCase().contains("Shenzhen Seavo".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Shenzhen Seavo");
                                        }  else if (vendor.toLowerCase().contains("Shenzhen WiSiYiLink".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Shenzhen WiSiYiLink");
                                        }  else if (vendor.toLowerCase().contains("Realme Chongqing Mobile".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Realme Chongqing Mobile");
                                        }  else if (vendor.toLowerCase().contains("ELECTRONIC".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "ELECTRONIC");
                                        }  else if (vendor.toLowerCase().contains("IEEE Registration".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "IEEE Registration");
                                        }  else if (vendor.toLowerCase().contains("Alcatel-Lucent".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Alcatel-Lucent");
                                        }  else if (vendor.toLowerCase().contains("Toshiba Teli".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Toshiba Teli");
                                        }  else if (vendor.toLowerCase().contains("Shenzhen Sunchip Technology".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "Shenzhen Sunchip Technology");
                                        }  else if (vendor.toLowerCase().contains("DAVICOM SEMICONDUCTOR".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "DAVICOM SEMICONDUCTOR");
                                        }else if(vendor.toLowerCase().toLowerCase().contains("openbsd".toLowerCase())){
                                            insertDeviceScan(arp, vendor, arp.getMac(),"openbsd");
                                        } if (vendor.toLowerCase().contains("LCFC(Hefei) Electronics".toLowerCase())) {
                                            insertDeviceScan(arp, vendor, arp.getMac(), "LCFC(Hefei) Electronics");
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
                }
            }

            this.probeUtils.insertTerminal();// 剩余probe写入终端
        }
        // arp表中只有ipv6地址没有ipv4地址的条目放入terminal_detail，os填mac_vendor的内容
        params.clear();
        List<Arp> arps = this.arpService.selectObjByMap(params);
        if(arps.size() > 0){
            for (Arp arp : arps) {
                if(StringUtil.isEmpty(arp.getIp()) && StringUtil.isNotEmpty(arp.getIpv6())){
//                    insertTerminal(arp); 直接插入终端表
                    v6ToTerminal(arp);
                }
            }
        }
    }

    public void insertTerminal(Probe probe, String os){
        Map params = new HashMap();
        Terminal terminal = new Terminal();
        terminal.setOs(os);
        if(StringUtil.isNotEmpty(probe.getIp_addr())){
            terminal.setIpv4addr(probe.getIp_addr());
            params.clear();
            params.put("likeIp", "%" + probe.getIp_addr() + "%");
            List<Arp> arps = arpService.selectObjByMap(params);
            if(arps.size() > 0){
                Arp arp = arps.get(0);
                terminal.setIpv6addr(arp.getIpv6());
                terminal.setMac(arp.getMac());
                terminal.setMacvendor(arp.getMacVendor());
            }
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

    public void v6ToTerminal(Arp arp){
        Map params = new HashMap();
        if(StringUtil.isNotEmpty(arp.getMac())){
            if(!MacUtils.getMac(arp.getMac()).equals("") && MacUtils.isValidMACAddress(arp.getMac())){
                params.clear();
                params.put("mac", MacUtils.getMac(arp.getMac()));
                List<MacVendor> macVendorList = macVendorService.selectObjByMap(params);
                if(macVendorList.size() > 0){
                    MacVendor macVendor = macVendorList.get(0);
                    if(macVendor.getVendor() != null){
                        String vendor = macVendor.getVendor();
                        if (vendor.toLowerCase().contains("Tenda".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Tenda");
                        } else if (vendor.toLowerCase().toLowerCase().contains("h3c".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "h3c");
                        } else if (vendor.toLowerCase().toLowerCase().contains("TP-LINK".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "TP-LINK");
                        } else if (vendor.toLowerCase().toLowerCase().contains("Lite-On".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Lite-On");
                        } else if (vendor.toLowerCase().toLowerCase().contains("mercury".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "mercury");
                        } else if (vendor.toLowerCase().toLowerCase().contains("Huawei Device".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Huawei Device");
                        } else if (vendor.toLowerCase().contains("ruijie".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "ruijie");
                        } else if (vendor.toLowerCase().contains("PUTIAN".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "PUTIAN");
                        } else if (vendor.toLowerCase().toLowerCase().contains("Hikvision".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Hikvision");
                        } else if (vendor.toLowerCase().toLowerCase().contains("Dahua".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Dahua");
                        } else if (vendor.toLowerCase().toLowerCase().contains("Ezviz".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Ezviz");
                        } else if (vendor.toLowerCase().toLowerCase().contains("Panasonic".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Panasonic");
                        } else if (vendor.toLowerCase().toLowerCase().contains("Logitech".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Logitech");
                        } else if (vendor.toLowerCase().contains("Tiandy".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Tiandy");
                        } else if (vendor.toLowerCase().contains("Cannon".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Cannon");
                        } else if (vendor.toLowerCase().contains("Samsung".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Samsung");
                        } else if (vendor.toLowerCase().contains("RICOH".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "RICOH");
                        } else if (vendor.toLowerCase().contains("Epson".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Epson");
                        } else if (vendor.toLowerCase().contains("Brother".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Brother");
                        } else if (vendor.toLowerCase().contains("KONICA".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "KONICA");
                        } else if (vendor.toLowerCase().contains("MINOLTA".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "MINOLTA");
                        } else if (vendor.toLowerCase().contains("LEXMARK".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "LEXMARK");
                        } else if (vendor.toLowerCase().contains("Sundray".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Sundray");
                        } else if (vendor.toLowerCase().contains("IEIT SYSTEMS".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "IEIT SYSTEMS");
                        } else if (vendor.toLowerCase().contains("Topsec".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Topsec");
                        } else if (vendor.toLowerCase().contains("Sangfor".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Sangfor");
                        } else if (vendor.toLowerCase().contains("Jiangxi".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Jiangxi");
                        } else if (vendor.toLowerCase().contains("Xunte".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Xunte");
                        } else if (vendor.toLowerCase().contains("DPtech".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "DPtech");
                        } else if (vendor.toLowerCase().contains("D-Link".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "D-Link");
                        } else if (vendor.toLowerCase().contains("Tenda".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Tenda");
                        } else if (vendor.toLowerCase().contains("NETGEAR".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "NETGEAR");
                        } else if (vendor.toLowerCase().contains("NETCORE".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "NETCORE");
                        } else if (vendor.toLowerCase().contains("MERCURY".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "MERCURY");
                        } else if (vendor.toLowerCase().contains("zte".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "zte");
                        } else if (vendor.toLowerCase().contains("Fiberhome".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Fiberhome");
                        } else if (vendor.toLowerCase().contains("Ericsson".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Ericsson");
                        } else if (vendor.toLowerCase().contains("Cisco".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Cisco");
                        } else if (vendor.toLowerCase().contains("Juniper".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Juniper");
                        } else if (vendor.toLowerCase().contains("Brocade".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Brocade");
                        } else if (vendor.toLowerCase().contains("Extreme".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Extreme");
                        } else if (vendor.toLowerCase().contains("ProCurve".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "ProCurve");
                        } else if (vendor.toLowerCase().contains("Maipu".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Maipu");
                        } else if (vendor.toLowerCase().contains("Ruijie".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Ruijie");
                        } else if (vendor.toLowerCase().contains("Venustech".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Venustech");
                        } else if (vendor.toLowerCase().contains("Shenou".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Shenou");
                        } else if (vendor.toLowerCase().contains("REALTEK".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "REALTEK");
                        } else if (vendor.toLowerCase().contains("Lite-On".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "LINK");
                        } else if (vendor.toLowerCase().contains("Sharp".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "INK");
                        } else if (vendor.toLowerCase().contains("Tiger NetCom".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "LINK");
                        } else if (vendor.toLowerCase().contains("AI-Link".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "AI-Link");
                        } else if (vendor.toLowerCase().contains("Century Xinyang".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Century Xinyang");
                        } else if (vendor.toLowerCase().contains("BaoLun".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "BaoLun");
                        } else if (vendor.toLowerCase().contains("Shiyuan Electronic".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Shiyuan Electronic");
                        } else if (vendor.toLowerCase().contains("SHENZHEN FAST".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "SHENZHEN FAST");
                        } else if (vendor.toLowerCase().contains("AMPAK".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "AMPAK");
                        } else if (vendor.toLowerCase().contains("Midea".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Midea");
                        } else if (vendor.toLowerCase().contains("ASIX ELECTRONICS".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "ASIX ELECTRONICS");
                        } else if (vendor.toLowerCase().contains("Shanghai WDK Industrial".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Shanghai WDK Industrial");
                        }  else if (vendor.toLowerCase().contains("IEEE Registration".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "IEEE Registration");
                        }  else if (vendor.toLowerCase().contains("Alcatel-Lucent".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Alcatel-Lucent");
                        }  else if (vendor.toLowerCase().contains("Toshiba Teli".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Toshiba Teli");
                        }  else if (vendor.toLowerCase().contains("Shenzhen Sunchip Technology".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Shenzhen Sunchip Technology");
                        }  else if (vendor.toLowerCase().contains("DAVICOM SEMICONDUCTOR".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "DAVICOM SEMICONDUCTOR");
                        }else if (vendor.toLowerCase().contains("Zhejiang Uniview".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Zhejiang Uniview");
                        } else if (vendor.toLowerCase().contains("Zhejiang Dahua".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Zhejiang Dahua");
                        } else if (vendor.toLowerCase().contains("SUNIX".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "SUNIX");
                        } else if (vendor.toLowerCase().contains("YEALINK".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "YEALINK");
                        }  else if (vendor.toLowerCase().contains("Uniview".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Uniview");
                        }  else if (vendor.toLowerCase().contains("Camille Bauer".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Camille Bauer");
                        }  else if (vendor.toLowerCase().contains("Microchip  ".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Microchip");
                        }  else if (vendor.toLowerCase().contains("AMPAK".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "AMPAK");
                        }  else if (vendor.toLowerCase().contains("Electronics ".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Electronics");
                        }  else if (vendor.toLowerCase().contains("Shenzhen Decnta".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Shenzhen Decnta");
                        }  else if (vendor.toLowerCase().contains("Shenzhen Seavo".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Shenzhen Seavo");
                        }  else if (vendor.toLowerCase().contains("Shenzhen WiSiYiLink".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Shenzhen WiSiYiLink");
                        }  else if (vendor.toLowerCase().contains("Shenzhen WiSiYiLink".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Shenzhen WiSiYiLink");
                        }   else if (vendor.toLowerCase().contains("SHENZHEN BILIAN ELECTRONIC".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "SHENZHEN BILIAN ELECTRONIC");
                        }   else if (vendor.toLowerCase().contains("Mobile".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Mobile");
                        }  else if (vendor.toLowerCase().contains("ELECTRONIC".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "ELECTRONIC");
                        }  else if (vendor.toLowerCase().contains("Electronics".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "Electronics");
                        }  else if (vendor.toLowerCase().contains("HUAWEI TECHNOLOGIES".toLowerCase())) {
                            insertDeviceScan(arp, vendor, arp.getMac(), "HUAWEI TECHNOLOGIES");
                        } else if(vendor.toLowerCase().toLowerCase().contains("openbsd".toLowerCase())){
                            insertDeviceScan(arp, vendor, arp.getMac(), "openbsd");
                        } else{
                            insertV6Terminal(arp);
                        }
                    }
                }else{
                    insertV6Terminal(arp);
                }
            }
        }
    }

    public void insertV6Terminal(Arp arp) {
        Map params = new HashMap();
        Terminal terminal = new Terminal();
        if (StringUtil.isNotEmpty(arp.getIpv6())) {
            terminal.setIpv6addr(arp.getIpv6());
            terminal.setMac(arp.getMac());
            terminal.setMacvendor(arp.getMacVendor());
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
