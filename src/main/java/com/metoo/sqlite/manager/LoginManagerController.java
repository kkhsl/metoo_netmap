//package com.metoo.sqlite.manager;
//
//import com.metoo.sqlite.entity.User;
//import com.metoo.sqlite.model.LoginBody;
//import com.metoo.sqlite.service.IUserService;
//import com.metoo.sqlite.utils.StringUtils;
//import com.metoo.sqlite.utils.login.CaptchaUtil;
//import com.metoo.sqlite.utils.login.CookieUtil;
//import com.metoo.sqlite.utils.ResponseUtil;
//import com.metoo.sqlite.vo.Result;
//import io.swagger.annotations.Api;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.authc.IncorrectCredentialsException;
//import org.apache.shiro.authc.UnknownAccountException;
//import org.apache.shiro.authc.UsernamePasswordToken;
//import org.apache.shiro.session.Session;
//import org.apache.shiro.subject.Subject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import javax.imageio.ImageIO;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author HKK
// * @version 1.0
// * @date 2024-06-19 10:51
// */
//@Slf4j
//@Api(description = "登录控制器")
//@Controller
//@RequestMapping(value = "/admin")
//public class LoginManagerController {
//
//    @Autowired
//    private IUserService userService;
//
//
//    @PostMapping("/login")
//    public String login(@RequestBody LoginBody loginBody, RedirectAttributes redirectAttributes){
//        // 通过安全工具类获取 Subject
//        Subject subject = SecurityUtils.getSubject();
//
//        // 获取当前已登录用户
//        Session session = SecurityUtils.getSubject().getSession();
//
//        String sessionCaptcha = (String) session.getAttribute("captcha");
//        session.getStartTimestamp();
//
//        if(!org.springframework.util.StringUtils.isEmpty(loginBody.getCaptcha()) && !StringUtils.isEmpty(sessionCaptcha)){
//            if(sessionCaptcha.toUpperCase().equals(loginBody.getCaptcha().toUpperCase())){
//                boolean flag = true;// 当前用户是否已登录
//                if(subject.getPrincipal() != null && subject.isAuthenticated()){
//                    String userName = subject.getPrincipal().toString();
//                    if(userName.equals(loginBody.getUsername())){
//                        flag = false;
//                    }
//                }
//                if(flag){
//                    UsernamePasswordToken token = new UsernamePasswordToken(loginBody.getUsername(), loginBody.getPassword());
//                    try {
//                        subject.login(token);
//                        session.removeAttribute("captcha");
//                        User user = this.userService.selectObjByName(loginBody.getUsername());
//                        return "redirect:/admin/index"; // 登录成功跳转首页
//                    } catch (UnknownAccountException e) {
//                        e.printStackTrace();
//
//                    } catch (IncorrectCredentialsException e){
//                        e.printStackTrace();
//                    }
//                }else{
//                    redirectAttributes.addFlashAttribute("error", "用户已登录！");
//                }
//            }else{
//                redirectAttributes.addFlashAttribute("error", "验证码错误！");
//            }
//        }else{
//            redirectAttributes.addFlashAttribute("error", "验证码已失效！");
//        }
//        return "redirect:/admin/index";
//    }
//
//    // 显示首页
//    @GetMapping("/index")
//    public String homePage() {
//        return "index"; // 返回首页视图
//    }
//    @GetMapping("/doLogin")
//    public String login() {
//        return "login"; // 返回首页视图
//    }
//
//
//    @GetMapping("/captcha")
//    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        //设置响应头信息，通知浏览器不要缓存
//        response.setHeader("Expires", "-1");
//        response.setHeader("Cache-Control", "no-cache");
//        response.setHeader("Pragma", "-1");
//        response.setContentType("image/jpeg");
//
//        // 获取验证码
//        String code = CaptchaUtil.getRandomCode();
//        // 将验证码输入到session中，用来验证
//        HttpSession session = request.getSession();
//        log.info("SESSIONID：" + session.getId());
//        session.setAttribute("captcha", code);
//        this.removeAttrbute(session, "captcha");
//        // 输出到web页面
//        ImageIO.write(CaptchaUtil.genCaptcha(code), "jpg", response.getOutputStream());
//    }
//
//    public void removeAttrbute(final HttpSession session, final String attrName) {
//        final Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            public void run() {
//                // 删除session中存的验证码
//                session.removeAttribute(attrName);
//                timer.cancel();
//            }
//        }, 5 * 60 * 1000);
//    }
//
//    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//
//    public void startCleaningTask(HttpSession session) {
//        scheduler.scheduleAtFixedRate(() -> {
//            // 从 session 中移除验证码
//            session.removeAttribute("captcha");
//            System.out.println("Captcha removed from session.");
//        }, 0, 5, TimeUnit.MINUTES); // 每 5 分钟执行一次
//    }
//
//
////    @PostMapping("/login")
////    public Result login(@RequestBody LoginBody loginBody){
////        return this.userService.login(loginBody);
////    }
//
//    @RequestMapping("/logout")
//    public Object logout(HttpServletRequest request, HttpServletResponse response){
//        Subject subject = SecurityUtils.getSubject();
//        if(subject.getPrincipal() != null){
//            // 清除cookie
//            subject.logout(); // 退出登录
//            CookieUtil.removeCookie(request, response, "JSESSIONID");
//            return ResponseUtil.ok();
//        }else{
//            return new Result(401,"log in");
//        }
//    }
//}
