package com.metoo.sqlite.gather.factory.gather.thread;

import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.gather.pool.GatherDataThreadPool;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollector;
import com.metoo.sqlite.gather.strategy.other.PortCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.IDeviceService;
import com.metoo.sqlite.service.IPortIpv4Service;
import com.metoo.sqlite.service.IPortIpv6Service;
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
public class GatherPort implements Gather {

//    private final IDeviceService deviceService;
//    private final PortIpv4Service ipv4PortService;
//    private final PyExecUtils pyExecUtils;
//
//    @Autowired // 通过构造函数注入 DataService
//    public PortGather(IDeviceService deviceService, PortIpv4Service ipv4PortService, PyExecUtils pyExecUtils){
//        this.deviceService = deviceService;
//        this.ipv4PortService = ipv4PortService;
//        this.pyExecUtils = pyExecUtils;
//    }

    private final Lock lock = new ReentrantLock();

    @Override
    public void executeMethod() {
        Long time = System.currentTimeMillis();
        log.info("Port ipv4/ipv6 Start......");
        try {
            IDeviceService deviceService = (IDeviceService) ApplicationContextUtils.getBean("deviceServiceImpl");
            IPortIpv4Service portIpv4Service = (IPortIpv4Service) ApplicationContextUtils.getBean("portIpv4ServiceImpl");
            IPortIpv6Service portIpv6Service = (IPortIpv6Service) ApplicationContextUtils.getBean("portIpv6ServiceImpl");
            PyExecUtils pyExecUtils = (PyExecUtils) ApplicationContextUtils.getBean("pyExecUtils");

            portIpv4Service.deleteTableGather();

            portIpv6Service.deleteTableGather();


            // 获取所有设备列表

            List<Device> deviceList = deviceService.selectObjByMap(null);

            if(deviceList.size() > 0){

                try {

                    CountDownLatch latch = Global.isConcurrent ? new CountDownLatch(deviceList.size()) : null;
                    for (Device device : deviceList) {

                        Context context = new Context();
                        context.setCreateTime(DateTools.getCreateTime());
                        context.setEntity(device);
                        context.setLatch(latch);

                        if(device.getLoginType().equals("api") && device.getDeviceTypeAlias().equals("api")
                                && device.getDeviceVendorAlias().equals("pana")){
                            if(latch != null){
                                latch.countDown();
                            }
                        }else if((device.getLoginType().equals("ssh") || device.getLoginType().equals("telnet"))/* &&
                                device.getDeviceTypeAlias().equals("router") || device.getDeviceTypeAlias().equals("switch")*/){

                            PortCollectionStrategy collectionStrategy = new PortCollectionStrategy(portIpv4Service, portIpv6Service,
                                    pyExecUtils);
                            ExecThread.exec(collectionStrategy, context);
                        }else{
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
                    // 复制数据到端口ip表
                    portIpv4Service.copyGatherData();
                    portIpv6Service.copyGatherData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Port ipv4/ipv6 end......" + (System.currentTimeMillis() - time));
    }
}
