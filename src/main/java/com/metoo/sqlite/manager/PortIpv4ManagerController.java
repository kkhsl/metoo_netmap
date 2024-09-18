package com.metoo.sqlite.manager;

import com.metoo.sqlite.service.IPortIpv4Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-24 17:58
 */
@RequestMapping("/admin/port/ipv4")
@RestController
public class PortIpv4ManagerController {

    @Autowired
    private IPortIpv4Service portIpv4Service;

    @GetMapping("/port/ipv4/list")
    public Object portIpv4List(){
        return this.portIpv4Service.selectObjByMap(null);
    }

}
