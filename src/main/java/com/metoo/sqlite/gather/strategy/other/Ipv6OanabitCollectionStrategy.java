package com.metoo.sqlite.gather.strategy.other;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.Ipv6;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.Ipv6Service;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.panabit.PanabitSendMsgUtils.PanabitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-23 14:49
 * <p>
 * ipv4 port 采集
 */
@Slf4j
@Component
public class Ipv6OanabitCollectionStrategy implements DataCollectionStrategy {

    private final Ipv6Service ipv6Service;
    private final PanabitService panabitService;

    @Autowired
    public Ipv6OanabitCollectionStrategy(Ipv6Service ipv6Service, PanabitService panabitService) {
        this.ipv6Service = ipv6Service;
        this.panabitService = panabitService;
    }

    @Override
    public void collectData(Context context) {
        try {
            Device device = (Device) context.getEntity();
            if (device != null) {
                String result = panabitService.load_ipobj_list("v6", device.getIp(), device.getLoginPort(),
                        device.getLoginName(), device.getLoginPassword().replaceAll("^\"|\"$", ""));
                if (StringUtil.isNotEmpty(result)) {
                    try {
                        JSONObject panabit = JSONObject.parseObject(result);
                        if(panabit.getString("data") != null){
                            JSONArray array = JSONArray.parseArray(panabit.getString("data"));
                            if(array.size() > 0){
                                List<Ipv6> ipv6List = new ArrayList<>();
                                for (Object o : array) {
                                    JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(o));
                                    Ipv6 ipv6 = new Ipv6();
                                    if(json.getString("ipaddr") != null){
                                        if(json.getString("ipaddr").toLowerCase().startsWith("FE80".toLowerCase())){
                                            continue;
                                        }
                                        ipv6.setIpv6_address(json.getString("ipaddr"));
                                    }
                                    ipv6.setCreateTime(context.getCreateTime());
                                    ipv6.setIpv6_mac(json.getString("mac").replace("-", ":"));

                                    ipv6List.add(ipv6);

                                }
                                this.ipv6Service.batchInsertGather(ipv6List);
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
        String result = "[  {    \"ipv6_address\": \"240E:670:9830:1::BFC\",    \"ipv6_mac\": \"b8:d6:f6:12:f1:cd\",    \"state\": \"STALE\",    \"port\": \"XGE1/8/0/5\",    \"age\": \"00\",    \"vid\": \"100\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::BFE\",    \"ipv6_mac\": \"00:00:5e:00:02:66\",    \"state\": \"STALE\",    \"port\": \"XGE1/8/0/5\",    \"age\": \"00\",    \"vid\": \"100\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::6805\",    \"ipv6_mac\": \"f4:f1:9e:1b:05:23\",    \"state\": \"STALE\",    \"port\": \"GE2/2/0/5\",    \"age\": \"00\",    \"vid\": \"140\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::682A\",    \"ipv6_mac\": \"66:6d:ac:8c:9e:b6\",    \"state\": \"STALE\",    \"port\": \"Eth-Trunk20\",    \"age\": \"00\",    \"vid\": \"140\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::1002\",    \"ipv6_mac\": \"5c:60:ba:3c:0e:96\",    \"state\": \"STALE\",    \"port\": \"GE1/11/0/16\",    \"age\": \"00\",    \"vid\": \"220\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::1003\",    \"ipv6_mac\": \"d8:cb:8a:68:86:44\",    \"state\": \"STALE\",    \"port\": \"GE1/11/0/16\",    \"age\": \"00\",    \"vid\": \"220\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::1004\",    \"ipv6_mac\": \"c8:5a:cf:01:a2:e9\",    \"state\": \"STALE\",    \"port\": \"GE1/11/0/16\",    \"age\": \"00\",    \"vid\": \"220\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::1005\",    \"ipv6_mac\": \"44:37:e6:ec:ce:d2\",    \"state\": \"STALE\",    \"port\": \"GE1/11/0/16\",    \"age\": \"00\",    \"vid\": \"220\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::1006\",    \"ipv6_mac\": \"f8:b4:6a:80:0d:55\",    \"state\": \"STALE\",    \"port\": \"GE1/11/0/16\",    \"age\": \"00\",    \"vid\": \"220\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::1801\",    \"ipv6_mac\": \"e0:be:03:9c:a3:3d\",    \"state\": \"STALE\",    \"port\": \"GE1/11/0/12\",    \"age\": \"00\",    \"vid\": \"240\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::1807\",    \"ipv6_mac\": \"50:7b:9d:8e:10:17\",    \"state\": \"STALE\",    \"port\": \"GE1/11/0/12\",    \"age\": \"00\",    \"vid\": \"240\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::2023\",    \"ipv6_mac\": \"4c:cc:6a:31:ca:c9\",    \"state\": \"STALE\",    \"port\": \"XGE1/8/0/10\",    \"age\": \"00\",    \"vid\": \"250\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::202D\",    \"ipv6_mac\": \"d8:cb:8a:6a:ab:c8\",    \"state\": \"STALE\",    \"port\": \"XGE1/8/0/10\",    \"age\": \"00\",    \"vid\": \"250\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::2803\",    \"ipv6_mac\": \"d8:cb:8a:0e:1b:57\",    \"state\": \"STALE\",    \"port\": \"XGE1/8/0/10\",    \"age\": \"00\",    \"vid\": \"270\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::2822\",    \"ipv6_mac\": \"a0:8c:fd:e1:76:bb\",    \"state\": \"STALE\",    \"port\": \"XGE1/8/0/10\",    \"age\": \"00\",    \"vid\": \"270\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::2824\",    \"ipv6_mac\": \"a0:8c:fd:e1:67:5c\",    \"state\": \"STALE\",    \"port\": \"XGE1/8/0/10\",    \"age\": \"00\",    \"vid\": \"270\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::2830\",    \"ipv6_mac\": \"b8:ae:ed:21:f3:c1\",    \"state\": \"STALE\",    \"port\": \"XGE1/8/0/10\",    \"age\": \"00\",    \"vid\": \"270\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::2831\",    \"ipv6_mac\": \"c8:d3:ff:c0:cf:47\",    \"state\": \"STALE\",    \"port\": \"XGE1/8/0/10\",    \"age\": \"00\",    \"vid\": \"270\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::2832\",    \"ipv6_mac\": \"d8:cb:8a:0e:1b:77\",    \"state\": \"STALE\",    \"port\": \"XGE1/8/0/10\",    \"age\": \"00\",    \"vid\": \"270\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::2834\",    \"ipv6_mac\": \"e8:6a:64:da:17:db\",    \"state\": \"STALE\",    \"port\": \"XGE1/8/0/10\",    \"age\": \"00\",    \"vid\": \"270\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::2C02\",    \"ipv6_mac\": \"4c:cc:6a:31:ca:ab\",    \"state\": \"STALE\",    \"port\": \"XGE1/8/0/10\",    \"age\": \"00\",    \"vid\": \"280\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::2C05\",    \"ipv6_mac\": \"8c:16:45:43:12:50\",    \"state\": \"STALE\",    \"port\": \"XGE1/8/0/10\",    \"age\": \"00\",    \"vid\": \"280\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::2C06\",    \"ipv6_mac\": \"8c:16:45:45:4b:e6\",    \"state\": \"STALE\",    \"port\": \"XGE1/8/0/10\",    \"age\": \"00\",    \"vid\": \"280\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::2C1C\",    \"ipv6_mac\": \"44:8a:5b:e1:9e:d6\",    \"state\": \"STALE\",    \"port\": \"XGE1/8/0/10\",    \"age\": \"00\",    \"vid\": \"280\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::2C1E\",    \"ipv6_mac\": \"4c:cc:6a:77:15:2e\",    \"state\": \"STALE\",    \"port\": \"XGE1/8/0/10\",    \"age\": \"00\",    \"vid\": \"280\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::2C1F\",    \"ipv6_mac\": \"70:b5:e8:5a:3a:19\",    \"state\": \"STALE\",    \"port\": \"XGE1/8/0/10\",    \"age\": \"00\",    \"vid\": \"280\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::2C39\",    \"ipv6_mac\": \"e8:6a:64:da:17:de\",    \"state\": \"STALE\",    \"port\": \"XGE1/8/0/10\",    \"age\": \"00\",    \"vid\": \"280\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::6C02\",    \"ipv6_mac\": \"c4:65:16:26:c5:a8\",    \"state\": \"STALE\",    \"port\": \"Eth-Trunk10\",    \"age\": \"00\",    \"vid\": \"300\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::6C05\",    \"ipv6_mac\": \"f4:f1:9e:1b:04:f5\",    \"state\": \"STALE\",    \"port\": \"Eth-Trunk10\",    \"age\": \"00\",    \"vid\": \"300\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::6C18\",    \"ipv6_mac\": \"c4:65:16:26:b6:e6\",    \"state\": \"STALE\",    \"port\": \"Eth-Trunk10\",    \"age\": \"00\",    \"vid\": \"300\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::6C2D\",    \"ipv6_mac\": \"44:8a:5b:ea:cc:09\",    \"state\": \"STALE\",    \"port\": \"Eth-Trunk10\",    \"age\": \"00\",    \"vid\": \"300\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::6C3B\",    \"ipv6_mac\": \"90:f6:44:cf:1d:50\",    \"state\": \"STALE\",    \"port\": \"Eth-Trunk10\",    \"age\": \"00\",    \"vid\": \"300\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::3C01\",    \"ipv6_mac\": \"6c:92:bf:58:a5:2e\",    \"state\": \"STALE\",    \"port\": \"GE2/2/0/17\",    \"age\": \"00\",    \"vid\": \"400\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::3C03\",    \"ipv6_mac\": \"6c:92:bf:58:a5:2c\",    \"state\": \"STALE\",    \"port\": \"GE2/2/0/17\",    \"age\": \"00\",    \"vid\": \"400\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::3C15\",    \"ipv6_mac\": \"20:7b:d2:59:33:d8\",    \"state\": \"STALE\",    \"port\": \"GE2/2/0/17\",    \"age\": \"00\",    \"vid\": \"400\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::4405\",    \"ipv6_mac\": \"28:6e:d4:89:79:d0\",    \"state\": \"STALE\",    \"port\": \"Eth-Trunk63\",    \"age\": \"00\",    \"vid\": \"450\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::4C99\",    \"ipv6_mac\": \"28:6e:d4:89:60:74\",    \"state\": \"STALE\",    \"port\": \"Eth-Trunk63\",    \"age\": \"00\",    \"vid\": \"452\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::6401\",    \"ipv6_mac\": \"f4:f1:9e:1b:32:18\",    \"state\": \"STALE\",    \"port\": \"Eth-Trunk20\",    \"age\": \"00\",    \"vid\": \"4040\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::6402\",    \"ipv6_mac\": \"14:84:77:22:0a:f0\",    \"state\": \"STALE\",    \"port\": \"Eth-Trunk20\",    \"age\": \"00\",    \"vid\": \"4040\",    \"vpninstance\": \"Is\"  },  {    \"ipv6_address\": \"240E:670:9830:1::642A\",    \"ipv6_mac\": \"f4:f1:9e:1b:02:75\",    \"state\": \"STALE\",    \"port\": \"Eth-Trunk20\",    \"age\": \"00\",    \"vid\": \"4040\",    \"vpninstance\": \"Is\"  }]";
        List<Ipv6> ipv6List = JSONObject.parseArray(result, Ipv6.class);
        System.out.println(ipv6List);
    }
}


