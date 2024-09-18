package com.metoo.sqlite.manager;

import com.github.pagehelper.Page;
import com.metoo.sqlite.core.config.shiro.ShiroUserHolder;
import com.metoo.sqlite.dto.UserDto;
import com.metoo.sqlite.entity.User;
import com.metoo.sqlite.service.IUserService;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-19 15:14
 */
@Api("用户管理")
@RequestMapping("/admin/user")
@RestController
public class UserManagerController {

    @Autowired
    private IUserService userService;

    @ApiOperation("用户列表")
    @RequestMapping("/list")
    public Result list(@RequestBody(required = false) UserDto dto) {
        Result result = this.userService.selectObjConditionQuery(dto);
        return result;
    }

    @ApiOperation("保存用户")
    @RequestMapping("/save")
    public Result save(@RequestBody(required = false) UserDto dto) {
        Result result = this.userService.save(dto);
        return result;
    }

    @ApiOperation("创建用户")
    @RequestMapping("/create")
    public Result create(@RequestBody(required = false) UserDto dto) {
        Result result = this.userService.create(dto);
        return result;
    }

    @ApiOperation("删除用户")
    @RequestMapping("/delete")
    public Result delete(@RequestParam String ids) {
        Result result = this.userService.delete(ids);
        return result;
    }

    @ApiOperation("修改密码")
    @RequestMapping("/editPassword")
    public Result editPassword(@RequestBody UserDto instance) {
        Result result = this.userService.editPassword(instance);
        return result;
    }

    @RequestMapping("/personal")
    public Object personal() {
        User user = ShiroUserHolder.currentUser();
        if (user == null) {
            return ResponseUtil.unlogin();
        }
        return ResponseUtil.ok(user);
    }
}
