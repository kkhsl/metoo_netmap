package com.metoo.sqlite.gather.strategy.other;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.Ipv4;
import com.metoo.sqlite.entity.PanaSwitch;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.service.IPanaSwitchService;
import com.metoo.sqlite.service.Ipv4Service;
import com.metoo.sqlite.utils.muyun.MuyunService;
import com.metoo.sqlite.utils.net.Ipv4Utils;
import com.metoo.sqlite.utils.panabit.PanabitSendMsgUtils.PanabitService;
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
public class Ipv4MuyunCollectionStrategy implements DataCollectionStrategy {

    private final Ipv4Service ipv4Service;
    private final MuyunService muyunService;

    @Autowired
    public Ipv4MuyunCollectionStrategy(Ipv4Service ipv4Service, MuyunService muyunService) {
        this.ipv4Service = ipv4Service;
        this.muyunService = muyunService;
    }

    @Override
    public void collectData(Context context) {
        try {
            Device device = (Device) context.getEntity();
            if (device != null) {
                String result = muyunService.getArp(device.getIp(), Integer.parseInt(device.getLoginPort()),
                        device.getLoginName(), device.getLoginPassword().replaceAll("^\"|\"$", ""));
                if (StringUtil.isNotEmpty(result)) {
                    try {
                        JSONArray data = JSONArray.parseArray(result);
                        if(data.size() > 0){
                            List<Ipv4> ipv4List = new ArrayList<>();
                            for (Object o : data) {
                                try {
                                    JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(o));
                                    if(Ipv4Utils.verifyIp(json.getString("ip"))){
                                        Ipv4 ipv4 = new Ipv4();
                                        ipv4.setCreateTime(context.getCreateTime());
                                        ipv4.setIp(json.getString("ip"));
                                        ipv4.setPort(json.getString("netif"));
                                        ipv4.setMac(json.getString("mac"));
                                        ipv4List.add(ipv4);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            log.info("数据打印: {}", ipv4List);
                            this.ipv4Service.batchInsertGather(ipv4List);
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
