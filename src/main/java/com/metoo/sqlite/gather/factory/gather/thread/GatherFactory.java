package com.metoo.sqlite.gather.factory.gather.thread;

import com.metoo.sqlite.utils.Global;
import org.springframework.stereotype.Component;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-24 16:43
 */
@Component
public class GatherFactory {

    // 非线程安全
    public Gather getGather(String shapeType){
        if(shapeType == null){
            return null;
        }
        if(shapeType.equalsIgnoreCase(Global.PY_SUFFIX_ALIVEINT)){
            return new GatherPort();
        } else if(shapeType.equalsIgnoreCase(Global.PY_SUFFIX_IPV4)){
            return new GatherIpv4();
        } else if(shapeType.equalsIgnoreCase(Global.PY_SUFFIX_IPV6_NEIGHBORS)){
            return new GatherIpv6();
        } else if(shapeType.equalsIgnoreCase(Global.PY_SUFFIX_PING)){
            return new GatherPing();
        }else if(shapeType.equalsIgnoreCase(Global.SUBNET_IPV6)){
            return new GatherSubentIpv6();
        } else if(shapeType.equalsIgnoreCase(Global.ARP)){
            return new GatherArp();
        } else if(shapeType.equalsIgnoreCase(Global.PY_SUFFIX_GET_SWITCH)){
            return new GatherGetSwith();
        } else if(shapeType.equalsIgnoreCase(Global.PY_SUFFIX_GET_FIREWALL)){
            return new GatherGatewayInfo();
        } else if(shapeType.equalsIgnoreCase(Global.DEVICE_SCAN)){
            return new GatherDeviceScan();
        } else if(shapeType.equalsIgnoreCase(Global.TERMINAL)){
            return new GatherTerminal();
        } /*else if(shapeType.equalsIgnoreCase(Global.GATEWAY_OPERATOR)){
            return new GatherGatewayOperator();
        }*/ else if(shapeType.equalsIgnoreCase("fileToProbe2")){
            return new GatherOsScan();
        } else if(shapeType.equalsIgnoreCase("fileToProbe")){
//            return new GatherOsScanVersin();

            return new GatherOsScanVersin3();

        } else if(shapeType.equalsIgnoreCase(Global.IPV4_PANABIT)){
            return new GatherIpv4Panabit();
        } else if(shapeType.equalsIgnoreCase(Global.IPV6_PANABIT)){
            return new GatherIpv6Panabit();
        }
        return null;
    }

}
