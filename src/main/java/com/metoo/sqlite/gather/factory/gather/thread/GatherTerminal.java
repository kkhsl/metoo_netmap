package com.metoo.sqlite.gather.factory.gather.thread;

import cn.hutool.core.map.MapUtil;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Arp;
import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.entity.Terminal;
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
        IArpService arpService = (IArpService) ApplicationContextUtils.getBean("arpServiceImpl");
        IProbeService probeService = (IProbeService) ApplicationContextUtils.getBean("probeServiceImpl");

        terminalService.deleteTableGather();

        Set<String> deleteSet = new HashSet();

        Map<String, TerminalModel> resultMap = new HashMap<>();

        Map params = new HashMap();

        params.clear();
        List<Probe> probes = probeService.selectObjByMap(params);

        if(probes.size() > 0){
            for (Probe probe : probes) {

                boolean flag = false;

                String ip = probe.getIp_addr();
                String ipv6 = probe.getIpv6();
                String port_number = probe.getPort_num();
                String os_family = probe.getOs_family();
                String port_service_product = probe.getPort_service_product();
                String port_service_vendor = probe.getPort_service_vendor();
                String vendor = probe.getVendor();
                String os_gen = probe.getOs_gen();
                Float reliability = probe.getReliability();
                Integer ttl = probe.getTtl();
                String os = "";

                if(probe.getTtl() != null){
                    if(probe.getTtl() > 120 && probe.getTtl() < 129){
                        flag = true;
                        os = "Windows";
                    }
                }

                if(port_service_vendor != null && port_service_vendor.toLowerCase().contains("microsoft")){
                    flag = true;
                    os = "Windows";
                }

                if(vendor != null){
                    if(vendor.toLowerCase().contains("microsoft")
                            || vendor.toLowerCase().contains("apple")
                            || vendor.toLowerCase().contains("google")
                            || vendor.toLowerCase().contains("linux")
                            || vendor.toLowerCase().contains("motorola")
                            || vendor.toLowerCase().contains("cannon")){
                        flag = true;
                    }
                }



                if(resultMap.containsKey(ip) || resultMap.containsKey(ipv6)){
                    TerminalModel terminalModel = resultMap.get(ip);
                    if(terminalModel == null){
                        resultMap.get(ipv6);
                    }
                    if(StringUtil.isNotEmpty(port_number) && !terminalModel.containsPort_number(port_number)){
                        terminalModel.addPort_number(port_number);
                    }
                    if(StringUtil.isNotEmpty(os_family) && !terminalModel.containsOs_family(os_family)){
                        terminalModel.addOs_family(os_family);
                    }
                    if(StringUtil.isNotEmpty(port_service_product) && !terminalModel.containsPort_service_product(port_service_product)){
                        terminalModel.addPort_service_product(port_service_product);
                    }
                    if(reliability != null){
                        terminalModel.setReliability(reliability);
                    }
                    if(StringUtil.isNotEmpty(vendor)){
                        terminalModel.setVendor(vendor);
                    }
                    if(StringUtil.isNotEmpty(os_gen)){
                        terminalModel.setOs_gen(os_gen);
                    }
                    if(!terminalModel.isFlag()){
                        terminalModel.setFlag(flag);
                    }

                }else{
                    TerminalModel terminalModel = new TerminalModel(ip, port_number, os_family,
                            port_service_product, os, reliability, ttl, os_gen, vendor, flag);

                    if(StringUtil.isEmpty(ip)){
                        resultMap.put(ipv6, terminalModel);
                    }else{
                        resultMap.put(ip, terminalModel);
                    }

                }
            }
        }
        List<Terminal> terminals = new ArrayList<>();
        if(resultMap != null && !resultMap.isEmpty()){
            for (String key : resultMap.keySet()) {
                if(StringUtil.isNotEmpty(key)){
                    TerminalModel terminalModel = resultMap.get(key);
                    if(terminalModel != null && terminalModel.isFlag()){
                        deleteSet.add(key);

                        Terminal terminal = new Terminal();
                        terminal.setCreateTime(DateTools.getCreateTime());
                        if(terminalModel.getPort_numberS().size() > 0){
                            terminal.setActive_port(String.join(",", terminalModel.getPort_numberS()));
                        }
                        if(terminalModel.getPort_service_productS().size() > 0){
                            terminal.setService(String.join(",", terminalModel.getPort_service_productS()));
                        }
                        if(StringUtil.isEmpty(terminal.getOs())){
                            if(StringUtils.isNotEmpty(terminalModel.getOs_gen())
                                    || StringUtils.isNotEmpty(terminalModel.getVendor())){
                                if(StringUtil.isNotEmpty(terminalModel.getVendor())){
                                    terminal.setOs(terminalModel.getVendor() + " " + terminalModel.getOs_gen());
                                }else{
                                    terminal.setOs(terminalModel.getOs_gen());
                                }
                            }
                        }

                        if(terminal.getId() == null){
                            params.clear();
                            params.put("ip", key);
                            List<Arp> arps = arpService.selectObjByMap(params);
                            if(arps.size() <= 0){
                                params.put("ipv6", key);
                                arps = arpService.selectObjByMap(params);
                            }
                            if(arps.size() > 0){
                                Arp arp = arps.get(0);
                                terminal.setIpv4addr(arp.getIp());
                                terminal.setIpv6addr(arp.getIpv6());
                                terminal.setMac(arp.getMac());
                                terminal.setMacvendor(arp.getMacVendor());
                                terminals.add(terminal);
                            }


                        }
                    }
                }
            }
        }

        if(terminals.size() > 0){
            for (Terminal terminal : terminals) {
                if(StringUtil.isNotEmpty(terminal.getMac())){
                    terminalService.insertGather(terminal);
                }
            }
        }
        if(deleteSet.size() > 0){
            for (String ip_addr : deleteSet) {
                this.deleteProbe(ip_addr, probeService);
            }
        }

        terminalService.copyGatherData();
    }

    public void deleteProbe(String ip_addr, IProbeService probeService) {
        Map params = new HashMap();
        params.put("ip_addr", ip_addr);
        List<Probe> deleteProbes = probeService.selectObjByMap(params);
        if (deleteProbes.size() <= 0) {
            params.put("ipv6", ip_addr);
            deleteProbes = probeService.selectObjByMap(params);
        }
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
