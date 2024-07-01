package com.metoo.sqlite.gather.strategy.other;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.PortIpv4;
import com.metoo.sqlite.entity.PortIpv6;
import com.metoo.sqlite.entity.Subnet;
import com.metoo.sqlite.gather.common.PyCommand;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.IPortIpv4Service;
import com.metoo.sqlite.service.IPortIpv6Service;
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
public class PingCollectionStrategy implements DataCollectionStrategy {

    private final PyExecUtils pyExecUtils;

    @Autowired
    public PingCollectionStrategy(PyExecUtils pyExecUtils) {
        this.pyExecUtils = pyExecUtils;
    }

    @Override
    public void collectData(Context context) {
        try {
            Subnet subnet = (Subnet) context.getEntity();
            if (subnet != null) {
                PyCommand pyCommand = (PyCommand) ApplicationContextUtils.getBean("pyCommand");
                pyCommand.setName("ping.py");
                pyCommand.setParams(new String[]{
                        subnet.getIp(), String.valueOf(subnet.getMask())});
                String result = this.pyExecUtils.exec(pyCommand);
                if (StringUtil.isNotEmpty(result)) {}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
