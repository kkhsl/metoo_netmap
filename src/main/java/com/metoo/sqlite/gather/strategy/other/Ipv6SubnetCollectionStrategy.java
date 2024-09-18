package com.metoo.sqlite.gather.strategy.other;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.Ipv4;
import com.metoo.sqlite.entity.SubnetIpv6;
import com.metoo.sqlite.gather.common.PyCommand;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.ISubnetIpv6Service;
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
public class Ipv6SubnetCollectionStrategy implements DataCollectionStrategy {

    private final PyExecUtils pyExecUtils;
    private final ISubnetIpv6Service subnetIpv6Service;

    @Autowired
    public Ipv6SubnetCollectionStrategy(PyExecUtils pyExecUtils, ISubnetIpv6Service subnetIpv6Service) {
        this.pyExecUtils = pyExecUtils;
        this.subnetIpv6Service = subnetIpv6Service;
    }

    @Override
    public void collectData(Context context) {
        try {
//                PyCommand pyCommand = (PyCommand) ApplicationContextUtils.getBean("pyCommand");
            PyCommandBuilder pyCommand = new PyCommandBuilder();
            pyCommand.setVersion(Global.py_name);
            pyCommand.setPy_prefix("-W ignore");
            pyCommand.setPath(Global.PYPATH);
            pyCommand.setName("subnetipv6.pyc");
            try {
                String result = this.pyExecUtils.exec(pyCommand);
                if (StringUtil.isNotEmpty(result)) {
                   // 递归ipv6网段数据
                    if(!"".equals(result)){
                        JSONObject obj = JSONObject.parseObject(result);
                        if(obj != null){
                            generic(obj, null);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generic(JSONObject obj, Integer parentId){
        if(obj != null){
            for (String key : obj.keySet()) {
                if(obj.get(key) != null){
                    SubnetIpv6 subnetIpv6 = new SubnetIpv6();
                    subnetIpv6.setIp(key.split("/")[0]);
                    subnetIpv6.setMask(Integer.parseInt(key.split("/")[1]));
                    subnetIpv6.setParentId(parentId);

                    this.subnetIpv6Service.save(subnetIpv6);

                    JSONArray childs = JSONObject.parseArray(obj.getString(key));
                    if(childs.size() > 0){
                        for (Object ele : childs) {
                            if(ele instanceof String){
                                SubnetIpv6 child = new SubnetIpv6();
                                child.setIp(String.valueOf(ele).split("/")[0]);
                                child.setMask(Integer.parseInt(String.valueOf(ele).split("/")[1]));
                                child.setParentId(subnetIpv6.getId());

                                this.subnetIpv6Service.save(child);

                            } if(ele instanceof JSONObject){
                                JSONObject child = JSONObject.parseObject(JSONObject.toJSONString(ele));
                                generic(child, subnetIpv6.getId());
                            } else {}
                        }
                    }
                }
            }
        }
    }
}
