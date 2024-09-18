package com.metoo.sqlite.gather.factory.gather.thread;

import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.gather.pool.GatherDataThreadPool;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollector;
import com.metoo.sqlite.gather.strategy.other.DeviceCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.IDeviceService;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.date.DateTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-24 16:37
 */
@Slf4j
@Component
public class GatherGetSwith implements Gather {

    private final Lock lock = new ReentrantLock();

    @Override
    public void executeMethod() {
        Long time = System.currentTimeMillis();
        log.info("Get switch start......");

        try {
            IDeviceService deviceService = (IDeviceService) ApplicationContextUtils.getBean("deviceServiceImpl");
            PyExecUtils pyExecUtils = (PyExecUtils) ApplicationContextUtils.getBean("pyExecUtils");

            List<Device> deviceList = deviceService.selectObjByMap(null);

            if(deviceList.size() > 0){
                try {

                    CountDownLatch latch = Global.isConcurrent ? new CountDownLatch(deviceList.size()) : null;

                    for (Device device : deviceList) {
                        Context context = new Context();
                        context.setCreateTime(DateTools.getCreateTime());
                        context.setEntity(device);
                        context.setLatch(latch);

                        if((device.getDeviceTypeAlias().equals("firewall") || device.getDeviceTypeAlias().equals("linux"))
                                && (device.getDeviceVendorAlias().equals("huawei") || device.getDeviceVendorAlias().equals("h3c"))
                                && (device.getLoginType().equals("ssh") || device.getLoginType().equals("telnet")) ){
                            if(latch != null){
                                latch.countDown();
                            }
                        }else if(device.getLoginType().equals("api") && device.getDeviceTypeAlias().equals("api")
                                && device.getDeviceVendorAlias().equals("pana")){
                            if(latch != null){
                                latch.countDown();
                            }
                        }else {
                            DeviceCollectionStrategy collectionStrategy = new DeviceCollectionStrategy(pyExecUtils, deviceService);
                            ExecThread.exec(collectionStrategy, context);
                        }
                    }

                    if(Global.isConcurrent && latch != null){
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Get switch end ......" + (System.currentTimeMillis() - time));
    }
}
