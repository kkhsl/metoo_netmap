package com.metoo.sqlite.gather.factory.gather;

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
        log.info("port ipv4/ipv6 Start......");

        try {
            IDeviceService deviceService = (IDeviceService) ApplicationContextUtils.getBean("deviceServiceImpl");
            IPortIpv4Service portIpv4Service = (IPortIpv4Service) ApplicationContextUtils.getBean("portIpv4ServiceImpl");
            IPortIpv6Service portIpv6Service = (IPortIpv6Service) ApplicationContextUtils.getBean("portIpv6ServiceImpl");
            PyExecUtils pyExecUtils = (PyExecUtils) ApplicationContextUtils.getBean("pyExecUtils");

            // 获取所有设备列表

            List<Device> deviceList = deviceService.selectObjByMap(null);

            if(deviceList.size() > 0){

//                lock.lock();
                try {
                    portIpv4Service.deleteTableGather();

                    portIpv6Service.deleteTableGather();

                    CountDownLatch latch = new CountDownLatch(deviceList.size());

                    for (Device device : deviceList) {

                        Context context = new Context();
                        context.setCreateTime(DateTools.getCreateTime());
                        context.setEntity(device);
                        context.setLatch(latch);

                        PortCollectionStrategy collectionStrategy = new PortCollectionStrategy(portIpv4Service, portIpv6Service,
                                pyExecUtils);
                        DataCollector dataCollector = new DataCollector(context, collectionStrategy);
                        GatherDataThreadPool.getInstance().addThread(dataCollector);
                    }
                    try {

                        latch.await();// 等待结果线程池线程执行结束

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 复制数据到端口ip表
                    portIpv4Service.copyGatherData();
                    portIpv6Service.copyGatherData();
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
        log.info("port ipv4/ipv6 End......" + (System.currentTimeMillis() - time));
    }
}
