package com.metoo.sqlite.gather.strategy.other;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.Ipv4;
import com.metoo.sqlite.entity.PanaSwitch;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.IPanaSwitchService;
import com.metoo.sqlite.service.Ipv4Service;
import com.metoo.sqlite.utils.Global;
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
public class Ipv4PanabitCollectionStrategy implements DataCollectionStrategy {

    private final Ipv4Service ipv4Service;
    private final PanabitService panabitService;
    private final IPanaSwitchService panaSwitchService;

    @Autowired
    public Ipv4PanabitCollectionStrategy(Ipv4Service ipv4Service, PanabitService panabitService, IPanaSwitchService panaSwitchService) {
        this.ipv4Service = ipv4Service;
        this.panabitService = panabitService;
        this.panaSwitchService = panaSwitchService;
    }

    @Override
    public void collectData(Context context) {
        try {
            Device device = (Device) context.getEntity();
            if (device != null) {
                String result = panabitService.load_ipobj_list("v4", device.getIp(), device.getLoginPort(),
                        device.getLoginName(), device.getLoginPassword().replaceAll("^\"|\"$", ""));

                if (StringUtil.isNotEmpty(result)) {
                    try {
                        JSONObject panabit = JSONObject.parseObject(result);
                        if(panabit.getString("data") != null){

                            JSONArray array = JSONArray.parseArray(panabit.getString("data"));
                            if(array.size() > 0){
                                List<Ipv4> ipv4List = new ArrayList<>();
                                for (Object o : array) {

                                    JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(o));
                                    Ipv4 ipv4 = new Ipv4();
                                    ipv4.setCreateTime(context.getCreateTime());
                                    if(device.isState()){
                                        if(json.getString("mac") != null && json.getString("mac").contains("-")){
                                            ipv4.setMac(json.getString("mac").replace("-", ":"));
                                        } if(json.getString("mac") != null && json.getString("mac").contains(":")){
                                            ipv4.setMac(json.getString("mac"));
                                        }
                                    }else{
                                        ipv4.setMac(null);
                                    }

                                    ipv4.setIp(json.getString("ipaddr"));
                                    ipv4List.add(ipv4);
                                }
                                this.ipv4Service.batchInsertGather(ipv4List);
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

    public static void main(String[] args) {
        String data = "[{\n" +
                "\t\"id\": \"2\",\n" +
                "\t\"ipaddr\": \"192.168.50.98\",\n" +
                "\t\"onlinesecs\": \"5/07:45:07\",\n" +
                "\t\"ttl\": \"597\",\n" +
                "\t\"flowcnt\": \"8\",\n" +
                "\t\"mac\": \"24-18-c6-92-33-82\",\n" +
                "\t\"in\": \"241.32M\",\n" +
                "\t\"out\": \"3.17G\",\n" +
                "\t\"inbps\": \"168\",\n" +
                "\t\"outbps\": \"47.06K\",\n" +
                "\t\"account\": \"0\",\n" +
                "\t\"iecookies\": \"0\",\n" +
                "\t\"chromecookies\": \"0\",\n" +
                "\t\"natip\": \"0\",\n" +
                "\t\"mstcnt\": \"0\",\n" +
                "\t\"ratein\": \"0\",\n" +
                "\t\"rateout\": \"0\",\n" +
                "\t\"name\": \"none\",\n" +
                "\t\"ippxy\": \"none\",\n" +
                "\t\"blackip\": \"0\"\n" +
                "}]";
        JSONArray array = JSONArray.parseArray(data);
        if(array.size() > 0){
            List<Ipv4> ipv4List = new ArrayList<>();
            for (Object o : array) {
                JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(o));
                System.out.println(json.getString("mac").replace("-", ":"));
            }
        }
    }
}
