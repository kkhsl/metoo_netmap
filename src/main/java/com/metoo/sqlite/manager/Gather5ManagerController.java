package com.metoo.sqlite.manager;

import com.metoo.sqlite.entity.GatherSemaphore;
import com.metoo.sqlite.manager.api.ApiService;
import com.metoo.sqlite.manager.utils.ArpUtils;
import com.metoo.sqlite.service.*;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-24 12:50
 */


@Slf4j
@RequestMapping("/admin/gather/5")
@RestController
public class Gather5ManagerController {

    private final ApiService apiService;
    @Autowired
    private IProbeResultService probeResultService;
    @Autowired
    private ISubnetIpv6Service subnetIpv6Service;
    @Autowired
    private IGatewayInfoService gatewayInfoService;
    @Autowired
    private ITerminalService terminalService;
    @Autowired
    private IGatherLogService gatherLogService;
    @Autowired
    private IGatherSemaphoreService gatherSemaphoreService;
    @Autowired
    private IArpService arpService;
    @Value("${AP.URL}")
    private String apUrl;
    @Autowired
    private ArpUtils arpUtils;
    @Autowired
    private ISubnetService subnetService;

    @Autowired
    public Gather5ManagerController(ApiService apiService) {
        this.apiService = apiService;
    }

    private final Lock lock = new ReentrantLock();


    // 使用信号量解决
    @GetMapping("/main")
    public Result main() {

        GatherSemaphore gatherSemaphore = null;
        lock.lock();
        try {
            // 查询采集信号量
            gatherSemaphore = this.gatherSemaphoreService.selectObjByOne();
            if (gatherSemaphore.getSemaphore() == 1) {
                return ResponseUtil.badArgument("正在采集...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        try {
            gatherSemaphore.setSemaphore(1);
            this.gatherSemaphoreService.update(gatherSemaphore);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

                log.info("running 1 .....");
                Thread.sleep(1000);

                log.info("running 2 .....");
                Thread.sleep(1000);

                log.info("running 3 .....");
                Thread.sleep(1000);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                gatherSemaphore.setSemaphore(0);// 采集结束
                this.gatherSemaphoreService.update(gatherSemaphore);
            }

            return ResponseUtil.ok("采集成功");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        finally {
//            lock.unlock();
//        }

    }

}
