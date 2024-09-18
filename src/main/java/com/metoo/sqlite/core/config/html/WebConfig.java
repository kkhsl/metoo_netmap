package com.metoo.sqlite.core.config.html;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置特定文件的访问路径
        registry.addResourceHandler("/encrypt.html")
                .addResourceLocations("classpath:/html/");
    }
}