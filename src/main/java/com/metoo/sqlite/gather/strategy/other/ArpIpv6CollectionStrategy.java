package com.metoo.sqlite.gather.strategy.other;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.Arp;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.IArpService;
import com.metoo.sqlite.utils.Global;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-23 14:49
 * <p>
 * ipv4 port 采集
 */
@Slf4j
@Component
public class ArpIpv6CollectionStrategy implements DataCollectionStrategy {

    private final IArpService arpService;
    private final PyExecUtils pyExecUtils;

    @Autowired
    public ArpIpv6CollectionStrategy(IArpService arpService, PyExecUtils pyExecUtils) {
        this.arpService = arpService;
        this.pyExecUtils = pyExecUtils;
    }

    @Override
    public void collectData(Context context) {
        try {
            Device device = (Device) context.getEntity();
            if (device != null) {
                PyCommandBuilder pyCommand = new PyCommandBuilder();
                pyCommand.setVersion(Global.py_name);
                pyCommand.setPy_prefix("-W ignore");
                pyCommand.setPath(Global.PYPATH);
                pyCommand.setName("main.pyc");
                pyCommand.setParams(new String[]{
                        device.getDeviceVendorAlias(),
                        device.getDeviceTypeAlias(),
                        device.getIp(),
                        device.getLoginType(),
                        device.getLoginPort(),
                        device.getLoginName(),
                        device.getLoginPassword(), Global.IPV6_ARP});

                String result = this.pyExecUtils.exec(pyCommand);

                if (StringUtil.isNotEmpty(result)) {
                    try {
                        List<Arp> arps = JSONObject.parseArray(result, Arp.class);
                        if (arps.size() > 0) {
                            if(arps.size() > 0){
                                arps.forEach(e -> {
                                    e.setDeviceUuid(device.getUuid());
                                    e.setCreateTime(context.getCreateTime());
                                });
                                this.arpService.batchInsert(arps);
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
