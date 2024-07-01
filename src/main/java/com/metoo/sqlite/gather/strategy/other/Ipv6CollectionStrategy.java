package com.metoo.sqlite.gather.strategy.other;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.Ipv4;
import com.metoo.sqlite.entity.Ipv6;
import com.metoo.sqlite.gather.common.PyCommand;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.Ipv4Service;
import com.metoo.sqlite.service.Ipv6Service;
import com.metoo.sqlite.utils.Global;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-23 14:49
 * <p>
 * ipv4 port 采集
 */
@Component
public class Ipv6CollectionStrategy implements DataCollectionStrategy {

    private final Ipv6Service ipv6Service;
    private final PyExecUtils pyExecUtils;

    @Autowired
    public Ipv6CollectionStrategy(Ipv6Service ipv6Service, PyExecUtils pyExecUtils) {
        this.ipv6Service = ipv6Service;
        this.pyExecUtils = pyExecUtils;
    }

    @Override
    public void collectData(Context context) {
        try {
            Device device = (Device) context.getEntity();
            if (device != null) {
                PyCommand pyCommand = (PyCommand) ApplicationContextUtils.getBean("pyCommand");
                pyCommand.setName("main.py");
                pyCommand.setParams(new String[]{
                        device.getDeviceVendorAlias(),
                        device.getDeviceTypeAlias(),
                        device.getIp(),
                        device.getLoginType(),
                        device.getLoginPort(),
                        device.getLoginName(),
                        device.getLoginPassword(), Global.PY_SUFFIX_IPV6_NEIGHBORS});
                String result = this.pyExecUtils.exec(pyCommand);
                if (StringUtil.isNotEmpty(result)) {
                    List<Ipv6> ipv6List = JSONObject.parseArray(result, Ipv6.class);
                    if (ipv6List.size() > 0) {

                        List<Ipv6> filteredObjects = ipv6List.stream()
                                .filter(obj -> !obj.getIpv6_mac().startsWith("FE80"))
                                .collect(Collectors.toList());

                        if(filteredObjects.size() > 0){
                            filteredObjects.forEach(e -> {
                                e.setDeviceUuid(device.getUuid());
                                e.setCreateTime(context.getCreateTime());
                            });
                            this.ipv6Service.batchInsertGather(filteredObjects);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
