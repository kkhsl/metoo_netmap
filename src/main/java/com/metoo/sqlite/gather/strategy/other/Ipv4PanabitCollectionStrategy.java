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
                                PanaSwitch panaSwitch = panaSwitchService.selectObjByOne();
                                for (Object o : array) {
                                    JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(o));
                                    Ipv4 ipv4 = new Ipv4();
                                    ipv4.setCreateTime(context.getCreateTime());
                                    if(panaSwitch != null && panaSwitch.getState() == true){
                                        ipv4.setMac(json.getString("mac").replace("-", ":"));
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
        String data = "{\"data\":[{\"id\":\"1\",\"ipaddr\":\"192.168.1.116\",\"onlinesecs\":\"0/09:38:15\",\"ttl\":\"599\",\"flowcnt\":\"27\",\"mac\":\"f8-6f-b0-fe-d7-85\",\"in\":\"5.18G\",\"out\":\"1.04G\",\"inbps\":\"105.89K\",\"outbps\":\"6.95K\",\"account\":\"0\",\"iecookies\":\"0\",\"chromecookies\":\"0\",\"natip\":\"0\",\"mstcnt\":\"0\",\"ratein\":\"0\",\"rateout\":\"0\",\"name\":\"none\",\"ippxy\":\"none\",\"blackip\":\"0\"},{\"id\":\"2\",\"ipaddr\":\"192.168.1.8\",\"onlinesecs\":\"11/22:10:51\",\"ttl\":\"599\",\"flowcnt\":\"8\",\"mac\":\"b0-51-8e-09-17-61\",\"in\":\"1.23G\",\"out\":\"2.28G\",\"inbps\":\"6.39K\",\"outbps\":\"8.60K\",\"account\":\"0\",\"iecookies\":\"0\",\"chromecookies\":\"0\",\"natip\":\"0\",\"mstcnt\":\"0\",\"ratein\":\"0\",\"rateout\":\"0\",\"name\":\"none\",\"ippxy\":\"none\",\"blackip\":\"0\"},{\"id\":\"3\",\"ipaddr\":\"192.168.1.112\",\"onlinesecs\":\"0/09:30:26\",\"ttl\":\"495\",\"flowcnt\":\"1\",\"mac\":\"14-d8-64-f7-01-f6\",\"in\":\"1.35G\",\"out\":\"83.15M\",\"inbps\":\"0\",\"outbps\":\"0\",\"account\":\"0\",\"iecookies\":\"0\",\"chromecookies\":\"0\",\"natip\":\"0\",\"mstcnt\":\"0\",\"ratein\":\"0\",\"rateout\":\"0\",\"name\":\"none\",\"ippxy\":\"none\",\"blackip\":\"0\"},{\"id\":\"4\",\"ipaddr\":\"192.168.1.119\",\"onlinesecs\":\"4/09:16:37\",\"ttl\":\"599\",\"flowcnt\":\"14\",\"mac\":\"c0-3f-d5-ed-2f-56\",\"in\":\"547.32M\",\"out\":\"193.25M\",\"inbps\":\"2.02K\",\"outbps\":\"1.74K\",\"account\":\"0\",\"iecookies\":\"0\",\"chromecookies\":\"0\",\"natip\":\"0\",\"mstcnt\":\"0\",\"ratein\":\"0\",\"rateout\":\"0\",\"name\":\"none\",\"ippxy\":\"none\",\"blackip\":\"0\"},{\"id\":\"5\",\"ipaddr\":\"192.168.0.200\",\"onlinesecs\":\"14/03:04:58\",\"ttl\":\"371\",\"flowcnt\":\"0\",\"mac\":\"94-09-d3-20-78-b2\",\"in\":\"73.13M\",\"out\":\"185.91M\",\"inbps\":\"0\",\"outbps\":\"0\",\"account\":\"0\",\"iecookies\":\"0\",\"chromecookies\":\"0\",\"natip\":\"0\",\"mstcnt\":\"0\",\"ratein\":\"0\",\"rateout\":\"0\",\"name\":\"none\",\"ippxy\":\"none\",\"blackip\":\"0\"},{\"id\":\"6\",\"ipaddr\":\"192.168.1.102\",\"onlinesecs\":\"0/00:24:13\",\"ttl\":\"491\",\"flowcnt\":\"3\",\"mac\":\"68-dd-b7-3e-ac-63\",\"in\":\"7.91M\",\"out\":\"925.86K\",\"inbps\":\"0\",\"outbps\":\"0\",\"account\":\"0\",\"iecookies\":\"0\",\"chromecookies\":\"0\",\"natip\":\"0\",\"mstcnt\":\"0\",\"ratein\":\"0\",\"rateout\":\"0\",\"name\":\"dhcp/TL-WDR5620鏄撳睍鐗?,\"ippxy\":\"none\",\"blackip\":\"0\"},{\"id\":\"7\",\"ipaddr\":\"192.168.1.201\",\"onlinesecs\":\"11/20:51:26\",\"ttl\":\"531\",\"flowcnt\":\"0\",\"mac\":\"b6-8a-31-85-62-f8\",\"in\":\"2.31M\",\"out\":\"1.40M\",\"inbps\":\"0\",\"outbps\":\"0\",\"account\":\"0\",\"iecookies\":\"0\",\"chromecookies\":\"0\",\"natip\":\"0\",\"mstcnt\":\"0\",\"ratein\":\"0\",\"rateout\":\"0\",\"name\":\"none\",\"ippxy\":\"none\",\"blackip\":\"0\"},{\"id\":\"8\",\"ipaddr\":\"192.168.1.4\",\"onlinesecs\":\"11/21:39:30\",\"ttl\":\"575\",\"flowcnt\":\"0\",\"mac\":\"64-db-8b-e1-f3-eb\",\"in\":\"0\",\"out\":\"3.63M\",\"inbps\":\"0\",\"outbps\":\"0\",\"account\":\"0\",\"iecookies\":\"0\",\"chromecookies\":\"0\",\"natip\":\"0\",\"mstcnt\":\"0\",\"ratein\":\"0\",\"rateout\":\"0\",\"name\":\"none\",\"ippxy\":\"none\",\"blackip\":\"0\"},{\"id\":\"9\",\"ipaddr\":\"192.168.1.123\",\"onlinesecs\":\"0/03:18:33\",\"ttl\":\"491\",\"flowcnt\":\"5\",\"mac\":\"00-25-ab-49-3c-c0\",\"in\":\"251.81K\",\"out\":\"222.19K\",\"inbps\":\"0\",\"outbps\":\"0\",\"account\":\"0\",\"iecookies\":\"0\",\"chromecookies\":\"0\",\"natip\":\"0\",\"mstcnt\":\"0\",\"ratein\":\"0\",\"rateout\":\"0\",\"name\":\"none\",\"ippxy\":\"none\",\"blackip\":\"0\"},{\"id\":\"10\",\"ipaddr\":\"0.0.0.0\",\"onlinesecs\":\"0\",\"ttl\":\"0\",\"flowcnt\":\"58\",\"mac\":\"00:00:00:00:00:00\",\"in\":\"8.37G\",\"out\":\"3.78G\",\"inbps\":\"114.30K\",\"outbps\":\"17.30K\",\"account\":\"0\",\"iecookies\":\"0\",\"chromecookies\":\"0\",\"natip\":\"0\",\"mstcnt\":\"0\",\"ratein\":\"\",\"rateout\":\"\",\"name\":\"\",\"ippxy\":\"\",\"blackip\":\"\"}],\"total\":\"10\"}}";
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
