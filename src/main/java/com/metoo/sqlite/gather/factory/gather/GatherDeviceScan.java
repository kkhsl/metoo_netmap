package com.metoo.sqlite.gather.factory.gather;

import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Arp;
import com.metoo.sqlite.entity.DeviceScan;
import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.service.*;
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
        log.info("metoo_scan Start......");

        try {
            IDeviceScanService deviceScanService = (IDeviceScanService) ApplicationContextUtils.getBean("deviceScanServiceImpl");
            IArpService arpService = (IArpService) ApplicationContextUtils.getBean("arpServiceImpl");
            IProbeService probeService = (IProbeService) ApplicationContextUtils.getBean("probeServiceImpl");

            List<Probe> probes = probeService.selectObjByMap(null);

            if(probes.size() > 0){

                List<DeviceScan> deviceScans = new ArrayList<>();

                String createTime = DateTools.getCreateTime();

                Set<String> ips = new HashSet<>();
                List<Probe> probes_exchange = probes.stream()
                        .filter(e -> "交换机".equals(e.getDevice_type())
                                || "路由器".equals(e.getDevice_type()))
                        .collect(Collectors.toList());
                if(probes_exchange.size() > 0){
                    for (Probe probe : probes_exchange) {
                        // 写入deviceScan
                        try {
                            DeviceScan deviceScan = new DeviceScan();
                            deviceScan.setCreateTime(createTime);
                            deviceScan.setDevice_ipv4(probe.getIp_addr());
                            deviceScan.setDevice_product(probe.getPort_service_product());
                            deviceScan.setDevice_type(probe.getDevice_type());

    //                        this.deviceScanService.insert(deviceScan);
                            deviceScans.add(deviceScan);

                            // 删除arp条目
                            if(StringUtil.isNotEmpty(probe.getIp_addr())){
                                ips.add(probe.getIp_addr());
                            }
                            probeService.delete(probe.getId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                List<Probe> probes_switch = probes.stream()
                        .filter(e -> e.getPort_service_product() != null && e.getPort_service_product().contains("switch"))
                        .collect(Collectors.toList());
                for (Probe probe : probes_switch) {
                    // 写入deviceScan
                    try {
                        DeviceScan deviceScan = new DeviceScan();
                        deviceScan.setCreateTime(createTime);
                        deviceScan.setDevice_ipv4(probe.getIp_addr());
                        deviceScan.setDevice_product(probe.getPort_service_product());
                        deviceScan.setDevice_type(probe.getDevice_type());

    //                    this.deviceScanService.insert(deviceScan);
                        deviceScans.add(deviceScan);

                        // 删除arp条目
                        ips.add(probe.getIp_addr());

                        probeService.delete(probe.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // 删除arp条目
                if(ips.size() > 0){
                    Map params = new HashMap();
                    for (String ip : ips) {
                        params.clear();
                        params.put("ip", ip);
                        List<Arp> arps = arpService.selectObjByMap(params);
                        if(arps.size() > 0){
                            for (Arp arp : arps) {
                                arpService.delete(arp.getId());
                            }
                        }
                    }
                }

                // 批量写入
                deviceScanService.copyGatherData(deviceScans);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("metoo_scan End......" + (System.currentTimeMillis() - time));

    }
}
