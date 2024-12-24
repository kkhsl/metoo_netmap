package com.metoo.sqlite.gather.factory.gather.thread;

import cn.hutool.core.map.MapUtil;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Arp;
import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.entity.Terminal;
import com.metoo.sqlite.gather.common.ConcatenationCharacter;
import com.metoo.sqlite.model.TerminalModel;
import com.metoo.sqlite.service.IArpService;
import com.metoo.sqlite.service.IProbeService;
import com.metoo.sqlite.service.ITerminalService;
import com.metoo.sqlite.utils.StringUtils;
import com.metoo.sqlite.utils.date.DateTools;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-28 21:51
 */
@Slf4j
public class GatherTerminal implements Gather {

    @Override
    public void executeMethod() {

        Long time = System.currentTimeMillis();

        log.info("Terminal start ......");

        this.version();

        log.info("Terminal end ....." + (System.currentTimeMillis() - time));
    }

    public void version(){
        ITerminalService terminalService = (ITerminalService) ApplicationContextUtils.getBean("terminalServiceImpl");
        IProbeService probeService = (IProbeService) ApplicationContextUtils.getBean("probeServiceImpl");

        terminalService.deleteTableGather();
        terminalService.deleteTable();

        List<Probe> probeList = probeService.selectDeduplicationByIp(Collections.emptyMap());

        if(probeList.size() > 0){

            for (Probe probe : probeList) {

                String ipv4 = probe.getIp_addr();
                String ipv6 = probe.getIpv6();
                String mac = probe.getMac();
                String mac_vendor = probe.getMac_vendor();
                String os_family = probe.getOs_family();
                String application_protocol = probe.getApplication_protocol();
                String vendor = probe.getVendor();
                String os_gen = probe.getOs_gen();
                String os = "";

                boolean flag = false;

                String ttls = probe.getTtls();
                if(StringUtil.isNotEmpty(ttls)){
                    String[] eles = ttls.split(",");
                    for (String ele : eles) {
                        if(Integer.parseInt(ele) > 120 && Integer.parseInt(ele) < 129){
                            flag = true;
                            os = ConcatenationCharacter
                                    .concatenateWithSpace(":",
                                            ConcatenationCharacter.disassembleWithSpace(",", vendor),
                                            ConcatenationCharacter.disassembleWithSpace(",", os_gen),
                                            ConcatenationCharacter.disassembleWithSpace(",", os_family));
                            if(StringUtil.isEmpty(os)){
                                os = "Windows";
                            }
                        }
                            break;
                        }
                }

//                if(!flag && port_service_vendor != null && port_service_vendor.toLowerCase().contains("microsoft")){
//                    flag = true;
//                    os = "Windows";
//                }

                if(!flag && vendor != null && (
                        vendor.toLowerCase().contains("microsoft")
                        || vendor.toLowerCase().contains("apple")
                        || vendor.toLowerCase().contains("google"))){
                    flag = true;
                    os = ConcatenationCharacter
                            .concatenateWithSpace(":",
                                    ConcatenationCharacter.disassembleWithSpace(",", vendor),
                                    ConcatenationCharacter.disassembleWithSpace(",", os_gen),
                                    ConcatenationCharacter.disassembleWithSpace(",", os_family));

                }

//                if(!flag && vendor != null && (vendor.toLowerCase().contains("linux") || vendor.toLowerCase().contains("motorola") || vendor.toLowerCase().contains("cannon"))){
//                    flag = true;
//                    os = ConcatenationCharacter
//                            .concatenateWithSpace(":",
//                                    ConcatenationCharacter.disassembleWithSpace(",", vendor),
//                                    ConcatenationCharacter.disassembleWithSpace(",", os_gen));
//
//                }

                if(!flag && application_protocol != null && (application_protocol.toLowerCase().contains("msrpc")
                                || application_protocol.toLowerCase().contains("netbios-ssn")
                                || application_protocol.toLowerCase().contains("ms-wbt-server")
                                || application_protocol.toLowerCase().contains("microsoft-ds"))){
                    os = "Windows";
                    flag = true;
                }

                String service = ConcatenationCharacter.disassembleWithSpace(",", probe.getApplication_protocol());
                String activeport = ConcatenationCharacter.disassembleWithSpace(",", probe.getPort_num());

                // 写入终端采集表
                if(flag){
                    Terminal terminal = new Terminal();
                    terminal.setIpv4addr(ipv4);
                    terminal.setIpv6addr(ipv6);
                    terminal.setMac(mac);
                    terminal.setMacvendor(mac_vendor);
                    terminal.setOs(os);
                    terminal.setActive_port(activeport);
                    terminal.setService(service);
                    terminalService.insertGather(terminal);
                    probeService.deleteProbeByIp(ipv4, ipv6);
                }

            }
        }

        // 复制数据到终端表
        terminalService.copyGatherData();
    }

}
