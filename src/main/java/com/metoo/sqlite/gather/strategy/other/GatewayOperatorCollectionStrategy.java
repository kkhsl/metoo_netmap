package com.metoo.sqlite.gather.strategy.other;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.GatewayInfo;
import com.metoo.sqlite.gather.common.PyCommand;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.IGatewayInfoService;
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
public class GatewayOperatorCollectionStrategy implements DataCollectionStrategy {

    private final IGatewayInfoService gatewayInfoService;
    private final PyExecUtils pyExecUtils;

    @Autowired
    public GatewayOperatorCollectionStrategy(IGatewayInfoService gatewayInfoService, PyExecUtils pyExecUtils) {
        this.gatewayInfoService = gatewayInfoService;
        this.pyExecUtils = pyExecUtils;
    }

    @Override
    public void collectData(Context context) {
        try {
            GatewayInfo gatewayInfo = (GatewayInfo) context.getEntity();
            if (gatewayInfo != null) {
//                PyCommand pyCommand = (PyCommand) ApplicationContextUtils.getBean("pyCommand");
                PyCommandBuilder pyCommand = new PyCommandBuilder();
                pyCommand.setVersion(Global.py_name);
                pyCommand.setPath(Global.PYPATH);
                pyCommand.setName("ip2operate.pyc");
                pyCommand.setParams(new String[]{
                        gatewayInfo.getIp_address()
                });


                // 根据ipv4和ipv6查询对应的运营商
                // ipv4/ipv6都不为空，只需要查询ipv4运营商
                if(StringUtil.isNotEmpty(gatewayInfo.getIp_address()) && StringUtil.isNotEmpty(gatewayInfo.getIpv6_address())){
                    pyCommand.setParams(new String[]{
                            gatewayInfo.getIp_address()
                    });
                    String result = this.pyExecUtils.exec(pyCommand);

                    gatewayInfo.setOperator(result);
                }else {
                    if(StringUtil.isNotEmpty(gatewayInfo.getIp_address())){ // ipv4不为空 script/ip2operator.py 192.168.5.1
                        pyCommand.setParams(new String[]{
                                gatewayInfo.getIp_address()
                        });
                        String result = this.pyExecUtils.exec(pyCommand);

                        gatewayInfo.setOperator(result);
                    }
                    if(StringUtil.isNotEmpty(gatewayInfo.getIpv6_address())){ // ipv6不为空  script/ip2operator.py 240c::6666
                        pyCommand.setParams(new String[]{
                                gatewayInfo.getIpv6_address()
                        });
                        String result = this.pyExecUtils.exec(pyCommand);

                        gatewayInfo.setOperator(result);
                    }
                }

                this.gatewayInfoService.update(gatewayInfo);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
