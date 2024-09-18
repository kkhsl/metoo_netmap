package com.metoo.sqlite.core.config.license.filter;

import com.alibaba.fastjson.JSONObject;
import com.metoo.sqlite.entity.License;
import com.metoo.sqlite.service.ILicenseService;
import com.metoo.sqlite.vo.Result;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// @Component 仅作用于shiro过滤器链
public class LicenseFilter extends PathMatchingFilter {

    private ILicenseService licenseService;

    public LicenseFilter(ILicenseService licenseService){
        this.licenseService = licenseService;
    }

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        License license = licenseService.detection();
        if(license != null && license.getStatus() == 0 && license.getFrom() == 0){
            return true;
        }else{
            // 如果不在排除列表中，则执行过滤器的逻辑
            // 此处可以编写你的过滤器逻辑
            String message = "未授权";
            if(license != null){
                switch (license.getStatus()){
                    case 1:
                        message = "未授权";
                        break;
                    case 2:
                        message = "授权已过期";
                        break;
                }
            }
            Result result = new Result(413, message);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().print(JSONObject.toJSONString(result));
            return false;
        }
    }

    @Override
    public void destroy() {

    }
}
