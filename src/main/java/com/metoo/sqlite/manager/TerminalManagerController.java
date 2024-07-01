package com.metoo.sqlite.manager;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.Arp;
import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.entity.Terminal;
import com.metoo.sqlite.gather.factory.gather.Gather;
import com.metoo.sqlite.gather.factory.gather.GatherFactory;
import com.metoo.sqlite.model.TerminalModel;
import com.metoo.sqlite.service.IArpService;
import com.metoo.sqlite.service.IProbeService;
import com.metoo.sqlite.service.ITerminalService;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
