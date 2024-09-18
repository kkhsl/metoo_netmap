package com.metoo.sqlite.gather.factory.gather.thread;

import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.Arp;
import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.other.OsScannerCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.IArpService;
import com.metoo.sqlite.service.IProbeService;
import com.metoo.sqlite.utils.StringUtils;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.utils.file.FileToDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-24 16:37
 */
@Slf4j
@Component
public class GatherOsScan implements Gather {

    @Override
    public void executeMethod() {
        Long time = System.currentTimeMillis();
        log.info("os scan Start......");

        try {
            PyExecUtils pyExecUtils = (PyExecUtils) ApplicationContextUtils.getBean("pyExecUtils");
            IProbeService probeService = (IProbeService) ApplicationContextUtils.getBean("probeServiceImpl");
            IArpService arpService = (IArpService) ApplicationContextUtils.getBean("arpServiceImpl");
            FileToDatabase fileToDatabase = (FileToDatabase) ApplicationContextUtils.getBean("fileToDatabase");
            List<Arp> arps = arpService.selectObjByMap(null);
            if(arps.size() > 0){
                Map params = new HashMap();
                for (Arp arp : arps) {
                    if(StringUtils.isNotEmpty(arp.getIp())){
                        params.put("ip_addr", arp.getIp());
                        List<Probe> probes = probeService.selectObjByMap(params);
                        if(probes.size() > 0){

                            // 筛选最小端口
                            // 使用 Stream API 和自定义比较器找出最小端口的记录
                            Optional<Probe> minPortProbeOptional = probes.stream()
                                    .min(Comparator.comparingInt(probe -> Integer.parseInt(probe.getPort_num())));
                            Probe obj = minPortProbeOptional.orElse(null);
                            if(obj != null){
                                try {
                                    Context context = new Context();
                                    context.setCreateTime(DateTools.getCreateTime());
                                    context.setEntity(obj);
                                    OsScannerCollectionStrategy collectionStrategy = new OsScannerCollectionStrategy(pyExecUtils);
                                    collectionStrategy.collectData(context);
                                    // 读取result文件
                                    fileToDatabase.write("");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

//                            }
                        }else{
//                            Probe probe = new Probe();
//                            probe.setIp_addr(arp.getIp());
//                            probe.setPort_num("2");
//                            Context context = new Context();
//                            context.setCreateTime(DateTools.getCreateTime());
//                            context.setEntity(probe);
//                            OsScannerCollectionStrategy collectionStrategy = new OsScannerCollectionStrategy(pyExecUtils);
//                            collectionStrategy.collectData(context);
//                            // 读取result文件
//                            fileToDatabase.write("insert");
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("os scan......" + (System.currentTimeMillis() - time));
    }

}
