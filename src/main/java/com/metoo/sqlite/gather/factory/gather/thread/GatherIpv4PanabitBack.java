package com.metoo.sqlite.gather.factory.gather.thread;

import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.gather.pool.GatherDataThreadPool;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollector;
import com.metoo.sqlite.gather.strategy.other.Ipv4ByVendorCollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv4CollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv4MuyunCollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv4PanabitCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.IDeviceService;
import com.metoo.sqlite.service.IPanaSwitchService;
import com.metoo.sqlite.service.Ipv4Service;
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
public class GatherIpv4PanabitBack implements Gather {

    private final Lock lock = new ReentrantLock();

    @Override
    public void executeMethod() {

        Long time = System.currentTimeMillis();

        log.info("Ipv4 start......");

        try {

            IDeviceService deviceService = (IDeviceService) ApplicationContextUtils.getBean("deviceServiceImpl");
            Ipv4Service ipv4Service = (Ipv4Service) ApplicationContextUtils.getBean("ipv4ServiceImpl");
            PanabitService panabitService = (PanabitService) ApplicationContextUtils.getBean("panabitService");
            PyExecUtils pyExecUtils = (PyExecUtils) ApplicationContextUtils.getBean("pyExecUtils");
            IPanaSwitchService panaSwitchService = (IPanaSwitchService) ApplicationContextUtils.getBean("panaSwitchServiceImpl");
            MuyunService muyunService = (MuyunService) ApplicationContextUtils.getBean("muyunService");

            ipv4Service.deleteTableGather();

            List<Device> deviceList = deviceService.selectObjByMap(null);

            if (deviceList.size() > 0) {
                try {
                    CountDownLatch latch = new CountDownLatch(deviceList.size());

                    for (Device device : deviceList) {
                        Context context = new Context();
                        context.setCreateTime(DateTools.getCreateTime());
                        context.setLatch(latch);
                        context.setEntity(device);

                        if (device.getLoginType().equals("api")
                                && device.getDeviceTypeAlias().equals("api")
                                && device.getDeviceVendorAlias().equals("muyun")) {

                            Ipv4MuyunCollectionStrategy collectionStrategy = new Ipv4MuyunCollectionStrategy(ipv4Service,
                                    muyunService);

                            DataCollector dataCollector = new DataCollector(context, collectionStrategy);

                            GatherDataThreadPool.getInstance().addThread(dataCollector);

                        } else if (device.getLoginType().equals("api")
                                && device.getDeviceTypeAlias().equals("api")
                                && device.getDeviceVendorAlias().equals("pana")) {

                            Ipv4PanabitCollectionStrategy collectionStrategy = new Ipv4PanabitCollectionStrategy(ipv4Service,
                                    panabitService, panaSwitchService);

                            DataCollector dataCollector = new DataCollector(context, collectionStrategy);

                            GatherDataThreadPool.getInstance().addThread(dataCollector);

                        } else if ((device.getLoginType().equals("ssh") || device.getLoginType().equals("telnet")) &&
                                device.getDeviceTypeAlias().equals("router") || device.getDeviceTypeAlias().equals("switch")) {

                            Ipv4CollectionStrategy collectionStrategy = new Ipv4CollectionStrategy(ipv4Service,
                                    pyExecUtils);
                            DataCollector dataCollector = new DataCollector(context, collectionStrategy);
                            GatherDataThreadPool.getInstance().addThread(dataCollector);

                        } else if (
                                (device.getDeviceTypeAlias().equals("firewall") || device.getDeviceTypeAlias().equals("linux"))
                                        &&
                                        (device.getDeviceVendorAlias().equals("huawei")
                                                || device.getDeviceVendorAlias().equals("h3c")
                                                || device.getDeviceVendorAlias().equals("sanfor")
                                                || device.getDeviceVendorAlias().equals("sanforip")
                                        )
                                        || device.getDeviceVendorAlias().equals("stone")
                                        || device.getDeviceVendorAlias().equals("leadsec")

                                        && (device.getLoginType().equals("ssh") || device.getLoginType().equals("telnet"))) {

                            Ipv4ByVendorCollectionStrategy collectionStrategy = new Ipv4ByVendorCollectionStrategy(ipv4Service,
                                    pyExecUtils);
                            DataCollector dataCollector = new DataCollector(context, collectionStrategy);
                            GatherDataThreadPool.getInstance().addThread(dataCollector);
                        } else {
                            latch.countDown();
                        }

                    }

                    try {
                        latch.await();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    ipv4Service.copyGatherData();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("ipv4 End......" + (System.currentTimeMillis() - time));
    }
}
