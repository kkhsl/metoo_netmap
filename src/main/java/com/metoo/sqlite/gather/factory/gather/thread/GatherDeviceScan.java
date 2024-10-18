package com.metoo.sqlite.gather.factory.gather.thread;

import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Arp;
import com.metoo.sqlite.entity.DeviceScan;
import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.gather.common.ConcatenationCharacter;
import com.metoo.sqlite.service.IArpService;
import com.metoo.sqlite.service.IDeviceScanService;
import com.metoo.sqlite.service.IProbeService;
import com.metoo.sqlite.utils.date.DateTools;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-28 21:51
 */
@Slf4j
public class GatherDeviceScan implements Gather {

    @Override
    public void executeMethod() {

        Long time = System.currentTimeMillis();

        log.info("Device scan start ......");

        version();

        log.info("Device scan end ......" + (System.currentTimeMillis() - time));
    }

    public void version(){
        try {
            IDeviceScanService deviceScanService = (IDeviceScanService) ApplicationContextUtils.getBean("deviceScanServiceImpl");
            IProbeService probeService = (IProbeService) ApplicationContextUtils.getBean("probeServiceImpl");

            deviceScanService.deleteTable();
            deviceScanService.deleteTableBck();

            List<Probe> probeList = probeService.selectDeduplicationByIp(null);

            if (probeList.size() > 0) {
                // 过滤ip地址相同probe，合并指定字段[vender、osgen、osfamily]
                for (Probe probe : probeList) {

                    DeviceScan deviceScan = null;
                    List<DeviceScan> deviceScanList = deviceScanService.selectObjByIpv4OrIpv6(probe.getIp_addr(), probe.getIpv6());
                    if(deviceScanList.size() > 0){
                        deviceScan = deviceScanList.get(0);
                    }else{
                        deviceScan = new DeviceScan();
                    }

                    boolean flag = false;

                    String ttls = probe.getTtls();
                    if(StringUtil.isNotEmpty(ttls)){
                        String[] eles = ttls.split(",");
                        for (String ele : eles) {
                            if(Integer.parseInt(ele) > 200){
                                flag = true;
                                break;
                            }
                        }
                    }

                    String application_protocl = probe.getApplication_protocol();
                    if (!flag && (application_protocl != null && application_protocl.toLowerCase().contains("telnet"))) {
                        flag = true;
                    }
                    if(flag){
                        try {
                            deviceScan.setCreateTime(DateTools.getCreateTime());
                            deviceScan.setDevice_ipv4(probe.getIp_addr());
                            deviceScan.setDevice_type(probe.getDevice_type());
                            deviceScan.setMac(probe.getMac());
                            deviceScan.setMacVendor(probe.getMac_vendor());

                            String vendor = probe.getVendor();
                            String os_gen = probe.getOs_gen();
                            String os_family = probe.getOs_family();
                            String device_product = ConcatenationCharacter
                                    .concatenateWithSpace(":",
                                            ConcatenationCharacter.disassembleWithSpace(",", vendor),
                                            ConcatenationCharacter.disassembleWithSpace(",", os_gen),
                                            ConcatenationCharacter.disassembleWithSpace(",", os_family));
                            deviceScan.setDevice_product(device_product);

                            deviceScan.setDevice_ipv6(probe.getIpv6());
                            deviceScanService.insert(deviceScan);

                            probeService.deleteProbeByIp(probe.getIp_addr(), probe.getIpv6());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                deviceScanService.copyToBck();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
