package com.metoo.sqlite.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.log.OperationType;
import com.metoo.sqlite.dto.UserDto;
import com.metoo.sqlite.dto.page.PageInfo;
import com.metoo.sqlite.entity.OperationLog;
import com.metoo.sqlite.entity.User;
import com.metoo.sqlite.manager.utils.VerifyUtils;
import com.metoo.sqlite.mapper.UserMapper;
import com.metoo.sqlite.model.LoginBody;
import com.metoo.sqlite.service.IOperationLogService;
import com.metoo.sqlite.service.IUserService;
import com.metoo.sqlite.utils.*;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.utils.login.SaltUtils;
import com.metoo.sqlite.core.config.shiro.ShiroUserHolder;
import com.metoo.sqlite.utils.net.Ipv4Utils;
import com.metoo.sqlite.vo.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-19 10:55
 */
@Service
@Transactional
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IOperationLogService operationLogService;

    @Override
    public User selectObjById(Integer id) {
        return this.userMapper.selectObjById(id);
    }

    @Override
    public User selectObjByName(String username) {
        return this.userMapper.selectObjByName(username);
    }

    @Override
    public Result selectObjByMap(Map params) {
        List<User> list = this.userMapper.selectObjByMap(params);
        return ResponseUtil.ok(list);
    }

    @Override
    public Result login(HttpServletRequest request, LoginBody loginBody) {
        // 通过安全工具类获取 Subject
        Subject subject = SecurityUtils.getSubject();

        // 获取当前已登录用户
        Session session = SecurityUtils.getSubject().getSession();

        String sessionCaptcha = (String) session.getAttribute("captcha");
        session.getStartTimestamp();
        if(StringUtils.isEmpty(loginBody.getUsername())){
            return ResponseUtil.badArgument("用户名必填");
        }
        if(StringUtils.isEmpty(loginBody.getPassword())){
            return ResponseUtil.badArgument("密码必填");
        }
        if(StringUtils.isEmpty(loginBody.getCaptcha())){
            return ResponseUtil.badArgument("验证码必填");
        }
        if(!org.springframework.util.StringUtils.isEmpty(loginBody.getCaptcha()) && !StringUtils.isEmpty(sessionCaptcha)){
            if(sessionCaptcha.toUpperCase().equals(loginBody.getCaptcha().toUpperCase())){
                boolean flag = true;// 当前用户是否已登录
                if(subject.getPrincipal() != null && subject.isAuthenticated()){
                    String userName = subject.getPrincipal().toString();
                    if(userName.equals(loginBody.getUsername())){
                        flag = false;
                    }
                }
                if(flag){
                    UsernamePasswordToken token = new UsernamePasswordToken(loginBody.getUsername(), loginBody.getPassword());
                    try {
                        subject.login(token);
                        session.removeAttribute("captcha");
                        User user = this.userMapper.selectObjByName(loginBody.getUsername());

                        OperationLog instance = new OperationLog();
                        instance.setCreateTime(DateTools.getCreateTime());
                        instance.setIp(Ipv4Utils.getRealIP(request));
                        instance.setAction("LOGIN");
                        instance.setDesc("登录");
                        instance.setType("2");
                        instance.setAccount(loginBody.getUsername());
                        instance.setName(loginBody.getUsername());
                        instance.setCreateTime(DateTools.getCreateTime());
                        this.operationLogService.insert(instance);
                        return ResponseUtil.ok(user.getId());
                    } catch (UnknownAccountException e) {
                        e.printStackTrace();
                        return new Result(410, "用户名错误");
                    } catch (IncorrectCredentialsException e){
                        e.printStackTrace();
                        return new Result(420, "密码错误");
                    }
                }else{
                    return new Result(200, "用户已登录");
                }
            }else{
                return new Result(430, "验证码错误");
            }
        }else{
            return new Result(400,  "验证码已过期");
        }
    }

    @Override
    public Result selectObjConditionQuery(UserDto dto) {
        if(dto == null){
            dto = new UserDto();
        }
        Page<User> page = PageHelper.startPage(dto.getCurrentPage(), dto.getPageSize());
        this.userMapper.selectObjConditionQuery(dto);
        if (page.getResult().size() > 0) {
            return ResponseUtil.ok(new PageInfo<User>(page));
        }
        return ResponseUtil.ok();
    }

    @Override
    public Result save(UserDto instance) {
        if (instance != null && instance.getId() != null) {
            if (instance.getUsername() != null && !instance.getUsername().equals("")) {
                User currentUser = ShiroUserHolder.currentUser();
                User user = this.userMapper.selectObjByName(instance.getUsername());
                if (user != null) {
                    if (currentUser != null) {
                        // 判断修改时是否为本人
                        if (user.getId().equals(currentUser.getId())) {
                            instance.setFlag(true);
                        }
                    } else {
                        return ResponseUtil.fail(400, "用户已存在");
                    }
                }
                if (!StringUtils.isEmpty(instance.getPassword())
                        || !StringUtils.isEmpty(instance.getVerifyPassword())) {
                    String oldPassword = CommUtils.password(instance.getOldPassword(), currentUser.getSalt());
                    if (!currentUser.getPassword().equals(oldPassword)) {
                        return ResponseUtil.badArgument("旧密码与原始密码不一致");
                    }
                    String newPassword = CommUtils.password(instance.getPassword(), currentUser.getSalt());
                    if (newPassword.equals(oldPassword)) {
                        return ResponseUtil.badArgument("新密码与旧密码相同");
                    }
                    if (!instance.getPassword().equals(instance.getVerifyPassword())) {
                        return ResponseUtil.badArgument("新密码与确认密码不一致");
                    }
                    if (instance.getPassword().length() < 6 || instance.getPassword().length() > 20) {
                        return ResponseUtil.badArgument("设置6-20位新密码");
                    }
                    String sale = SaltUtils.getSalt(8);
                    instance.setPassword(CommUtils.password(instance.getPassword(), sale));
                    instance.setSalt(sale);
                    instance.setFlag(true);
                }
                if(StringUtils.isNotEmpty(instance.getMobile())){
                    // 校验格
                    boolean verifyMobile = VerifyUtils.isValidPhoneNumber(instance.getMobile());
                    if(!verifyMobile){
                        return ResponseUtil.ok("手机号码格式错误");
                    }
                    if(!this.verifyMobileUnique(instance.getMobile(), instance.getId())){
                        return ResponseUtil.badArgument("手机号码重复");
                    }
                }

                if(StringUtils.isNotEmpty(instance.getEmail())){

                    boolean verifyMobile = VerifyUtils.isValidEmail(instance.getEmail());
                    if(!verifyMobile){
                        return ResponseUtil.ok("邮箱格式错误");
                    }
                    if(!this.verifyEmailUnique(instance.getEmail(), instance.getId())){
                        return ResponseUtil.badArgument("邮箱重复");
                    }
                }

                if (this.userMapper.update(instance) > 0) {
                    if(instance.isFlag()){
                        SecurityUtils.getSubject().logout();
                    }
                    return ResponseUtil.ok();
                }
                return ResponseUtil.error();
            }
            return ResponseUtil.badArgument("请输入用户名");
        }
        return ResponseUtil.badArgument("请输入用户ID");
    }

    @Override
    public Result create(UserDto instance) {
        if (instance != null) {
            if (StringUtil.isEmpty(instance.getUsername())) {// 新增时必须验证密码
                return ResponseUtil.badArgument("请输入用户名");
            }
            if (instance.getId() != null && instance.getPassword() == null && instance.getVerifyPassword() == null) {
                return ResponseUtil.badArgument("参数错误");
            }
            // 验证密码参数
            if (instance.getId() == null) {
                if (instance.getId() == null && StringUtils.isEmpty(instance.getPassword())) {
                    return ResponseUtil.badArgument("请输入密码");
                }
                if (instance.getId() == null && StringUtils.isEmpty(instance.getVerifyPassword())) {
                    return ResponseUtil.badArgument("请输入确认密码");
                }
            } else {
                if (!StringUtils.isEmpty(instance.getPassword())) {
                    if (StringUtils.isEmpty(instance.getVerifyPassword())) {
                        return ResponseUtil.badArgument("请输入确认密码");
                    }
                }
                if (!StringUtils.isEmpty(instance.getVerifyPassword())) {
                    if (StringUtils.isEmpty(instance.getPassword())) {
                        return ResponseUtil.badArgument("请输入密码");
                    }
                }
            }

            User user = this.userMapper.selectObjByName(instance.getUsername());
            if (user != null) {
                User currentUser = ShiroUserHolder.currentUser();
                if (currentUser != null) {
                    // 判断修改时是否为本人
                    if (user.getId().equals(currentUser.getId())) {
                        instance.setFlag(true);
                    }
                } else {
                    return ResponseUtil.fail(400, "用户已存在");
                }
            }

            if (!org.springframework.util.StringUtils.isEmpty(instance.getPassword())) {
                if (instance.getPassword().length() < 6 || instance.getPassword().length() > 20) {
                    return ResponseUtil.badArgument("设置6-20位新密码");
                } else {
                    String sale = SaltUtils.getSalt(8);
                    String password = CommUtils.password(instance.getPassword(), sale);
                    instance.setPassword(password);
                    instance.setSalt(sale);
                }
            }

            if(StringUtils.isNotEmpty(instance.getMobile())){
                if(!this.verifyMobileUnique(instance.getMobile(), instance.getId())){
                    return ResponseUtil.badArgument("手机号码重复");
                }
            }

            if(StringUtils.isNotEmpty(instance.getEmail())){
                if(!this.verifyEmailUnique(instance.getEmail(), instance.getId())){
                    return ResponseUtil.badArgument("邮箱重复");
                }
            }

            if(instance.getId() == null || instance.getId().equals("")){
                if (this.userMapper.save(instance) > 0) {
                    // 判断是否为本人
                    if(instance.isFlag()){
                        SecurityUtils.getSubject().logout();
                    }
               /* User currentUser = ShiroUserHolder.currentUser();
                if (currentUser.getUsername().equals(instance.getUsername())) {
                    SecurityUtils.getSubject().logout();
                }*//*else{
                    // 退出指定用户

                }*/
                    return ResponseUtil.ok();
                }
            }else{
                if (this.userMapper.update(instance) > 0) {
                    if(instance.isFlag()){
                        SecurityUtils.getSubject().logout();
                    }
                    return ResponseUtil.ok();
                }
            }

            return ResponseUtil.error();
        }
        return ResponseUtil.badArgument();
    }

    public boolean verifyMobileUnique(String mobile, Integer id){
        Map params = new HashMap();
        params.put("mobile", mobile);
        params.put("notId", id);
        List<User> users = this.userMapper.selectObjByMap(params);
        if(users.size() > 0){
            return false;
        }
        return true;
    }

    public boolean verifyEmailUnique(String email, Integer id){
        Map params = new HashMap();
        params.put("email", email);
        params.put("notId", id);
        List<User> users = this.userMapper.selectObjByMap(params);
        if(users.size() > 0){
            return false;
        }
        return true;
    }

    @Override
    public Result update(User user) {
        return null;
    }

    @Override
    public Result delete(String ids) {
        if(ids != null && !ids.equals("")){
            for (String id : ids.split(",")){
                User user = this.userMapper.selectObjById(Integer.parseInt(id));
                if(user != null){
                    try {
                        int i = this.userMapper.delete(Integer.parseInt(id));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return ResponseUtil.badArgument(user.getUsername() + "删除失败");
                    }
                }
            }
            return ResponseUtil.ok();
        }
        return ResponseUtil.badArgument();
    }

    @Override
    public Result editPassword(UserDto instance) {
        if (instance != null && instance.getId() != null) {
            User currentUser = ShiroUserHolder.currentUser();
            User user = this.userMapper.selectObjById(instance.getId());
            if (currentUser.getUsername().equals(user.getUsername())) {
                if (!StringUtils.isEmpty(instance.getPassword())
                        || !StringUtils.isEmpty(instance.getVerifyPassword())) {
                    String oldPassword = CommUtils.password(instance.getOldPassword(), currentUser.getSalt());
                    if (!currentUser.getPassword().equals(oldPassword)) {
                        return ResponseUtil.badArgument("旧密码与原始密码不一致");
                    }
                    String newPassword = CommUtils.password(instance.getPassword(), currentUser.getSalt());
                    if (newPassword.equals(oldPassword)) {
                        return ResponseUtil.badArgument("新密码与旧密码相同");
                    }
                    if (!instance.getPassword().equals(instance.getVerifyPassword())) {
                        return ResponseUtil.badArgument("新密码与确认密码不一致");
                    }
                    if (instance.getPassword().length() < 6 || instance.getPassword().length() > 20) {
                        return ResponseUtil.badArgument("设置6-20位新密码");
                    }
                    String sale = SaltUtils.getSalt(8);
                    instance.setPassword(CommUtils.password(instance.getPassword(), sale));
                    instance.setSalt(sale);
                    instance.setFlag(true);
                    if (this.userMapper.update(instance) > 0) {
                        Subject subject = SecurityUtils.getSubject();
                        subject.logout();
                        return ResponseUtil.ok();
                    }
                } else {

                    return ResponseUtil.badArgument("请输入密码或确认密码");
                }
            }
            return ResponseUtil.badArgument();

        }
        return ResponseUtil.badArgument("请输入用户ID");
    }
}
