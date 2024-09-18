package com.metoo.sqlite.gather.strategy.other;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.Ipv4;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
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
public class Ipv4CollectionStrategyTest implements DataCollectionStrategy {

    private final Ipv4Service ipv4Service;
    private final PyExecUtils pyExecUtils;

    @Autowired
    public Ipv4CollectionStrategyTest(Ipv4Service ipv4Service, PyExecUtils pyExecUtils) {
        this.ipv4Service = ipv4Service;
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
                pyCommand.setPath("D:\\metoo\\install\\install发布版(2)\\netmap\\script");
                pyCommand.setName("main.py");
                pyCommand.setParams(new String[]{
                        device.getDeviceVendorAlias(),
                        device.getDeviceTypeAlias(),
                        device.getIp(),
                        device.getLoginType(),
                        device.getLoginPort(),
                        device.getLoginName(),
                        device.getLoginPassword(), Global.PY_SUFFIX_IPV4});

                String result = this.pyExecUtils.exec(pyCommand);

                log.info("Ipv4 script data ================ " + result);

                if (StringUtil.isNotEmpty(result)) {
                    try {
                        List<Ipv4> ipv4List = JSONObject.parseArray(result, Ipv4.class);
                        if (ipv4List.size() > 0) {
                            if(ipv4List.size() > 0){
                                ipv4List.forEach(e -> {
                                    e.setDeviceUuid(device.getUuid());
                                    e.setCreateTime(context.getCreateTime());
                                });
                                this.ipv4Service.batchInsertGather(ipv4List);
//                                for (Ipv4 ipv4 : ipv4List) {
//                                    log.info("========================== insert" + device.getIp());
//                                    ipv4Service.insert(ipv4);
//                                }
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
