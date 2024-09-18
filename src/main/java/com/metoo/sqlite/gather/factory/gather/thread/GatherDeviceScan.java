package com.metoo.sqlite.gather.factory.gather.thread;

import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Arp;
import com.metoo.sqlite.entity.DeviceScan;
import com.metoo.sqlite.entity.Probe;
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
            IArpService arpService = (IArpService) ApplicationContextUtils.getBean("arpServiceImpl");

            deviceScanService.deleteTable();
            deviceScanService.deleteTableBck();

            Map params = new HashMap();
            params.clear();
            List<Probe> probeList = probeService.selectObjByMap(params);

            Set<String> deleteSet = new HashSet();

            if (probeList.size() > 0) {

                List<Probe> uniqueDevices = probeList.stream()
                        .collect(Collectors.toMap(
                                Probe::getIp_addr,
                                probe -> probe,
                                (existing, replacement) -> existing
                        ))
                        .values()
                        .stream()
                        .collect(Collectors.toList());

                for (int i = 0; i < uniqueDevices.size(); i++) {

                    params.clear();
                    params.put("ip_addr", uniqueDevices.get(i).getIp_addr());
                    List<Probe> probes = probeService.selectObjByMap(params);

                    for (Probe probe : probes) {

                        DeviceScan deviceScan = null;

                        params.clear();
                        params.put("device_ipv4", probe.getIp_addr());
                        List<DeviceScan> deviceScanList = deviceScanService.selectObjByMap(params);
                        if(deviceScanList.size() > 0){
                            deviceScan = deviceScanList.get(0);
                        }else{
                            deviceScan = new DeviceScan();
                        }

                        if (probe.getTtl() != null && probe.getTtl() > 200) {
                            try {
                                deviceScan.setCreateTime(DateTools.getCreateTime());
                                deviceScan.setDevice_ipv4(probe.getIp_addr());
                                deviceScan.setDevice_type(probe.getDevice_type());

                                params.clear();
                                params.put("likeIp", "%" + deviceScan.getDevice_ipv4() + "%");
                                List<Arp> arps = arpService.selectObjByMap(params);
                                if(arps.size() > 0){
                                    Arp arp = arps.get(0);
                                    deviceScan.setMac(arp.getMac());
                                    deviceScan.setMacVendor(arp.getMacVendor());
                                    deviceScan.setDevice_product(arp.getMacVendor());
                                    deviceScan.setDevice_ipv6(arp.getIpv6());
                                }

                                deviceScanService.insert(deviceScan);

                                deleteSet.add(probe.getIp_addr());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else if (probe.getPort_num() != null
                                && (probe.getPort_num().equals("53") || probe.getPort_num().equals("23"))){
                            try {
                                deviceScan.setCreateTime(DateTools.getCreateTime());
                                deviceScan.setDevice_ipv4(probe.getIp_addr());
                                deviceScan.setDevice_type(probe.getDevice_type());

                                params.clear();
                                params.put("likeIp", "%" + deviceScan.getDevice_ipv4() + "%");
                                List<Arp> arps = arpService.selectObjByMap(params);
                                if(arps.size() > 0){
                                    Arp arp = arps.get(0);
                                    deviceScan.setMac(arp.getMac());
                                    deviceScan.setMacVendor(arp.getMacVendor());
                                    deviceScan.setDevice_product(arp.getMacVendor());
                                    deviceScan.setDevice_ipv6(arp.getIpv6());
                                }

                                deviceScanService.insert(deviceScan);

                                deleteSet.add(probe.getIp_addr());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                if(deleteSet.size() > 0){
                    for (String ip_addr : deleteSet) {
                        try {
                            this.deleteProbe(ip_addr, probeService);
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

    public void deleteProbe(String ip_addr, IProbeService probeService) {
        if (StringUtil.isNotEmpty(ip_addr)) {
            Map params = new HashMap();
            params.put("ip_addr", ip_addr);
            List<Probe> deleteProbes = probeService.selectObjByMap(params);
            if (deleteProbes.size() > 0) {
                for (Probe deleteProbe : deleteProbes) {
                    try {
                        probeService.delete(deleteProbe.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
