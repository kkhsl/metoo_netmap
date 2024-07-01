package com.metoo.sqlite.gather.factory.gather;

import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.gather.pool.GatherDataThreadPool;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollector;
import com.metoo.sqlite.gather.strategy.other.Ipv4CollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv6SubnetCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.IArpService;
import com.metoo.sqlite.service.ISubnetIpv6Service;
import com.metoo.sqlite.service.Ipv4Service;
import com.metoo.sqlite.utils.date.DateTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
public class GatherSubentIpv6 implements Gather {

    private final Lock lock = new ReentrantLock();

    @Override
    public void executeMethod() {
        Long time = System.currentTimeMillis();
        log.info("subnet ipv6 Start......");


        ISubnetIpv6Service subnetIpv6Service = (ISubnetIpv6Service) ApplicationContextUtils.getBean("subnetIpv6ServiceImpl");
        PyExecUtils pyExecUtils = (PyExecUtils) ApplicationContextUtils.getBean("pyExecUtils");

        try {
            subnetIpv6Service.deleteTable();

            Context context = new Context();
            context.setCreateTime(DateTools.getCreateTime());

            Ipv6SubnetCollectionStrategy collectionStrategy = new Ipv6SubnetCollectionStrategy(pyExecUtils,
                    subnetIpv6Service);
            DataCollector dataCollector = new DataCollector(context, collectionStrategy);
            GatherDataThreadPool.getInstance().addThread(dataCollector);
        } catch (Exception e) {
            e.printStackTrace();
        }


        log.info("subnet ipv6 end......" + (System.currentTimeMillis() - time));
    }

}
