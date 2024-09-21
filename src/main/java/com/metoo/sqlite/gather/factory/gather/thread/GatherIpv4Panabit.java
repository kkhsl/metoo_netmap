package com.metoo.sqlite.gather.factory.gather.thread;

import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv4ByVendorCollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv4CollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv4MuyunCollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv4PanabitCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.IDeviceService;
import com.metoo.sqlite.service.IPanaSwitchService;
import com.metoo.sqlite.service.Ipv4Service;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.utils.muyun.MuyunService;
import com.metoo.sqlite.utils.panabit.PanabitSendMsgUtils.PanabitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-24 16:37
 */
@Slf4j
@Component
public class GatherIpv4Panabit implements Gather {

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
                CountDownLatch latch = Global.isConcurrent ? new CountDownLatch(deviceList.size()) : null;

                deviceList.forEach(device -> {
                    Context context = new Context();
                    context.setCreateTime(DateTools.getCreateTime());
                    context.setLatch(latch);
                    context.setEntity(device);
                    DataCollectionStrategy strategy = getCollectionStrategy(device, ipv4Service, pyExecUtils,
                            panabitService, panaSwitchService, muyunService);

                    if (strategy != null) {
                        ExecThread.exec(strategy, context);
                    } else if (latch != null) {
                        latch.countDown();
                    }
                });

                if (Global.isConcurrent && latch != null) {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                ipv4Service.copyGatherData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Ipv4 end ========= " + (System.currentTimeMillis() - time));
    }


    private DataCollectionStrategy getCollectionStrategy(Device device, Ipv4Service ipv4Service, PyExecUtils pyExecUtils,
                                                         PanabitService panabitService, IPanaSwitchService panaSwitchService, MuyunService muyunService) {
        if (device.getLoginType().equals("api") && device.getDeviceTypeAlias().equals("api")
                && device.getDeviceVendorAlias().equals("muyun")) {
            return new Ipv4MuyunCollectionStrategy(ipv4Service, muyunService);
        } else if (device.getLoginType().equals("api") && device.getDeviceTypeAlias().equals("api")
                && device.getDeviceVendorAlias().equals("pana")) {
            return new Ipv4PanabitCollectionStrategy(ipv4Service, panabitService, panaSwitchService);
        } else if ((device.getLoginType().equals("ssh") || device.getLoginType().equals("telnet"))
                && (device.getDeviceTypeAlias().equals("router") || device.getDeviceTypeAlias().equals("switch"))) {
            return new Ipv4CollectionStrategy(ipv4Service, pyExecUtils);
        } else if ((device.getDeviceTypeAlias().equals("firewall") || device.getDeviceTypeAlias().equals("linux"))
                && (device.getDeviceVendorAlias().matches("huawei|h3c|sanfor|sanforip|stone|leadsec|hd"))
                && (device.getLoginType().equals("ssh") || device.getLoginType().equals("telnet"))) {
            return new Ipv4ByVendorCollectionStrategy(ipv4Service, pyExecUtils);
        }
        return null;
    }
}
