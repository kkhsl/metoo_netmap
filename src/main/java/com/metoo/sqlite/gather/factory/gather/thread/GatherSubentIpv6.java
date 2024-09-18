package com.metoo.sqlite.gather.factory.gather.thread;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.SubnetIpv6;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.ISubnetIpv6Service;
import com.metoo.sqlite.utils.Global;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

        log.info("Subnet ipv6 Start......");

        ISubnetIpv6Service subnetIpv6Service = (ISubnetIpv6Service) ApplicationContextUtils.getBean("subnetIpv6ServiceImpl");
        PyExecUtils pyExecUtils = (PyExecUtils) ApplicationContextUtils.getBean("pyExecUtils");


//        try {
//            subnetIpv6Service.deleteTable();
//
//            CountDownLatch latch = new CountDownLatch(1);
//
//            Context context = new Context();
//            context.setCreateTime(DateTools.getCreateTime());
//            context.setLatch(latch);
//
//            Ipv6SubnetCollectionStrategy collectionStrategy = new Ipv6SubnetCollectionStrategy(pyExecUtils,
//                    subnetIpv6Service);
//            DataCollector dataCollector = new DataCollector(context, collectionStrategy);
//            GatherDataThreadPool.getInstance().addThread(dataCollector);
//
//            try {
//
//                latch.await();// 等待结果线程池线程执行结束
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        subnetIpv6Service.deleteTable();
        try {
            PyCommandBuilder pyCommand = new PyCommandBuilder();

            pyCommand.setVersion(Global.py_name);
            pyCommand.setPath(Global.PYPATH);

            pyCommand.setName("subnetipv6.pyc");
            try {
                String result = pyExecUtils.exec(pyCommand);

                if (StringUtil.isNotEmpty(result)) {
                    // 递归ipv6网段数据
                    if(!"".equals(result)){
                        JSONObject obj = JSONObject.parseObject(result);
                        if(obj != null){
                            generic(obj, null, subnetIpv6Service);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("Subnet ipv6 end......");

    }


    public void generic(JSONObject obj, Integer parentId,  ISubnetIpv6Service subnetIpv6Service){
        if(obj != null){
            for (String key : obj.keySet()) {
                if(obj.get(key) != null){
                    SubnetIpv6 subnetIpv6 = new SubnetIpv6();
                    subnetIpv6.setIp(key.split("/")[0]);
                    subnetIpv6.setMask(Integer.parseInt(key.split("/")[1]));
                    subnetIpv6.setParentId(parentId);

                    boolean i = subnetIpv6Service.save(subnetIpv6);

                    JSONArray childs = JSONObject.parseArray(obj.getString(key));
                    if(childs.size() > 0){
                        for (Object ele : childs) {
                            if(ele instanceof String){
                                SubnetIpv6 child = new SubnetIpv6();
                                child.setIp(String.valueOf(ele).split("/")[0]);
                                child.setMask(Integer.parseInt(String.valueOf(ele).split("/")[1]));
                                child.setParentId(subnetIpv6.getId());

                                subnetIpv6Service.save(child);
                            } if(ele instanceof JSONObject){
                                JSONObject child = JSONObject.parseObject(JSONObject.toJSONString(ele));
                                generic(child, subnetIpv6.getId(), subnetIpv6Service);
                            }
                        }
                    }
                }
            }
        }
    }

}
