package com.metoo.sqlite.gather.factory.gather;

import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.gather.pool.GatherDataThreadPool;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollector;
import com.metoo.sqlite.gather.strategy.other.DeviceCollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv4CollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.IDeviceService;
import com.metoo.sqlite.service.Ipv4Service;
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
        log.info("get switch Start......");

        try {
            IDeviceService deviceService = (IDeviceService) ApplicationContextUtils.getBean("deviceServiceImpl");
            PyExecUtils pyExecUtils = (PyExecUtils) ApplicationContextUtils.getBean("pyExecUtils");

            List<Device> deviceList = deviceService.selectObjByMap(null);

            if(deviceList.size() > 0){

//                lock.lock();
                try {

                    CountDownLatch latch = new CountDownLatch(deviceList.size());

                    for (Device device : deviceList) {
                        Context context = new Context();
                        context.setCreateTime(DateTools.getCreateTime());
                        context.setEntity(device);
                        context.setLatch(latch);

                        DeviceCollectionStrategy collectionStrategy = new DeviceCollectionStrategy(pyExecUtils, deviceService);
                        DataCollector dataCollector = new DataCollector(context, collectionStrategy);
                        GatherDataThreadPool.getInstance().addThread(dataCollector);
                    }
                    try {

                        latch.await();// 等待结果线程池线程执行结束

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                finally {
//                    lock.unlock();
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("get switch......" + (System.currentTimeMillis() - time));
    }
}
