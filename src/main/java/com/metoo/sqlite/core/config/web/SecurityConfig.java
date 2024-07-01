//package com.metoo.sqlite.core.config.web;
//
//
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author HKK
// * @version 1.0
// * @date 2024-06-27 16:10
// */
//@Configuration
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .csrf().and()
//                .authorizeRequests()
//                .antMatchers("/api/**").authenticated()
//                .and()
//                .formLogin().and()
//                .httpBasic();
//    }
//}
