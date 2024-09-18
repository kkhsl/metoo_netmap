package com.metoo.sqlite.gather.factory.gather.thread;

import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.service.IArpService;
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
public class GatherArp implements Gather {

    private final Lock lock = new ReentrantLock();

    @Override
    public void executeMethod() {
        Long time = System.currentTimeMillis();
        log.info("Arp Start......");

        IArpService arpService = (IArpService) ApplicationContextUtils.getBean("arpServiceImpl");

        try {

            arpService.gather();

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("arp End......" + (System.currentTimeMillis() - time));
    }

}
