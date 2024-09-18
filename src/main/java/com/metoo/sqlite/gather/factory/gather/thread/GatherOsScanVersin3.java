package com.metoo.sqlite.gather.factory.gather.thread;

import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Arp;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.other.OsScannerCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.*;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.StringUtils;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.utils.file.DataFileWrite;
import com.metoo.sqlite.utils.file.FileToDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-24 16:37
 */
@Slf4j
@Component
public class GatherOsScanVersin3 implements Gather {

    public static <T> List<List<T>> splitList(List<T> list, int numOfSubLists) {
        List<List<T>> subLists = new ArrayList<>(numOfSubLists);
        int size = list.size();
        int subListSize = (int) Math.ceil((double) size / numOfSubLists);

        for (int i = 0; i < numOfSubLists; i++) {
            int fromIndex = i * subListSize;
            int toIndex = Math.min(fromIndex + subListSize, size);
            if (fromIndex < toIndex) {
                subLists.add(new ArrayList<>(list.subList(fromIndex, toIndex)));
            } else {
                subLists.add(new ArrayList<>()); // 如果没有更多元素，添加一个空子集合
            }
        }
        return subLists;
    }

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= 17; i++) {
            list.add(i);
        }

        List<List<Integer>> result = splitList(list, 5);

//        for (int i = 0; i < result.size(); i++) {
//            System.out.println("Sublist " + (i + 1) + ": " + result.get(i));
//        }

        AtomicInteger index = new AtomicInteger();

        for (List<Integer> integers : result) {

            int i = index.incrementAndGet();

            for (Integer integer : integers) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                System.out.println("Sublist " + String.valueOf(i)
//                        + " ThreadName" + Thread.currentThread().getName()
//                        + ": " + integer);
            }
        }

    }

    @Override
    public void executeMethod() {
        Long time = System.currentTimeMillis();
        log.info("os scan Start......");

        try {
            IArpService arpService = (IArpService) ApplicationContextUtils.getBean("arpServiceImpl");
            IUnreachService unreachService = (IUnreachService) ApplicationContextUtils.getBean("unreachServiceImpl");

            try {
                unreachService.deleteTable();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Integer number = 1;
            // 清空result_overlap.txt文件
            try {
                DataFileWrite.clearFile(number);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Map params = new HashMap();
            params.put("isNotIp", "0.0.0.0");
            List<Arp> arps = arpService.selectObjByMap(null);
            if(arps.size() > 0){
                this.osScan(arps, String.valueOf(number));

            }

        } catch (Exception e) {
            e.printStackTrace();
    }
        log.info("os scan......" + (System.currentTimeMillis() - time));
    }


    public void osScan(List<Arp> arps, String path_suffix){

        if(arps.size() > 0){

            PyExecUtils pyExecUtils = (PyExecUtils) ApplicationContextUtils.getBean("pyExecUtils");
            IProbeService probeService = (IProbeService) ApplicationContextUtils.getBean("probeServiceImpl");
            FileToDatabase fileToDatabase = (FileToDatabase) ApplicationContextUtils.getBean("fileToDatabase");
            IDeviceService deviceService = (IDeviceService) ApplicationContextUtils.getBean("deviceServiceImpl");

            Map params = new HashMap();
            for (Arp arp : arps) {
                if(StringUtils.isNotEmpty(arp.getIp())){
                    params.put("ip_addr", arp.getIp());
                    List<Probe> probes = probeService.selectObjByMap(params);
                    if(probes.size() > 0){

                        // 筛选最小端口
                        // 使用 Stream API 和自定义比较器找出最小端口的记录
//                        Optional<Probe> minPortProbeOptional = probes.stream()
//                                .min(Comparator.comparingInt(probe -> Integer.parseInt(probe.getPort_num())));
//                        Probe obj = minPortProbeOptional.orElse(null);
//                            Probe obj = probes.get(0);
                    for (Probe obj : probes) {
                            if(obj != null){
                                try {
                                    Context context = new Context();
                                    context.setCreateTime(DateTools.getCreateTime());
                                    context.setEntity(obj);
                                    context.setPath(Global.os_scanner + path_suffix);
                                    OsScannerCollectionStrategy collectionStrategy = new OsScannerCollectionStrategy(pyExecUtils);
                                    collectionStrategy.collectData(context);
                                    // 读取result文件
                                    Device device = null;
                                    params.clear();
                                    params.put("uuid", arp.getDeviceUuid());
                                    List<Device> devices = deviceService.selectObjByMap(params);
                                    if(devices.size() > 0){
                                        device = devices.get(0);
                                    }
                                    fileToDatabase.write(path_suffix, device);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                         }
                    }else{
//                        Unreach unreach = new Unreach();
//                        unreach.setIp_addr(arp.getIp());
//                        unreach.setMac_addr(arp.getMac());
//                        if(StringUtil.isNotEmpty(arp.getMac())){
//                            params.clear();
//                            params.put("mac", MacUtils.getMac(arp.getMac()));
//                            List<MacVendor> macVendorList = macVendorService.selectObjByMap(params);
//                            if(macVendorList.size() > 0){
//                                MacVendor macVendor = macVendorList.get(0);
//                                unreach.setMac_vendor(macVendor.getVendor());
//                            }
//                        }
//                        unreachService.insert(unreach);

                        Probe probe = new Probe();
                        probe.setIp_addr(arp.getIp());
                        probe.setPort_num("2");
                        Context context = new Context();
                        context.setCreateTime(DateTools.getCreateTime());
                        context.setEntity(probe);
                        context.setPath(Global.os_scanner + path_suffix);
                        OsScannerCollectionStrategy collectionStrategy = new OsScannerCollectionStrategy(pyExecUtils);
                        collectionStrategy.collectData(context);
                        // 读取result文件
                        Device device = null;
                        params.clear();
                        params.put("uuid", arp.getDeviceUuid());
                        List<Device> devices = deviceService.selectObjByMap(params);
                        if(devices.size() > 0){
                            device = devices.get(0);
                        }
                        fileToDatabase.write(path_suffix, device);
                    }
                }
            }
        }
    }
}
