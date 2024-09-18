package com.metoo.sqlite.gather.strategy.other;

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
public class Ipv6ByVendorCollectionStrategy implements DataCollectionStrategy {

    private final Ipv6Service ipv6Service;
    private final PyExecUtils pyExecUtils;

    @Autowired
    public Ipv6ByVendorCollectionStrategy(Ipv6Service ipv6Service, PyExecUtils pyExecUtils) {
        this.ipv6Service = ipv6Service;
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
                pyCommand.setPath(Global.PYPATH);
                pyCommand.setName("main.pyc");
                pyCommand.setParams(new String[]{
                        device.getDeviceVendorAlias(),
                        device.getDeviceTypeAlias(),
                        device.getIp(),
                        device.getLoginType(),
                        device.getLoginPort(),
                        device.getLoginName(),
                        device.getLoginPassword(), Global.IPV6_ARP});
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
        String result = "[{\n" +
                "\t\"ipv6_address\": \"2408:864C:8001::1\",\n" +
                "\t\"ipv6_mac\": \"fc:73:fb:ff:cb:bb\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"REACH\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"26\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001::12\",\n" +
                "\t\"ipv6_mac\": \"fc:73:fb:ff:cb:bb\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"5202\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:14::4\",\n" +
                "\t\"ipv6_mac\": \"1c:69:7a:2a:ab:00\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"11\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:14::8\",\n" +
                "\t\"ipv6_mac\": \"00:e0:1a:51:61:bf\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"3899\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::6\",\n" +
                "\t\"ipv6_mac\": \"e0:be:03:51:bb:ec\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"1859\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::8\",\n" +
                "\t\"ipv6_mac\": \"50:7b:9d:b2:49:69\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"1748\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::A\",\n" +
                "\t\"ipv6_mac\": \"1c:69:7a:29:f0:8c\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"14248\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::D\",\n" +
                "\t\"ipv6_mac\": \"9c:5a:44:8a:6e:61\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"278\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::14\",\n" +
                "\t\"ipv6_mac\": \"8c:16:45:4b:a4:35\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"575\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::1C\",\n" +
                "\t\"ipv6_mac\": \"94:c6:91:df:93:74\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"13\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::2E\",\n" +
                "\t\"ipv6_mac\": \"6c:4b:90:8a:6c:98\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"43\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::2F\",\n" +
                "\t\"ipv6_mac\": \"4c:cc:6a:ee:09:02\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"255\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::33\",\n" +
                "\t\"ipv6_mac\": \"e8:6a:64:12:03:bb\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"67\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::3E\",\n" +
                "\t\"ipv6_mac\": \"8c:16:45:4b:9e:e4\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"2925\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::3F\",\n" +
                "\t\"ipv6_mac\": \"38:ca:84:3b:3d:c6\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"2583\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::44\",\n" +
                "\t\"ipv6_mac\": \"94:c6:91:d3:44:18\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"REACH\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"30\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::4D\",\n" +
                "\t\"ipv6_mac\": \"7c:57:58:5e:15:21\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"2583\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::4F\",\n" +
                "\t\"ipv6_mac\": \"00:e0:4c:02:cd:b5\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"REACH\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"30\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::52\",\n" +
                "\t\"ipv6_mac\": \"7c:57:58:5e:15:26\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"11485\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::53\",\n" +
                "\t\"ipv6_mac\": \"e0:73:e7:29:1d:1c\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"2617\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::54\",\n" +
                "\t\"ipv6_mac\": \"10:e7:c6:1f:20:4f\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"5470\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::57\",\n" +
                "\t\"ipv6_mac\": \"bc:ec:a0:31:73:9e\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"REACH\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"23\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::58\",\n" +
                "\t\"ipv6_mac\": \"08:8f:c3:bb:30:bf\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"13012\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::59\",\n" +
                "\t\"ipv6_mac\": \"e0:be:03:32:aa:84\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"30\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::67\",\n" +
                "\t\"ipv6_mac\": \"2c:f0:5d:2b:d9:34\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"9320\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:15::6A\",\n" +
                "\t\"ipv6_mac\": \"20:7b:d2:c3:21:4d\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"121\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:17::6\",\n" +
                "\t\"ipv6_mac\": \"e8:6a:64:81:0f:bd\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"1036\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:17::7\",\n" +
                "\t\"ipv6_mac\": \"e0:be:03:73:02:04\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"12648\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:18::6\",\n" +
                "\t\"ipv6_mac\": \"94:c6:91:71:c6:db\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"4687\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:18::B\",\n" +
                "\t\"ipv6_mac\": \"00:0e:c6:28:28:48\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"146\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:18::12\",\n" +
                "\t\"ipv6_mac\": \"1c:69:7a:2b:3c:36\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"3305\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:18::14\",\n" +
                "\t\"ipv6_mac\": \"00:0e:c6:2d:b3:27\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"341\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:18::1C\",\n" +
                "\t\"ipv6_mac\": \"8c:16:45:4b:9e:e3\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"3241\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:18::20\",\n" +
                "\t\"ipv6_mac\": \"c8:4d:44:29:7f:d6\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"191\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:18::2E\",\n" +
                "\t\"ipv6_mac\": \"1c:69:7a:2e:86:5f\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"1794\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:18::30\",\n" +
                "\t\"ipv6_mac\": \"6c:4b:90:9a:fc:bd\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"56\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:18::32\",\n" +
                "\t\"ipv6_mac\": \"1c:69:7a:2b:39:d8\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"2537\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:18::34\",\n" +
                "\t\"ipv6_mac\": \"e0:be:03:73:01:e2\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"4618\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:18::35\",\n" +
                "\t\"ipv6_mac\": \"8c:16:45:4b:8b:20\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"63\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:18::3F\",\n" +
                "\t\"ipv6_mac\": \"f4:4d:30:1a:07:18\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"4418\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:18::43\",\n" +
                "\t\"ipv6_mac\": \"1c:69:7a:29:f0:e8\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"605\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:18::48\",\n" +
                "\t\"ipv6_mac\": \"d8:cb:8a:dc:8f:5c\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"1791\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:18::4C\",\n" +
                "\t\"ipv6_mac\": \"6c:4b:90:50:eb:44\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"5705\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}, {\n" +
                "\t\"ipv6_address\": \"2408:864C:8001:7:18::53\",\n" +
                "\t\"ipv6_mac\": \"8c:16:45:4b:a0:a6\",\n" +
                "\t\"vid\": \"\",\n" +
                "\t\"port\": \"\",\n" +
                "\t\"state\": \"STALE\",\n" +
                "\t\"type\": \"Dynamic\",\n" +
                "\t\"age\": \"2171\",\n" +
                "\t\"vpninstance\": \"--                             \"\n" +
                "}]";
        List<Ipv6> ipv6List = JSONObject.parseArray(result, Ipv6.class);
        System.out.println(ipv6List);
    }
}


