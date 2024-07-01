//package com.metoo.sqlite.core.config.web;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.springframework.web.servlet.view.InternalResourceViewResolver;
//import org.springframework.web.servlet.view.UrlBasedViewResolver;
//
///**
// * @author HKK
// * @version 1.0
// * @date 2024-06-21 17:23
// */
//
///**
// * CORS（跨域资源共享）配置
// *
// *
// * 限制用户直接通过浏览器访问 API
// *  1. 使用身份验证和授权
// *  2. 使用自定义拦截器
// *  3. 使用 Referer 或 Origin 头验证请求来源
// */
//@Configuration
//@EnableWebMvc
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
////        System.out.println("===");
////        registry.addMapping("/admin/**")
////                .allowedOrigins("http://127.0.0.1")
////                .allowedMethods("GET", "POST", "PUT", "DELETE")
////                .allowCredentials(true);
//    }
//}