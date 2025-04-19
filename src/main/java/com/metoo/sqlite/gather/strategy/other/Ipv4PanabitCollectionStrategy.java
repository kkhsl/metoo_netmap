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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                                    try {
                                        JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(o));
                                        Ipv4 ipv4 = new Ipv4();
                                        ipv4.setCreateTime(context.getCreateTime());
                                        if(device.isState()){
//                                            if(json.getString("mac") != null && json.getString("mac").contains("-")){
//                                                ipv4.setMac(json.getString("mac").replace("-", ":"));
//                                            } if(json.getString("mac") != null && json.getString("mac").contains(":")){
//                                                ipv4.setMac(json.getString("mac"));
//                                            }else{
//                                                ipv4.setMac(json.getString("mac"));
//                                            }
                                            String mac = parseMacAddress(json.getString("mac"));
                                            ipv4.setMac(mac);
                                        }else{
                                            ipv4.setMac(null);
                                        }

                                        ipv4.setIp(json.getString("ipaddr"));
                                        ipv4List.add(ipv4);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
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

    public static String parseMacAddress(String input) {
        // 正则表达式匹配MAC地址
        String regex = "(?:[0-9a-fA-F]{2}[:\\-]){5}[0-9a-fA-F]{2}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String macAddress = matcher.group();
            // 如果格式是"-"分隔的，转换为":"分隔的格式
            return macAddress.replace('-', ':');
        }
        return null; // 如果没有匹配到MAC地址
    }

    public static void main(String[] args) {
        // 测试不同的格式
        String[] testInputs = {
                "c0:b8:e6:93:39:50",
                "00-f1-f3-b2-d1-85",
                "c0-b8-e6-93-39-50/RuijieNetwor_93-39-50",
                "c0:b8:e6:93:39:50/RuijieNetwor_93-39-50"
        };

        for (String input : testInputs) {
            String mac = parseMacAddress(input);
            System.out.println("Parsed MAC Address: " + mac);
        }
    }
}
