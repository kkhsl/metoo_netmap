package com.metoo.sqlite.gather.factory.gather;

import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.GatewayInfo;
import com.metoo.sqlite.gather.pool.GatherDataThreadPool;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollector;
import com.metoo.sqlite.gather.strategy.other.GatewayOperatorCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.IGatewayInfoService;
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
 *
 * 作废
 */
@Slf4j
@Component
public class GatherGatewayOperator implements Gather {

    private final Lock lock = new ReentrantLock();

    @Override
    public void executeMethod() {
        Long time = System.currentTimeMillis();
        log.info("gateway operator......");

        try {
            IGatewayInfoService gatewayInfoService = (IGatewayInfoService) ApplicationContextUtils.getBean("gatewayInfoServiceImpl");
            PyExecUtils pyExecUtils = (PyExecUtils) ApplicationContextUtils.getBean("pyExecUtils");

            List<GatewayInfo> gatewayInfoList = gatewayInfoService.selectObjByMap(null);

            if(gatewayInfoList.size() > 0){

//                lock.lock();
                try {
                    gatewayInfoService.deleteTableGather();

                    CountDownLatch latch = new CountDownLatch(gatewayInfoList.size());

                    for (GatewayInfo gatewayInfo : gatewayInfoList) {

                        Context context = new Context();
                        context.setCreateTime(DateTools.getCreateTime());
                        context.setEntity(gatewayInfo);
                        context.setLatch(latch);

                        GatewayOperatorCollectionStrategy collectionStrategy =
                                new GatewayOperatorCollectionStrategy(gatewayInfoService,
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
                    gatewayInfoService.copyGatherData();

                } catch (Exception e) {
                    e.printStackTrace();
                }
//                finally {
//                    lock.unlock();
////                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("gateway operator......" + (System.currentTimeMillis() - time));
    }
}
