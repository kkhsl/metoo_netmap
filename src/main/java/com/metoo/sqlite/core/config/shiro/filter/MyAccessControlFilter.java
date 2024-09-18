package com.metoo.sqlite.core.config.shiro.filter;

import com.alibaba.fastjson.JSONObject;
import com.metoo.sqlite.vo.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

// AuthenticatingFilter
public class MyAccessControlFilter extends AccessControlFilter {

    @Value("${spring.profiles.active}")
    private String env;

    /**
     * 处理请求前的过滤逻辑
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = SecurityUtils.getSubject();

        // 如果用户已经登录并且经过认证，允许通过
        if (isUserAuthenticated(subject)) {
            return true;
        }

        // 未通过认证或记住我功能，返回未登录的响应
        return handleUnauthenticated(response);
    }

    /**
     * 判断用户是否已通过认证
     */
    private boolean isUserAuthenticated(Subject subject) {
        return subject.getPrincipal() != null && subject.isAuthenticated();
    }

    /**
     * 处理未认证的用户响应
     */
    private boolean handleUnauthenticated(ServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().print(JSONObject.toJSONString(new Result(401, "Log in")));
        return false;
    }

    /**
     * 是否允许访问资源
     * 此处固定返回 false，让访问都走 onAccessDenied 方法进行认证控制
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    /**
     * 当访问被拒绝时的处理逻辑
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return false;
    }
}

