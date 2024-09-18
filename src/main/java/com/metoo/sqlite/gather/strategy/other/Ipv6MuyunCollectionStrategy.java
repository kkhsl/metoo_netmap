package com.metoo.sqlite.gather.strategy.other;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.Ipv6;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.service.Ipv6Service;
import com.metoo.sqlite.utils.muyun.MuyunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
public class Ipv6MuyunCollectionStrategy implements DataCollectionStrategy {

    private final Ipv6Service ipv6Service;
    private final MuyunService muyunService;

    @Autowired
    public Ipv6MuyunCollectionStrategy(Ipv6Service ipv6Service, MuyunService muyunService) {
        this.ipv6Service = ipv6Service;
        this.muyunService = muyunService;
    }

    @Override
    public void collectData(Context context) {
            Device device = (Device) context.getEntity();
            if (device != null) {
                String result = muyunService.getIpv6_neighbor(device.getIp(), Integer.parseInt(device.getLoginPort()),
                        device.getLoginName(), device.getLoginPassword().replaceAll("^\"|\"$", ""));

                if (StringUtil.isNotEmpty(result)) {
                    try {
                        JSONArray data = JSONArray.parseArray(result);
                        if (data.size() > 0) {
                            List<Ipv6> ipv6List = new ArrayList<>();
                            for (Object o : data) {
                                JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(o));
                                Ipv6 ipv6 = new Ipv6();
                                ipv6.setCreateTime(context.getCreateTime());
                                if (json.getString("ip") != null) {
                                    if (json.getString("ip").toLowerCase().startsWith("FE80".toLowerCase())) {
                                        continue;
                                    }
                                    ipv6.setIpv6_address(json.getString("ip"));
                                }
                                ipv6.setIpv6_mac(json.getString("mac"));
                                ipv6.setPort(json.getString("netif"));
                                ipv6List.add(ipv6);

                            }
                            this.ipv6Service.batchInsertGather(ipv6List);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

    }
}
