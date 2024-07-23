package com.metoo.sqlite.manager;

import com.alibaba.fastjson.JSONObject;
import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.entity.ProbeBody;
import com.metoo.sqlite.entity.ProbeResult;
import com.metoo.sqlite.entity.Terminal;
import com.metoo.sqlite.gather.factory.gather.Gather;
import com.metoo.sqlite.gather.factory.gather.GatherFactory;
import com.metoo.sqlite.model.TerminalModel;
import com.metoo.sqlite.service.IProbeResultService;
import com.metoo.sqlite.service.IProbeService;
import com.metoo.sqlite.utils.date.DateTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-25 22:29
 */
@Slf4j
@RequestMapping
@RestController
public class ProbeManagerController {

    @Autowired
    private IProbeService probeService;
    @Autowired
    private IProbeResultService probeResultService;


    @PostMapping("/probeNmap/uploadScanResult")
    public String probe(@RequestBody ProbeBody body) {
        Map result = new HashMap();
        result.put("code", 0);
        result.put("message", null);
        result.put("taskuuid", body.getTaskuuid());
        if (body.getTaskuuid() == null || "".equals(body.getTaskuuid())) {
            result.put("code", 1);
        }
        if (body.getResult() == null || body.getResult().equals("")) {
            result.put("code", 1);
        } else {
            this.probeService.deleteTable();
            log.info("chuangfa---------------------------：" + body.getResult());
            List<Probe> probeDataList = JSONObject.parseArray(body.getResult(), Probe.class);
            if (probeDataList.size() > 0) {
                String createTime = DateTools.getCreateTime();
                for (Probe probeData : probeDataList) {
                    probeData.setCreateTime(createTime);
                    this.probeService.insert(probeData);
                }
            }
        }


        try {
            GatherFactory factory = new GatherFactory();
            Gather gather = factory.getGather("fileToProbe");
            gather.executeMethod();

            this.probeService.deleteTableBack();
            this.probeService.copyToBck();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List list = this.probeService.selectObjByMap(null);
        log.info("chuangfa + os_scan========================================：" + JSONObject.toJSONString(list));

        ProbeResult probeResult = this.probeResultService.selectObjByOne();
        probeResult.setResult(probeResult.getResult() + 1);
        this.probeResultService.update(probeResult);

        return JSONObject.toJSONString(result);
    }

    // 模拟回调
//    @PostMapping("/probeNmap/uploadScanResult")
//    public String probe(@RequestBody ProbeBody body) {
//
//        // 这里执行
//        ProbeResult probeResult = this.probeResultService.selectObjByOne();
//        probeResult.setResult(probeResult.getResult() + 1);
//        this.probeResultService.update(probeResult);
//        return "";
//    }

}
