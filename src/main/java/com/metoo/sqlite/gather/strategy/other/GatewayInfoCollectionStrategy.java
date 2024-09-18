package com.metoo.sqlite.gather.strategy.other;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.GatewayInfo;
import com.metoo.sqlite.entity.Ipv4;
import com.metoo.sqlite.gather.common.PyCommand;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.IGatewayInfoService;
import com.metoo.sqlite.service.Ipv4Service;
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
public class GatewayInfoCollectionStrategy implements DataCollectionStrategy {

    private final IGatewayInfoService gatewayInfoService;
    private final PyExecUtils pyExecUtils;

    @Autowired
    public GatewayInfoCollectionStrategy(IGatewayInfoService gatewayInfoService, PyExecUtils pyExecUtils) {
        this.gatewayInfoService = gatewayInfoService;
        this.pyExecUtils = pyExecUtils;
}

    @Override
    public void collectData(Context context) {
        try {
            Device device = (Device) context.getEntity();
            if (device != null) {

                // python3 192.168.5.1
//                PyCommand pyCommand = (PyCommand) ApplicationContextUtils.getBean("pyCommand");
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
                        device.getLoginPassword(), Global.PY_SUFFIX_GET_FIREWALL});
                String result = this.pyExecUtils.exec(pyCommand);
                if (StringUtil.isNotEmpty(result)) {
                    try {
                        List<GatewayInfo> gatewayInfoList = JSONObject.parseArray(result, GatewayInfo.class);
                        if(gatewayInfoList.size() > 0){
                            gatewayInfoList.forEach(e -> {
                                e.setCreateTime(context.getCreateTime());
                                e.setDeviceName(device.getName());
                            });
                            this.gatewayInfoService.batchInsertGather(gatewayInfoList);
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
