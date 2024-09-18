package com.metoo.sqlite.gather.factory.gather.thread;

import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv4CollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv4CollectionStrategyTest;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.Ipv4Service;
import com.metoo.sqlite.service.impl.Ipv4ServiceImpl;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.date.DateTools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class TestThreadExec {

    public static void main(String[] args) {
        gatherIpv4();

    }



    // 无法注入bean
    public static void gatherIpv4(){
        Device device = new Device();
        device.setIp("192.168.6.1");
        device.setDeviceTypeAlias("switch");
        device.setDeviceVendorAlias("h3c");
        device.setLoginName("metoo");
        device.setLoginPassword("metoo89745000");
        device.setLoginType("ssh");
        device.setLoginPort("22");

        Device device2 = new Device();
        device2.setIp("192.168.6.1");
        device2.setDeviceTypeAlias("switch");
        device2.setDeviceVendorAlias("h3c");
        device2.setLoginName("metoo");
        device2.setLoginPassword("metoo89745000");
        device2.setLoginType("ssh");
        device2.setLoginPort("22");


        Device device3 = new Device();
        device3.setIp("192.168.6.1");
        device3.setDeviceTypeAlias("switch");
        device3.setDeviceVendorAlias("h3c");
        device3.setLoginName("metoo");
        device3.setLoginPassword("metoo89745000");
        device3.setLoginType("ssh");
        device3.setLoginPort("22");

        List<Device> deviceList = new ArrayList<>();
        deviceList.add(device);
        deviceList.add(device2);
        deviceList.add(device3);


        CountDownLatch latch = Global.isConcurrent ? new CountDownLatch(deviceList.size()) : null;

        PyExecUtils pyExecUtils = (PyExecUtils) ApplicationContextUtils.getBean("pyExecUtils");

        Ipv4Service ipv4Service = (Ipv4Service) ApplicationContextUtils.getBean("ipv4ServiceImpl");

        ipv4Service.deleteTableGather();

        for (Device device1 : deviceList) {
            Context context = new Context();
            context.setCreateTime(DateTools.getCreateTime());
            context.setLatch(latch);
            context.setEntity(device1);
            Ipv4CollectionStrategyTest collectionStrategy = new Ipv4CollectionStrategyTest(ipv4Service,
                    pyExecUtils);
            ExecThread.exec(collectionStrategy, context);
        }

        if(Global.isConcurrent && latch != null){
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ipv4Service.copyGatherData();
    }


}
