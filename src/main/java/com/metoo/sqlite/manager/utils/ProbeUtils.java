package com.metoo.sqlite.manager.utils;

import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.Arp;
import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.entity.Terminal;
import com.metoo.sqlite.model.TerminalModel;
import com.metoo.sqlite.service.IArpService;
import com.metoo.sqlite.service.IProbeService;
import com.metoo.sqlite.service.ITerminalService;
import com.metoo.sqlite.utils.date.DateTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProbeUtils {


    @Autowired
    private IProbeService probeService;
    @Autowired
    private ITerminalService terminalService;
    @Autowired
    private IArpService arpService;

    public void insertTerminal(){

        Set<String> deleteSet = new HashSet();

        Map<String, TerminalModel> resultMap = new HashMap<>();

        Map params = new HashMap();

        // 需要在metoo_scan后面执行
        params.clear();
        List<Probe> probes = probeService.selectObjByMap(params);

        if(probes.size() > 0){
            // 组合数据，更新终端采集表
            for (Probe probe : probes) {

                String ip = probe.getIp_addr();
                String ipv6 = probe.getIpv6();
                String port_number = probe.getPort_num();
                String os_family = probe.getOs_family();
                String port_service_product = probe.getPort_service_product();
                String vendor = probe.getVendor();
                String os_gen = probe.getOs_gen();
                Float reliability = probe.getReliability();
                Integer ttl = probe.getTtl();
                String os = "";

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
                }else{
                    TerminalModel terminalModel = new TerminalModel(ip, port_number, os_family,
                            port_service_product, os, reliability, ttl, os_gen, vendor, true);
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

                TerminalModel terminalModel = resultMap.get(key);

                if(terminalModel != null){

                    deleteSet.add(key);

                    Terminal terminal = new Terminal();

                    terminal.setCreateTime(DateTools.getCreateTime());

                    if(terminalModel.getPort_numberS().size() > 0){
                        terminal.setActive_port(String.join(",", terminalModel.getPort_numberS()));
                    }

                    if(terminalModel.getPort_service_productS().size() > 0){
                        terminal.setService(String.join(",", terminalModel.getPort_service_productS()));
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
                            terminal.setIpv4addr(key);
                            terminal.setIpv6addr(arp.getIpv6());
                            terminal.setMac(arp.getMac());
                            terminal.setMacvendor(arp.getMacVendor());
                            terminal.setOs(arp.getMacVendor());
                        }
                        terminals.add(terminal);
                    }
                }
            }
        }

        if(terminals.size() > 0){
//            terminalService.batchInsertGather(terminals);
            for (Terminal terminal : terminals) {
                if(StringUtil.isNotEmpty(terminal.getMac())){
                    terminalService.insert(terminal);
                }
            }
        }
        if(deleteSet.size() > 0){
            for (String ip_addr : deleteSet) {
                this.deleteProbe(ip_addr, probeService);
            }
        }

    }


    public void deleteProbe(String ip_addr, IProbeService probeService) {
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
