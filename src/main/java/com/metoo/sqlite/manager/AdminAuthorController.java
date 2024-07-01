package com.metoo.sqlite.manager;

import com.metoo.sqlite.utils.ResponseUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin/auth")
public class AdminAuthorController {

    @GetMapping("/401")
    public Object page401() {
        return ResponseUtil.unlogin();
    }

    @GetMapping("/403")
    public Object page403() {
        return ResponseUtil.unauthz();
    }

}
