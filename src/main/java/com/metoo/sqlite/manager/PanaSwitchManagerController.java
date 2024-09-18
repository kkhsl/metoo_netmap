package com.metoo.sqlite.manager;

import com.metoo.sqlite.entity.PanaSwitch;
import com.metoo.sqlite.service.IPanaSwitchService;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/pana")
public class PanaSwitchManagerController {

    @Autowired
    private IPanaSwitchService panaSwitchService;

    @GetMapping("/state")
    public Result state(){
        PanaSwitch panaSwitch = this.panaSwitchService.selectObjByOne();
        if(panaSwitch == null){
            PanaSwitch obj = new PanaSwitch();
            obj.setState(true);
            this.panaSwitchService.insert(obj);
            return ResponseUtil.ok(obj);
        }
        return ResponseUtil.ok(panaSwitch);
    }

    @PutMapping("/state")
    public Result update(@RequestParam Boolean state){
        PanaSwitch panaSwitch = this.panaSwitchService.selectObjByOne();
        if(panaSwitch == null){
            PanaSwitch obj = new PanaSwitch();
            obj.setState(state);
            this.panaSwitchService.insert(obj);
        }else{
            panaSwitch.setState(state);
            this.panaSwitchService.update(panaSwitch);
        }
        return ResponseUtil.ok(panaSwitch);
    }
}
