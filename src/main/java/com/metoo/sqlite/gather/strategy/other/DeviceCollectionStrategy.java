package com.metoo.sqlite.gather.strategy.other;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.Ipv4;
import com.metoo.sqlite.gather.common.PyCommand;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.IDeviceService;
import com.metoo.sqlite.service.Ipv4Service;
import com.metoo.sqlite.utils.Global;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-23 14:49
 * <p>
 * ipv4 port 采集
 */
@Slf4j
@Component
public class DeviceCollectionStrategy implements DataCollectionStrategy {

    private final PyExecUtils pyExecUtils;
    private final IDeviceService deviceService;

    @Autowired
    public DeviceCollectionStrategy(PyExecUtils pyExecUtils, IDeviceService deviceService) {
        this.pyExecUtils = pyExecUtils;
        this.deviceService = deviceService;
    }

    public static void main(String[] args) {
        String a = "{\n" +
                "  \"model\": \"H3C S5500-58C-HI-D\",\n" +
                "  \"version\": \"5.20\",\n" +
                "  \"ipv6_keyword\": 33,\n" +
                "  \"ipv6_address\": 5,\n" +
                "  \"sentlocally\": \"2765325\",\n" +
                "  \"neighboradverts\": \"153747\"\n" +
                "}";
        Device obj = JSONObject.parseObject(a, Device.class);
        System.out.println(obj);
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
                        device.getLoginPassword(), Global.PY_SUFFIX_GET_SWITCH});
                String result = this.pyExecUtils.exec(pyCommand);
                if (StringUtil.isNotEmpty(result)) {
                    try {
                        log.info("Device: " + result + "=========================");
                        Device obj = JSONObject.parseObject(result, Device.class);
                        if(obj != null){
                            device.setModel(obj.getModel());
                            device.setVersion(obj.getVersion());
                            device.setIpv6_keyword(obj.getIpv6_keyword());
                            device.setIpv6Addrcount(obj.getIpv6_address());
                            device.setSentlocally(obj.getSentlocally());
                            device.setNeighboradverts(obj.getNeighboradverts());
                            device.setIpv6Forward("false");
                            if(!"".equals(obj.getSentlocally()) || !"".equals(obj.getNeighboradverts())){
                                device.setIpv6Forward("true");
                            }
                            deviceService.update(device);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    device.setModel(null);
                    device.setVersion(null);
                    device.setIpv6_keyword(null);
                    device.setIpv6Addrcount(null);
                    device.setSentlocally(null);
                    device.setNeighboradverts(null);
                    device.setIpv6Forward(null);
                    deviceService.update(device);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
