package com.metoo.sqlite.gather.factory.gather;

import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.DeviceScan;
import com.metoo.sqlite.entity.Probe;
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
        log.info("metoo_scan Start......");

        try {
            IDeviceScanService deviceScanService = (IDeviceScanService) ApplicationContextUtils.getBean("deviceScanServiceImpl");
            IProbeService probeService = (IProbeService) ApplicationContextUtils.getBean("probeServiceImpl");

            deviceScanService.deleteTable();

            Map params = new HashMap();

            params.clear();
//            params.put("ip_addr", "192.168.100.4");
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

                    String os = "";

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

                        String vendor = probe.getVendor();
                        String os_gen = probe.getOs_gen();

                        if(StringUtil.isNotEmpty(vendor)){
                            os = vendor + " " + os_gen;
                        }else if(StringUtil.isNotEmpty(os_gen)){
                            os = os_gen;
                        }

                        String createTime = DateTools.getCreateTime();
                        boolean flag = false;

                        if (probe.getDevice_type() != null && ("交换机".equals(probe.getDevice_type())
                                || "路由器".equals(probe.getDevice_type()))) {
                            try {
                                deviceScan.setCreateTime(createTime);
                                deviceScan.setDevice_ipv4(probe.getIp_addr());

//                                deviceScan.setDevice_product(probe.getPort_service_product());

                                deviceScan.setDevice_product(os.trim());


                                deviceScan.setDevice_type(probe.getDevice_type());
                                flag = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (probe.getPort_service_product() != null
                                && (probe.getPort_service_product().contains("switch")
                                || probe.getPort_service_product().contains("router"))) {
                            // 写入deviceScan
                            try {
                                deviceScan.setCreateTime(createTime);
                                deviceScan.setDevice_ipv4(probe.getIp_addr());
//                                deviceScan.setDevice_product(probe.getPort_service_product());

                                deviceScan.setDevice_product(os.trim());


                                deviceScan.setDevice_type(probe.getDevice_type());
                                flag = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (probe.getPort_num() != null
                                && probe.getPort_num().equals("23")) {
                            // 写入deviceScan
                            try {
                                deviceScan.setCreateTime(createTime);
                                deviceScan.setDevice_ipv4(probe.getIp_addr());
//                                deviceScan.setDevice_product(probe.getPort_service_product());

                                deviceScan.setDevice_product(os.trim());

                                deviceScan.setDevice_type(probe.getDevice_type());
                                flag = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (probe.getTtl() != null && probe.getTtl() > 250) {
                            try {
                                deviceScan.setCreateTime(DateTools.getCreateTime());
                                deviceScan.setDevice_ipv4(probe.getIp_addr());
//                                deviceScan.setDevice_product(probe.getPort_service_product());

                                deviceScan.setDevice_product(os.trim());

                                deviceScan.setDevice_type(probe.getDevice_type());
                                flag = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if(flag){
                            deviceScanService.insert(deviceScan);
                            deleteSet.add(probe.getIp_addr());
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
        log.info("metoo_scan End......" + (System.currentTimeMillis() - time));
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
