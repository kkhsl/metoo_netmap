package com.metoo.sqlite.core.config.license.filter;

import com.alibaba.fastjson.JSONObject;
import com.metoo.sqlite.entity.License;
import com.metoo.sqlite.service.ILicenseService;
import com.metoo.sqlite.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

//@Order(2)
public class LicenseFilter implements Filter {

    // 定义不需要过滤的路径
    private final List<String> excludedPaths
            = Arrays.asList(
            "/admin/captcha",
            "/admin/login",
            "/admin/logout",
            "/admin/license/systemInfo",
            "/admin/license/query",
            "/admin/license/update"
        );

    @Autowired
    private ILicenseService licenseService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        // 检测授权码
        License license = this.licenseService.detection();
        if(license != null && license.getStatus() == 0 && license.getFrom() == 0){
            chain.doFilter(servletRequest, servletResponse);
        }else{
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            System.out.println(request.getRequestURI());
            System.out.println(request.getRequestURL());
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            if (excludedPaths.stream().anyMatch(request.getRequestURI()::startsWith)) {
                // 如果在排除列表中，则直接放行
                chain.doFilter(request, response);
            } else {
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
            }
        }
      }

    @Override
    public void destroy() {

    }
}
