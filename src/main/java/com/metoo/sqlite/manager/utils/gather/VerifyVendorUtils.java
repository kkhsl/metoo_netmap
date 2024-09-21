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
                    String vendor = toDeviceScan(terminal.getMacvendor());
                    if(StringUtil.isNotEmpty(vendor)){
                        insertDeviceScan(terminal, vendor);
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
            this.deleteTerminal(terminal.getIpv4addr());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteTerminal(String ip) {
        if(StringUtil.isNotEmpty(ip)){
            Map params = new HashMap();
            params.put("ipv4addr", ip);
            List<Terminal> terminalList = this.terminalService.selectObjByMap(params);
            if(terminalList.size() > 0){
                for (Terminal terminal : terminalList) {
                    this.terminalService.delete(terminal.getId());
                }
            }
        }
    }


    private static String toDeviceScan(String vendor){
        if (vendor.toLowerCase().contains("Tenda".toLowerCase())) {
            return "Tenda";
        } else if (vendor.toLowerCase().toLowerCase().contains("TP-LINK".toLowerCase())) {
            return "TP-LINK";
        } else if (vendor.toLowerCase().toLowerCase().contains("h3c".toLowerCase())) {
            return "h3c";
        } else if (vendor.toLowerCase().toLowerCase().contains("Tiandy".toLowerCase())) {
            return "Tiandy";
        } else if (vendor.toLowerCase().toLowerCase().contains("Lite-On".toLowerCase())) {
            return "Lite-On";
        } else if (vendor.toLowerCase().toLowerCase().contains("mercury".toLowerCase())) {
            return "mercury";
        } else if (vendor.toLowerCase().toLowerCase().contains("Huawei Device".toLowerCase())) {
            return "Huawei Device";
        } else if (vendor.toLowerCase().contains("ruijie".toLowerCase())) {
            return "ruijie";
        } else if (vendor.toLowerCase().contains("PUTIAN".toLowerCase())) {
            return "PUTIAN";
        } else if (vendor.toLowerCase().toLowerCase().contains("Hikvision".toLowerCase())) {
            return "Hikvision";
        } else if (vendor.toLowerCase().toLowerCase().contains("Dahua".toLowerCase())) {
            return "Dahua";
        } else if (vendor.toLowerCase().toLowerCase().contains("Ezviz".toLowerCase())) {
            return "Ezviz";
        } else if (vendor.toLowerCase().toLowerCase().contains("Panasonic".toLowerCase())) {
            return "Panasonic";
        } else if (vendor.toLowerCase().toLowerCase().contains("Logitech".toLowerCase())) {
            return "Logitech";
        } else if (vendor.toLowerCase().contains("Tiandy".toLowerCase())) {
            return "Tiandy";
        } else if (vendor.toLowerCase().contains("Cannon".toLowerCase())) {
            return "Cannon";
        } else if (vendor.toLowerCase().contains("Samsung".toLowerCase())) {
            return "Samsung";
        } else if (vendor.toLowerCase().contains("RICOH".toLowerCase())) {
            return "RICOH";
        } else if (vendor.toLowerCase().contains("Epson".toLowerCase())) {
            return "Epson";
        } else if (vendor.toLowerCase().contains("Brother".toLowerCase())) {
            return "Brother";
        } else if (vendor.toLowerCase().contains("KONICA".toLowerCase())) {
            return "KONICA";
        } else if (vendor.toLowerCase().contains("MINOLTA".toLowerCase())) {
            return "MINOLTA";
        } else if (vendor.toLowerCase().contains("LEXMARK".toLowerCase())) {
            return "LEXMARK";
        } else if (vendor.toLowerCase().contains("Sundray".toLowerCase())) {
            return "Sundray";
        } else if (vendor.toLowerCase().contains("IEIT SYSTEMS".toLowerCase())) {
            return "IEIT SYSTEMS";
        } else if (vendor.toLowerCase().contains("Topsec".toLowerCase())) {
            return "Topsec";
        } else if (vendor.toLowerCase().contains("Sangfor".toLowerCase())) {
            return "Sangfor";
        } else if (vendor.toLowerCase().contains("Jiangxi".toLowerCase())) {
            return "Jiangxi";
        } else if (vendor.toLowerCase().contains("Xunte".toLowerCase())) {
            return "Xunte";
        } else if (vendor.toLowerCase().contains("DPtech".toLowerCase())) {
            return "DPtech";
        } else if (vendor.toLowerCase().contains("D-Link".toLowerCase())) {
            return "D-Link";
        } else if (vendor.toLowerCase().contains("Tenda".toLowerCase())) {
            return "Tenda";
        } else if (vendor.toLowerCase().contains("NETGEAR".toLowerCase())) {
            return "NETGEAR";
        } else if (vendor.toLowerCase().contains("NETCORE".toLowerCase())) {
            return "NETCORE";
        } else if (vendor.toLowerCase().contains("MERCURY".toLowerCase())) {
            return "MERCURY";
        } else if (vendor.toLowerCase().contains("zte".toLowerCase())) {
            return "zte";
        } else if (vendor.toLowerCase().contains("Fiberhome".toLowerCase())) {
            return "Fiberhome";
        } else if (vendor.toLowerCase().contains("Ericsson".toLowerCase())) {
            return "Ericsson";
        } else if (vendor.toLowerCase().contains("Cisco".toLowerCase())) {
            return "Cisco";
        } else if (vendor.toLowerCase().contains("Juniper".toLowerCase())) {
            return "Juniper";
        } else if (vendor.toLowerCase().contains("Brocade".toLowerCase())) {
            return "Brocade";
        } else if (vendor.toLowerCase().contains("Extreme".toLowerCase())) {
            return "Extreme";
        } else if (vendor.toLowerCase().contains("ProCurve".toLowerCase())) {
            return "ProCurve";
        } else if (vendor.toLowerCase().contains("Maipu".toLowerCase())) {
            return "Maipu";
        } else if (vendor.toLowerCase().contains("Ruijie".toLowerCase())) {
            return "Ruijie";
        } else if (vendor.toLowerCase().contains("Venustech".toLowerCase())) {
            return "Venustech";
        } else if (vendor.toLowerCase().contains("Shenou".toLowerCase())) {
            return "Shenou";
        } else if (vendor.toLowerCase().contains("REALTEK".toLowerCase())) {
            return "REALTEK";
        } else if (vendor.toLowerCase().contains("Lite-On".toLowerCase())) {
            return "Lite-On";
        } else if (vendor.toLowerCase().contains("Sharp".toLowerCase())) {
            return "Sharp";
        } else if (vendor.toLowerCase().contains("Tiger NetCom".toLowerCase())) {
            return "Tiger NetCom";
        } else if (vendor.toLowerCase().contains("AI-Link".toLowerCase())) {
            return "AI-Link";
        } else if (vendor.toLowerCase().contains("Century Xinyang".toLowerCase())) {
            return "Century Xinyang";
        } else if (vendor.toLowerCase().contains("BaoLun".toLowerCase())) {
            return "BaoLun";
        } else if (vendor.toLowerCase().contains("Shiyuan Electronic".toLowerCase())) {
            return "Shiyuan Electronic";
        } else if (vendor.toLowerCase().contains("SHENZHEN FAST".toLowerCase())) {
            return "SHENZHEN FAST";
        } else if (vendor.toLowerCase().contains("AMPAK".toLowerCase())) {
            return "AMPAK";
        } else if (vendor.toLowerCase().contains("Midea".toLowerCase())) {
            return "Midea";
        } else if (vendor.toLowerCase().contains("ASIX ELECTRONICS".toLowerCase())) {
            return "ASIX ELECTRONICS";
        } else if (vendor.toLowerCase().contains("Shanghai WDK Industrial".toLowerCase())) {
            return "Shanghai WDK Industrial";
        } else

        if (vendor.toLowerCase().contains("Zhejiang Uniview".toLowerCase())) {
            return "Zhejiang Uniview";
        } else if (vendor.toLowerCase().contains("Zhejiang Dahua".toLowerCase())) {
            return "Zhejiang Dahua";
        } else if (vendor.toLowerCase().contains("SUNIX".toLowerCase())) {
            return "SUNIX";
        } else if (vendor.toLowerCase().contains("YEALINK(XIAMEN)".toLowerCase())) {
            return "YEALINK(XIAMEN)";
        }  else if (vendor.toLowerCase().contains("Uniview".toLowerCase())) {
            return "Uniview";
        }  else if (vendor.toLowerCase().contains("Camille Bauer".toLowerCase())) {
            return "Camille Bauer";
        }  else if (vendor.toLowerCase().contains("Microchip".toLowerCase())) {
            return "Microchip ";
        }  else if (vendor.toLowerCase().contains("AMPAK".toLowerCase())) {
            return "AMPAK";
        }  else if (vendor.toLowerCase().contains("LCFC(Hefei) Electronics".toLowerCase())) {
            return "LCFC(Hefei) Electronics";
        }  else if (vendor.toLowerCase().contains("Shenzhen Decnta".toLowerCase())) {
            return "Shenzhen Decnta";
        }  else if (vendor.toLowerCase().contains("Shenzhen Seavo".toLowerCase())) {
            return "Shenzhen Seavo";
        }  else if (vendor.toLowerCase().contains("Shenzhen WiSiYiLink".toLowerCase())) {
            return "Shenzhen WiSiYiLink";
        }  else if (vendor.toLowerCase().contains("Shenzhen WiSiYiLink".toLowerCase())) {
            return "SHENZHEN BILIAN ELECTRONIC";
        }   else if (vendor.toLowerCase().contains("SHENZHEN BILIAN ELECTRONIC".toLowerCase())) {
            return "Shenzhen NORCO lntelligent";
        }   else if (vendor.toLowerCase().contains("Shenzhen NORCO lntelligent".toLowerCase())) {
            return "Realme Chongqing Mobile";
        }  else if (vendor.toLowerCase().contains("LCFC(Hefei) Electronics".toLowerCase())) {
            return "LCFC(Hefei) Electronics";
        }  else if (vendor.toLowerCase().contains("HUAWEI TECHNOLOGIES".toLowerCase())) {
            return "HUAWEI TECHNOLOGIES";
        }  else if (vendor.toLowerCase().contains("IEEE Registration".toLowerCase())) {
            return "IEEE Registration";
        }  else if (vendor.toLowerCase().contains("IEEE Registration".toLowerCase())) {
            return "Alcatel-Lucent";
        }  else if (vendor.toLowerCase().contains("Alcatel-Lucent".toLowerCase())) {
            return "Toshiba Teli";
        }  else if (vendor.toLowerCase().contains("Toshiba Teli".toLowerCase())) {
            return "DAVICOM SEMICONDUCTOR";
        }  else if (vendor.toLowerCase().contains("Shenzhen Sunchip Technology".toLowerCase())) {
            return "Shenzhen Sunchip Technology";
        }  else if (vendor.toLowerCase().contains("openbsd".toLowerCase())) {
            return "openbsd";
        }  else if (vendor.toLowerCase().contains("Routerboard.com".toLowerCase())) {
            return "Routerboard.com";
        }
        return "";
    }

}
