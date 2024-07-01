package com.metoo.sqlite.gather.factory.gather;

import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Arp;
import com.metoo.sqlite.entity.DeviceScan;
import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.entity.Terminal;
import com.metoo.sqlite.model.TerminalModel;
import com.metoo.sqlite.service.IArpService;
import com.metoo.sqlite.service.IDeviceScanService;
import com.metoo.sqlite.service.IProbeService;
import com.metoo.sqlite.service.ITerminalService;
import com.metoo.sqlite.utils.date.DateTools;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

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
        log.info("terminal Start......");

        ITerminalService terminalService = (ITerminalService) ApplicationContextUtils.getBean("terminalServiceImpl");
        IArpService arpService = (IArpService) ApplicationContextUtils.getBean("arpServiceImpl");
        IProbeService probeService = (IProbeService) ApplicationContextUtils.getBean("probeServiceImpl");



        // 写入终端采集表
        terminalService.deleteTableGather();

        List<Arp> arpList = arpService.selectObjByMap(null);

        String createTime = DateTools.getCreateTime();

        List<Terminal> terminals = new ArrayList<>();
        if(arpList.size() > 0){
            for (Arp arp : arpList) {
                Terminal terminal = new Terminal();
                terminal.setCreateTime(createTime);
                terminal.setIpv4addr(arp.getIp());
                terminal.setIpv6addr(arp.getIpv6());
                terminal.setMac(arp.getMac());
                terminal.setMacvendor(arp.getMacVendor());
                terminals.add(terminal);
            }
            if(terminals.size() > 0){
                terminalService.batchInsertGather(terminals);
            }
        }

        // os-os_family port_num-port service-port_serivce_product

        // 使用HashMap来存储合并后的结果
        Map<String, TerminalModel> resultMap = new HashMap<>();

        // 需要在metoo_scan后面执行
        List<Probe> probes = probeService.selectObjByMap(null);
        if(probes.size() > 0){
            // 组合数据，更新终端采集表
            for (Probe probe : probes) {
                String ip = probe.getIp_addr();
                String port_number = probe.getPort_num();
                String os_family = probe.getOs_family();
                String port_service_product = probe.getPort_service_product();

                if(resultMap.containsKey(ip)){
                    TerminalModel terminalModel = resultMap.get(ip);
                    if(StringUtil.isNotEmpty(port_number) && !terminalModel.containsPort_number(port_number)){
                        terminalModel.addPort_number(port_number);
                    }
                    if(StringUtil.isNotEmpty(os_family) && !terminalModel.containsOs_family(os_family)){
                        terminalModel.addOs_family(os_family);
                    }
                    if(StringUtil.isNotEmpty(port_service_product) && !terminalModel.containsPort_service_product(port_service_product)){
                        terminalModel.addPort_service_product(port_service_product);
                    }
                }else{
                    // 如果IP不存在，则直接添加新的NetworkInfo对象
                    TerminalModel terminalModel = new TerminalModel(ip, port_number, os_family, port_service_product);
                    resultMap.put(ip, terminalModel);
                }
            }
        }

        List<Terminal> terminalList = new ArrayList<>();

        if(resultMap != null && !resultMap.isEmpty()){
            Map params = new HashMap();
            for (String key : resultMap.keySet()) {
                TerminalModel terminalModel = resultMap.get(key);
                if(terminalModel != null){
                    params.clear();
                    params.put("ipv4addr", key);
                    List<Terminal> terminalListByIp = terminalService.selectObjByMap(params);
                    if(terminalListByIp.size() > 0){
                        Terminal terminal = terminalListByIp.get(0);
                        terminal.setCreateTime(createTime);
                        if(terminalModel.getPort_numberS().size() > 0){
                            terminal.setActive_port(String.join(",", terminalModel.getPort_numberS()));
                        }
                        if(terminalModel.getOs_familyS().size() > 0){
                            terminal.setOs(String.join(",", terminalModel.getOs_familyS()));
                        }
                        if(terminalModel.getPort_service_productS().size() > 0){
                            terminal.setService(String.join(",", terminalModel.getPort_service_productS()));
                        }
                        terminalList.add(terminal);
                    }
                }
            }
        }

        terminalService.batchInsertGather(terminalList);

        // 写入终端表
        terminalService.copyGatherData();

        log.info("terminal End......" + (System.currentTimeMillis() - time));
    }
}
