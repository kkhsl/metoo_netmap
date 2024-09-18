package com.metoo.sqlite.manager.utils.jx;

import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.Arp;
import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.entity.Terminal;
import com.metoo.sqlite.entity.Unsure;
import com.metoo.sqlite.model.TerminalModel;
import com.metoo.sqlite.service.IArpService;
import com.metoo.sqlite.service.IProbeService;
import com.metoo.sqlite.service.IUnsureService;
import com.metoo.sqlite.utils.StringUtils;
import com.metoo.sqlite.utils.date.DateTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataUtils {

    @Autowired
    private IUnsureService unsureService;
    @Autowired
    private IArpService arpService;

    public List<Terminal> getUnsureInfo() {
        Map<String, TerminalModel> resultMap = new HashMap<>();

        Map params = new HashMap();

        params.clear();

        List<Unsure> unsures = unsureService.selectObjByMap(params);

        if (unsures.size() > 0) {
            // 组合数据，更新终端采集表
            for (Unsure unsure : unsures) {

                boolean flag = false;

                String ip = unsure.getIp_addr();
                String port_number = unsure.getPort_num();
                String os_family = unsure.getOs_family();
                String port_service_product = unsure.getPort_service_product();
                String port_service_vendor = unsure.getPort_service_vendor();
                String vendor = unsure.getVendor();
                String os_gen = unsure.getOs_gen();
                Float reliability = unsure.getReliability();
                Integer ttl = unsure.getTtl();
                if (resultMap.containsKey(ip)) {
                    TerminalModel terminalModel = resultMap.get(ip);
                    if (StringUtil.isNotEmpty(port_number) && !terminalModel.containsPort_number(port_number)) {
                        terminalModel.addPort_number(port_number);
                    }
                    if (StringUtil.isNotEmpty(os_family) && !terminalModel.containsOs_family(os_family)) {
                        terminalModel.addOs_family(os_family);
                    }
                    if (StringUtil.isNotEmpty(port_service_product) && !terminalModel.containsPort_service_product(port_service_product)) {
                        terminalModel.addPort_service_product(port_service_product);
                    }
                    if (reliability != null) {
                        terminalModel.setReliability(reliability);
                    }
                    if (StringUtil.isNotEmpty(vendor)) {
                        terminalModel.setVendor(vendor);
                    }
                    if (StringUtil.isNotEmpty(os_gen)) {
                        terminalModel.setOs_gen(os_gen);
                    }
                } else {
                    TerminalModel terminalModel = new TerminalModel(ip, port_number, os_family,
                            port_service_product, "", reliability, ttl, os_gen, vendor, flag);
                    resultMap.put(ip, terminalModel);
                }
            }
        }
        List<Terminal> terminals = new ArrayList<>();
        if (resultMap != null && !resultMap.isEmpty()) {
            for (String key : resultMap.keySet()) {

                TerminalModel terminalModel = resultMap.get(key);
                if (terminalModel != null) {

                    Terminal terminal = new Terminal();
                    terminal.setCreateTime(DateTools.getCreateTime());
                    if (terminalModel.getPort_numberS().size() > 0) {
                        terminal.setActive_port(String.join(",", terminalModel.getPort_numberS()));
                    }

                    if (terminalModel.getPort_service_productS().size() > 0) {
                        terminal.setService(String.join(",", terminalModel.getPort_service_productS()));
                    }
//                    if(StringUtils.isNotEmpty(terminalModel.getOs_gen()) || StringUtils.isNotEmpty(terminalModel.getVendor())){
//                        if(StringUtil.isNotEmpty(terminalModel.getVendor())){
//                            terminal.setOs(terminalModel.getVendor() + " " + terminalModel.getOs_gen());
//                        }else{
//                            terminal.setOs(terminalModel.getOs_gen());
//                        }
//
//                    }
                    if (terminalModel.getReliability() != null) {
                        if (terminalModel.getReliability() > 0.9
                                && StringUtils.isEmpty(terminalModel.getOs_gen())) {

                            if (StringUtil.isNotEmpty(terminalModel.getVendor())) {
                                String os = terminalModel.getVendor() + " " + terminalModel.getOs_gen();
                                terminal.setOs(os.trim());
                            } else {
                                terminal.setOs(terminalModel.getOs_gen());
                            }
                        }
                    }

                    if (terminal.getId() == null) {
                        params.clear();
                        params.put("ip", key);
                        List<Arp> arps = arpService.selectObjByMap(params);
                        if (arps.size() > 0) {
                            Arp arp = arps.get(0);
                            terminal.setIpv4addr(key);
                            terminal.setIpv6addr(arp.getIpv6());
                            terminal.setMac(arp.getMac());
                            terminal.setMacvendor(arp.getMacVendor());
                        }
                        terminals.add(terminal);
                    }
                }
            }
        }
        return terminals;
    }

}
