package com.metoo.sqlite.gather.factory.gather.thread;

import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.gather.pool.GatherDataThreadPool;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollector;
import com.metoo.sqlite.gather.strategy.other.Ipv6ByVendorCollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv6CollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv6MuyunCollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv6OanabitCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.IDeviceService;
import com.metoo.sqlite.service.Ipv6Service;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.utils.muyun.MuyunService;
import com.metoo.sqlite.utils.panabit.PanabitSendMsgUtils.PanabitService;
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
public class GatherIpv6Panabit implements Gather {

    private final Lock lock = new ReentrantLock();

    @Override
    public void executeMethod() {
        Long time = System.currentTimeMillis();
        log.info("Ipv6 Start......");

        try {
            IDeviceService deviceService = (IDeviceService) ApplicationContextUtils.getBean("deviceServiceImpl");
            Ipv6Service ipv6Service = (Ipv6Service) ApplicationContextUtils.getBean("ipv6ServiceImpl");
            PanabitService panabitService = (PanabitService) ApplicationContextUtils.getBean("panabitService");
            PyExecUtils pyExecUtils = (PyExecUtils) ApplicationContextUtils.getBean("pyExecUtils");
            MuyunService muyunService = (MuyunService) ApplicationContextUtils.getBean("muyunService");

            ipv6Service.deleteTableGather();

            List<Device> deviceList = deviceService.selectObjByMap(null);

            if (deviceList.size() > 0) {
                CountDownLatch latch = Global.isConcurrent ? new CountDownLatch(deviceList.size()) : null;
                for (Device device : deviceList) {
                    Context context = new Context();
                    context.setCreateTime(DateTools.getCreateTime());
                    context.setLatch(latch);
                    context.setEntity(device);

                    if (device.getLoginType().equals("api") && device.getDeviceTypeAlias().equals("api")
                            && device.getDeviceVendorAlias().equals("muyun")) {
                        Ipv6MuyunCollectionStrategy collectionStrategy = new Ipv6MuyunCollectionStrategy(ipv6Service,
                                muyunService);
                        ExecThread.exec(collectionStrategy, context);
                    } else if (device.getLoginType().equals("api") && device.getDeviceTypeAlias().equals("api")
                            && device.getDeviceVendorAlias().equals("pana")) {
                        Ipv6OanabitCollectionStrategy collectionStrategy = new Ipv6OanabitCollectionStrategy(ipv6Service,
                                panabitService);
                        ExecThread.exec(collectionStrategy, context);
                    } else if ((device.getLoginType().equals("ssh") || device.getLoginType().equals("telnet")) &&
                            device.getDeviceTypeAlias().equals("router") || device.getDeviceTypeAlias().equals("switch")) {

                        Ipv6CollectionStrategy collectionStrategy = new Ipv6CollectionStrategy(ipv6Service,
                                pyExecUtils);

                        ExecThread.exec(collectionStrategy, context);

                    } else if ((device.getDeviceTypeAlias().equals("firewall") || device.getDeviceTypeAlias().equals("linux"))

                            && (device.getDeviceVendorAlias().equals("huawei") || device.getDeviceVendorAlias().equals("h3c")
                            || device.getDeviceVendorAlias().equals("sanfor")
                            || device.getDeviceVendorAlias().equals("sanforip"))

                            || device.getDeviceVendorAlias().equals("stone")
                            || device.getDeviceVendorAlias().equals("leadsec")

                            && (device.getLoginType().equals("ssh") || device.getLoginType().equals("telnet"))) {

                        Ipv6ByVendorCollectionStrategy collectionStrategy = new Ipv6ByVendorCollectionStrategy(ipv6Service,
                                pyExecUtils);
                        ExecThread.exec(collectionStrategy, context);
                    } else {
                        if(latch != null){
                            latch.countDown();
                        }
                    }
                }
                if(Global.isConcurrent && latch != null){
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            // 复制数据到端口ip表
            ipv6Service.copyGatherData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Ipv6 End......" + (System.currentTimeMillis() - time));
    }
}
