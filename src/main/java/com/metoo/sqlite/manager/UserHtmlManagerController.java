package com.metoo.sqlite.manager;

import com.metoo.sqlite.dto.UserDto;
import com.metoo.sqlite.service.IUserService;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-19 15:14
 */
@Api("用户管理")
@RequestMapping("/admin/user/html")
@Controller
public class UserHtmlManagerController {

    @Autowired
    private IUserService userService;

    @GetMapping("/index")
    public String index() {
        return "index";
//        return "redirect:/index.jsp";
    }


    @GetMapping("/indexTest")
    public String indexTest() {
        return "indexTest";
//        return "redirect:/index.jsp";
    }

    @GetMapping("/device")
    public String device() {
//        return "redirect:/deviceConfig.html";
        return "deviceConfig";
    }


    @ApiOperation("保存用户")
    @RequestMapping("/save")
    public Result save(@RequestBody(required = false) UserDto dto) {
        Result result = this.userService.save(dto);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("创建用户")
    @RequestMapping("/create")
    public Result create(@RequestBody(required = false) UserDto dto) {
        Result result = this.userService.create(dto);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("删除用户")
    @RequestMapping("/delete")
    public Result delete(@RequestParam String ids) {
        Result result = this.userService.delete(ids);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("修改密码")
    @RequestMapping("/editPassword")
    public Result editPassword(@RequestBody UserDto instance) {
        Result result = this.userService.editPassword(instance);
        return ResponseUtil.ok(result);
    }
}
