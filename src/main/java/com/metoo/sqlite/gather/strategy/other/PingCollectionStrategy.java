package com.metoo.sqlite.gather.strategy.other;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.*;
import com.metoo.sqlite.gather.common.PyCommand;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.IPortIpv4Service;
import com.metoo.sqlite.service.IPortIpv6Service;
import com.metoo.sqlite.utils.Global;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
//                /opt/sqlite/cf-scanner/cf-scanner -i 192.168.5.0/24  -ns=true -np=false -m ping
//                PyCommand pyCommand = (PyCommand) ApplicationContextUtils.getBean("pyCommand");
                PyCommandBuilder pyCommand = new PyCommandBuilder();
                pyCommand.setPrefix("");
                pyCommand.setVersion("");
                pyCommand.setPath(Global.cf_scanner);
                pyCommand.setName(Global.cf_scanner_name);
                pyCommand.setParams(new String[]{
                        "-i",
                        subnet.getIp()+"/"+String.valueOf(subnet.getMask()),
                        "-ns=true",
                        "-np=false",
                        "-m",
                        "ping"
                });

                log.info("ping result start" + subnet.getIp()+"/"+String.valueOf(subnet.getMask()));

                String result = this.pyExecUtils.exec(pyCommand);

                log.info("ping result end" + result);

                if (StringUtil.isNotEmpty(result)) {}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
