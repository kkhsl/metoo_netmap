package com.metoo.sqlite.gather.strategy.other;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.Ipv4;
import com.metoo.sqlite.entity.Ipv6;
import com.metoo.sqlite.gather.common.PyCommand;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.Ipv4Service;
import com.metoo.sqlite.service.Ipv6Service;
import com.metoo.sqlite.utils.Global;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
public class Ipv6CollectionStrategy implements DataCollectionStrategy {

    private final Ipv6Service ipv6Service;
    private final PyExecUtils pyExecUtils;

    @Autowired
    public Ipv6CollectionStrategy(Ipv6Service ipv6Service, PyExecUtils pyExecUtils) {
        this.ipv6Service = ipv6Service;
        this.pyExecUtils = pyExecUtils;
    }

    @Override
    public void collectData(Context context) {
        try {
            Device device = (Device) context.getEntity();
            if (device != null) {
//                PyCommand pyCommand = (PyCommand) ApplicationContextUtils.getBean("pyCommand");
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
                        device.getLoginPassword(), Global.PY_SUFFIX_IPV6_NEIGHBORS});
                try {
                    String result = this.pyExecUtils.exec(pyCommand);
                    if (StringUtil.isNotEmpty(result)) {
                        List<Ipv6> ipv6List = JSONObject.parseArray(result, Ipv6.class);
                        if (ipv6List.size() > 0) {

                            List<Ipv6> filteredObjects = ipv6List.stream()
                                    .filter(obj -> obj.getIpv6_address() != null
                                            && !obj.getIpv6_address().toLowerCase().startsWith("FE80".toLowerCase()))
                                    .collect(Collectors.toList());

                            if(filteredObjects.size() > 0){
                                filteredObjects.forEach(e -> {
                                    e.setDeviceUuid(device.getUuid());
                                    e.setCreateTime(context.getCreateTime());
                                });
                                this.ipv6Service.batchInsertGather(filteredObjects);
//                                for (Ipv6 filteredObject : filteredObjects) {
//                                    ipv6Service.insert(filteredObject);
//                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        String result = "[  {    \"ipv6_address\": \"FD01:172:172:172::250\",    \"ipv6_mac\": \"c4:d4:38:c3:2d:40\",    \"vid\": \"902\",    \"port\": \"GE1/1/0/1\",    \"state\": \"REACH\",    \"type\": \"Dynamic\",    \"age\": \"80\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:102::170\",    \"ipv6_mac\": \"00:23:24:b9:ea:ef\",    \"vid\": \"652\",    \"port\": \"GE1/1/0/16\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"2825\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:102::173\",    \"ipv6_mac\": \"4c:cc:6a:13:81:6e\",    \"vid\": \"652\",    \"port\": \"GE1/1/0/16\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"1598\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:102::174\",    \"ipv6_mac\": \"4c:cc:6a:38:54:c9\",    \"vid\": \"652\",    \"port\": \"GE1/1/0/16\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"3019\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:252::251\",    \"ipv6_mac\": \"70:ba:ef:9b:7f:a3\",    \"vid\": \"3\",    \"port\": \"BAGG1\",    \"state\": \"REACH\",    \"type\": \"Dynamic\",    \"age\": \"108\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:172:172:170::253\",    \"ipv6_mac\": \"70:ba:ef:9b:7f:a3\",    \"vid\": \"900\",    \"port\": \"BAGG1\",    \"state\": \"REACH\",    \"type\": \"Dynamic\",    \"age\": \"108\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:22::65\",    \"ipv6_mac\": \"30:b4:9e:67:71:3a\",    \"vid\": \"22\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"183\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:22::176\",    \"ipv6_mac\": \"d0:39:57:45:2b:99\",    \"vid\": \"22\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"185\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:22::177\",    \"ipv6_mac\": \"10:02:b5:cb:0c:ff\",    \"vid\": \"22\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"2523\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:22::187\",    \"ipv6_mac\": \"b0:47:e9:b2:58:40\",    \"vid\": \"22\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"2228\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:22::194\",    \"ipv6_mac\": \"a4:34:d9:2a:e0:d6\",    \"vid\": \"22\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"2532\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:23::187\",    \"ipv6_mac\": \"30:b4:9e:0c:0b:47\",    \"vid\": \"23\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"1774\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:23::195\",    \"ipv6_mac\": \"08:57:00:90:02:3b\",    \"vid\": \"23\",    \"port\": \"BAGG3\",    \"state\": \"REACH\",    \"type\": \"Dynamic\",    \"age\": \"1171\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:23::197\",    \"ipv6_mac\": \"30:b4:9e:68:b1:c1\",    \"vid\": \"23\",    \"port\": \"BAGG3\",    \"state\": \"REACH\",    \"type\": \"Dynamic\",    \"age\": \"739\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:23::200\",    \"ipv6_mac\": \"30:b4:9e:68:4c:a9\",    \"vid\": \"23\",    \"port\": \"BAGG3\",    \"state\": \"REACH\",    \"type\": \"Dynamic\",    \"age\": \"108\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:24::36\",    \"ipv6_mac\": \"30:b4:9e:d3:10:bf\",    \"vid\": \"24\",    \"port\": \"BAGG3\",    \"state\": \"REACH\",    \"type\": \"Dynamic\",    \"age\": \"451\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:24::153\",    \"ipv6_mac\": \"d0:39:57:45:39:3b\",    \"vid\": \"24\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"3364\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:24::154\",    \"ipv6_mac\": \"30:b4:9e:5d:be:f0\",    \"vid\": \"24\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"167\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:24::155\",    \"ipv6_mac\": \"78:60:5b:9f:61:e0\",    \"vid\": \"24\",    \"port\": \"BAGG3\",    \"state\": \"REACH\",    \"type\": \"Dynamic\",    \"age\": \"604\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:24::156\",    \"ipv6_mac\": \"30:b4:9e:55:6a:6f\",    \"vid\": \"24\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"6019\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:24::161\",    \"ipv6_mac\": \"80:9b:20:de:bf:84\",    \"vid\": \"24\",    \"port\": \"BAGG3\",    \"state\": \"REACH\",    \"type\": \"Dynamic\",    \"age\": \"1128\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:24::180\",    \"ipv6_mac\": \"30:b4:9e:01:87:cd\",    \"vid\": \"24\",    \"port\": \"BAGG3\",    \"state\": \"REACH\",    \"type\": \"Dynamic\",    \"age\": \"1044\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:24::194\",    \"ipv6_mac\": \"30:b4:9e:59:f1:c7\",    \"vid\": \"24\",    \"port\": \"BAGG3\",    \"state\": \"REACH\",    \"type\": \"Dynamic\",    \"age\": \"102\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:24::198\",    \"ipv6_mac\": \"30:b4:9e:59:f2:7e\",    \"vid\": \"24\",    \"port\": \"BAGG3\",    \"state\": \"REACH\",    \"type\": \"Dynamic\",    \"age\": \"845\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:24::203\",    \"ipv6_mac\": \"f8:34:41:38:89:25\",    \"vid\": \"24\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"13472\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:25::187\",    \"ipv6_mac\": \"e4:a4:71:4f:5c:86\",    \"vid\": \"25\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"1627\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:25::189\",    \"ipv6_mac\": \"dc:97:ba:2b:95:8f\",    \"vid\": \"25\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"1901\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:25::198\",    \"ipv6_mac\": \"28:56:5a:0b:60:4f\",    \"vid\": \"25\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"2401\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:25::200\",    \"ipv6_mac\": \"00:28:f8:bb:19:21\",    \"vid\": \"25\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"1690\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:26::200\",    \"ipv6_mac\": \"08:57:00:93:bd:6a\",    \"vid\": \"26\",    \"port\": \"BAGG3\",    \"state\": \"REACH\",    \"type\": \"Dynamic\",    \"age\": \"611\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:29::180\",    \"ipv6_mac\": \"18:93:41:75:7d:df\",    \"vid\": \"29\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"1174\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:29::184\",    \"ipv6_mac\": \"18:93:41:75:b9:d5\",    \"vid\": \"29\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"3603\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:29::186\",    \"ipv6_mac\": \"3c:46:d8:85:af:6c\",    \"vid\": \"29\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"3327\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:29::188\",    \"ipv6_mac\": \"08:57:00:93:55:2a\",    \"vid\": \"29\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"2679\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:29::189\",    \"ipv6_mac\": \"18:93:41:76:ee:ea\",    \"vid\": \"29\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"3218\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:29::190\",    \"ipv6_mac\": \"08:57:00:8f:d5:14\",    \"vid\": \"29\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"835\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:29::196\",    \"ipv6_mac\": \"18:93:41:77:16:90\",    \"vid\": \"29\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"1224\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:29::197\",    \"ipv6_mac\": \"9c:da:3e:6b:af:53\",    \"vid\": \"29\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"2245\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:29::198\",    \"ipv6_mac\": \"cc:b0:da:e7:c1:a7\",    \"vid\": \"29\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"1862\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:31::186\",    \"ipv6_mac\": \"00:93:37:91:00:0a\",    \"vid\": \"31\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"2333\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:31::188\",    \"ipv6_mac\": \"30:b4:9e:d2:d3:37\",    \"vid\": \"31\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"2607\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:31::200\",    \"ipv6_mac\": \"30:b4:9e:68:ec:8c\",    \"vid\": \"31\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"2262\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:32::139\",    \"ipv6_mac\": \"30:b4:9e:67:57:3b\",    \"vid\": \"32\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"2184\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:32::189\",    \"ipv6_mac\": \"b0:47:e9:b3:41:b0\",    \"vid\": \"32\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"1303\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:32::199\",    \"ipv6_mac\": \"18:93:41:78:17:c5\",    \"vid\": \"32\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"2945\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:32::200\",    \"ipv6_mac\": \"18:93:41:76:0b:83\",    \"vid\": \"32\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"264\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:239::28\",    \"ipv6_mac\": \"b0:47:e9:f0:f9:2e\",    \"vid\": \"239\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"1857\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:239::99\",    \"ipv6_mac\": \"d0:39:57:45:35:b5\",    \"vid\": \"239\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"1176\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:239::110\",    \"ipv6_mac\": \"f4:8c:50:4f:f4:6b\",    \"vid\": \"239\",    \"port\": \"BAGG3\",    \"state\": \"REACH\",    \"type\": \"Dynamic\",    \"age\": \"622\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:239::117\",    \"ipv6_mac\": \"14:ab:c5:17:b7:31\",    \"vid\": \"239\",    \"port\": \"BAGG3\",    \"state\": \"REACH\",    \"type\": \"Dynamic\",    \"age\": \"351\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:239::118\",    \"ipv6_mac\": \"00:21:6b:fd:61:fa\",    \"vid\": \"239\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"1757\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:239::130\",    \"ipv6_mac\": \"30:52:cb:80:c2:31\",    \"vid\": \"239\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"1987\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:239::162\",    \"ipv6_mac\": \"d0:39:57:44:4a:21\",    \"vid\": \"239\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"2071\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:239::179\",    \"ipv6_mac\": \"d0:39:57:45:37:21\",    \"vid\": \"239\",    \"port\": \"BAGG3\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"2784\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:104::25\",    \"ipv6_mac\": \"e0:be:03:8e:db:cd\",    \"vid\": \"654\",    \"port\": \"BAGG12\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"2290\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:104::108\",    \"ipv6_mac\": \"08:8f:c3:f9:40:d0\",    \"vid\": \"654\",    \"port\": \"BAGG12\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"2922\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:104::184\",    \"ipv6_mac\": \"54:e1:ad:58:2b:a7\",    \"vid\": \"654\",    \"port\": \"BAGG12\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"1095\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:104::198\",    \"ipv6_mac\": \"64:00:6a:7b:ff:50\",    \"vid\": \"654\",    \"port\": \"BAGG12\",    \"state\": \"REACH\",    \"type\": \"Dynamic\",    \"age\": \"49\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:107::184\",    \"ipv6_mac\": \"6c:0b:84:07:b3:3c\",    \"vid\": \"107\",    \"port\": \"BAGG11\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"2881\",    \"vpninstance\": \"--                             \"  },  {    \"ipv6_address\": \"FD01:10:20:239::29\",    \"ipv6_mac\": \"6c:0b:84:6b:a8:38\",    \"vid\": \"239\",    \"port\": \"GE2/1/0/9\",    \"state\": \"STALE\",    \"type\": \"Dynamic\",    \"age\": \"3816\",    \"vpninstance\": \"--                             \"  }]";
        List<Ipv6> ipv6List = JSONObject.parseArray(result, Ipv6.class);
        System.out.println(ipv6List);
    }
}


