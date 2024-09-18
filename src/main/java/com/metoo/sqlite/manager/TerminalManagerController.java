package com.metoo.sqlite.manager;

import com.metoo.sqlite.gather.factory.gather.thread.Gather;
import com.metoo.sqlite.gather.factory.gather.thread.GatherFactory;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-28 17:49
 */
@RequestMapping("/admin/terminal")
@RestController
public class TerminalManagerController {

    @GetMapping("/gather")
    public Result gather(){
        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather(Global.TERMINAL);
        gather.executeMethod();
        return ResponseUtil.ok();
    }

}
