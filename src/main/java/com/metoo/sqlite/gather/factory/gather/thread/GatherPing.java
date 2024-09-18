package com.metoo.sqlite.gather.factory.gather.thread;

import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Subnet;
import com.metoo.sqlite.gather.pool.GatherDataThreadPool;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollector;
import com.metoo.sqlite.gather.strategy.other.PingCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.*;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.date.DateTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
public class GatherPing implements Gather {

    private final Lock lock = new ReentrantLock();

    @Override
    public void executeMethod() {
        Long time = System.currentTimeMillis();
        log.info("Ping start=========");

        try {
            PyExecUtils pyExecUtils = (PyExecUtils) ApplicationContextUtils.getBean("pyExecUtils");
            Set<Subnet> set = getSubnet();

                if(set.size() > 0){

                try {
                    CountDownLatch latch = Global.isConcurrent ? new CountDownLatch(set.size()) : null;
                    for (Subnet subnet : set) {
                        Context context = new Context();
                        context.setCreateTime(DateTools.getCreateTime());
                        context.setEntity(subnet);
                        context.setLatch(latch);

                        PingCollectionStrategy collectionStrategy = new PingCollectionStrategy(pyExecUtils);
                        ExecThread.exec(collectionStrategy, context);
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
        log.info("Ping end......" + (System.currentTimeMillis() - time));
    }

    ISubnetService subnetService = (ISubnetService) ApplicationContextUtils.getBean("subnetServiceImpl");

    public Set<Subnet> getSubnet(){

        Set<Subnet> set = new HashSet<>();
        List<Subnet> parentList = this.subnetService.selectSubnetByParentId(null);
        if (parentList.size() > 0) {
            for (Subnet subnet : parentList) {
                this.genericSubnet(subnet, set);
            }
        }
        return set;
    }

    public List<Subnet> genericSubnet(Subnet subnet, Set<Subnet> set) {
        List<Subnet> subnets = this.subnetService.selectSubnetByParentId(subnet.getId());
        if (subnets.size() > 0) {
            for (Subnet child : subnets) {
                List<Subnet> subnetList = genericSubnet(child, set);
                if (subnetList.size() > 0) {
                    child.setSubnetList(subnetList);
                }else{
                    set.add(child);
                }
            }
        }else{
            set.add(subnet);
        }
        return subnets;
    }
}
