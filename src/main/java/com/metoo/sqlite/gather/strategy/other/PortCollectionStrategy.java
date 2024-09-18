package com.metoo.sqlite.gather.strategy.other;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.PortIpv4;
import com.metoo.sqlite.entity.PortIpv6;
import com.metoo.sqlite.gather.common.PyCommand;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.IPortIpv4Service;
import com.metoo.sqlite.service.IPortIpv6Service;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.net.Ipv4Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-23 14:49
 * <p>
 * ipv4 port 采集
 */
@Component
public class PortCollectionStrategy implements DataCollectionStrategy {

    private final IPortIpv4Service portIpv4Service;
    private final IPortIpv6Service portIpv6Service;
    private final PyExecUtils pyExecUtils;


    @Autowired
    public PortCollectionStrategy(IPortIpv4Service portIpv4Service, IPortIpv6Service portIpv6Service,
                                  PyExecUtils pyExecUtils) {
        this.portIpv4Service = portIpv4Service;
        this.pyExecUtils = pyExecUtils;
        this.portIpv6Service = portIpv6Service;
    }

    @Override
    public void collectData(Context context) {
        try {
            Device device = (Device) context.getEntity();
            if (device != null) {

                PyCommandBuilder pyCommand = new PyCommandBuilder()
                        .version(Global.py_name)
                        .path(Global.PYPATH)
                        .name("main.pyc")
                        .py_prefix("-W ignore")
                        .addParams2(device.getDeviceVendorAlias())
                        .addParams2(device.getDeviceTypeAlias())
                        .addParams2(device.getIp())
                        .addParams2(device.getLoginType())
                        .addParams2(device.getLoginPort())
                        .addParams2(device.getLoginName())
                        .addParams2(device.getLoginPassword())
                        .addParams2( Global.PY_SUFFIX_ALIVEINT);
                String result = this.pyExecUtils.exec(pyCommand);

                if (StringUtil.isNotEmpty(result)) {
                    try {
                        List<PortIpv4> ports = JSONObject.parseArray(result, PortIpv4.class);
                        if (ports.size() > 0) {
                            // ipv4
                            List<PortIpv4> list = ports.stream().filter(port -> port.getPort() != null && !port.getPort().isEmpty()).collect(Collectors.toList());
                            if(list.size() > 0){
                                ports.forEach(e -> {
                                    e.setStatus(1);
                                    e.setDeviceUuid(device.getUuid());
                                    e.setCreateTime(context.getCreateTime());
                                    if(StringUtil.isNotEmpty(e.getMask())){
                                        if(!e.getMask().contains(".")){
                                            e.setMask(Ipv4Utils.bitMaskConvertMask(Integer.parseInt(e.getMask())));
                                        }
                                    }
                                });
                                this.portIpv4Service.batchInsertGather(ports);
                            }
                            // ipv6
                            List<PortIpv6> ipv6Ports = JSONObject.parseArray(result, PortIpv6.class);
                            List<PortIpv6> ipv6s = ipv6Ports.stream().filter(port -> port.getPort() != null && !port.getPort().isEmpty()
                                                    && port.getIpv6_address() != null && !port.getIpv6_address().isEmpty()).collect(Collectors.toList());
                            if(ipv6s.size() > 0){
                                ipv6Ports.forEach(e -> {
                                    e.setStatus(1);
                                    e.setDeviceUuid(device.getUuid());
                                    e.setCreateTime(context.getCreateTime());
                                });
                                this.portIpv6Service.batchInsertGather(ipv6s);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
