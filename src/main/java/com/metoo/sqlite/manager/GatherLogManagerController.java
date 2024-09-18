package com.metoo.sqlite.manager;

import com.metoo.sqlite.dto.GatherLogDTO;
import com.metoo.sqlite.entity.GatherSemaphore;
import com.metoo.sqlite.service.IGatherLogService;
import com.metoo.sqlite.service.IGatherSemaphoreService;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-07-01 16:39
 */
@Slf4j
@RequestMapping("/admin/gather/log")
@RestController
public class GatherLogManagerController {

    @Autowired
    private IGatherLogService gatherLogService;
    @Autowired
    private IGatherSemaphoreService gatherSemaphoreService;

    @RequestMapping("list")
    public Result list(@RequestBody GatherLogDTO dto){
        Result result = this.gatherLogService.selectObjConditionQuery(dto);
        return result;
    }


    @GetMapping("status")
    public Result list(){
        GatherSemaphore semaphore = this.gatherSemaphoreService.selectObjByOne();
        return ResponseUtil.ok(semaphore);
    }
}
