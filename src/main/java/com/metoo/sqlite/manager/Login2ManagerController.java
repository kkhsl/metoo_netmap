package com.metoo.sqlite.manager;

import com.metoo.sqlite.core.config.log.OperationLogAnno;
import com.metoo.sqlite.core.config.log.OperationType;
import com.metoo.sqlite.model.LoginBody;
import com.metoo.sqlite.service.IUserService;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.utils.login.CaptchaUtil;
import com.metoo.sqlite.utils.login.CookieUtil;
import com.metoo.sqlite.vo.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-19 10:51
 */
@Slf4j
@Api(description = "登录控制器")
@RestController
@RequestMapping(value = "/admin")
public class Login2ManagerController {

    @Autowired
    private IUserService userService;

    @GetMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //设置响应头信息，通知浏览器不要缓存
        response.setHeader("Expires", "-1");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "-1");
        response.setContentType("image/jpeg");

        // 获取验证码
        String code = CaptchaUtil.getRandomCode();
        // 将验证码输入到session中，用来验证
        HttpSession session = request.getSession();
        log.info("SESSIONID：" + session.getId());
        session.setAttribute("captcha", code);
        this.removeAttrbute(session, "captcha");
        // 输出到web页面
        ImageIO.write(CaptchaUtil.genCaptcha(code), "jpg", response.getOutputStream());
    }

    public void removeAttrbute(final HttpSession session, final String attrName) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                // 删除session中存的验证码
                session.removeAttribute(attrName);
                timer.cancel();
            }
        }, 5 * 60 * 1000);
    }

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void startCleaningTask(HttpSession session) {
        scheduler.scheduleAtFixedRate(() -> {
            // 从 session 中移除验证码
            session.removeAttribute("captcha");
            System.out.println("Captcha removed from session.");
        }, 0, 5, TimeUnit.MINUTES); // 每 5 分钟执行一次
    }


//    @OperationLogAnno(operationType= OperationType.LOGIN, name = "登录", operateType = "2")
    @PostMapping("/login")
    public Result login(HttpServletRequest request, @RequestBody LoginBody loginBody){
        return this.userService.login(request, loginBody);
    }

    @RequestMapping("/logout")
    public Object logout(HttpServletRequest request, HttpServletResponse response){
        Subject subject = SecurityUtils.getSubject();
        if(subject.getPrincipal() != null){
            // 清除cookie
            subject.logout(); // 退出登录
            CookieUtil.removeCookie(request, response, "JSESSIONID");
            return ResponseUtil.ok();
        }else{
            return new Result(401,"log in");
        }
    }
}
