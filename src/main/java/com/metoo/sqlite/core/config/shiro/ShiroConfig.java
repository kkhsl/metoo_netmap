//package com.metoo.sqlite.core.config.shiro;
//
//import com.metoo.sqlite.core.config.license.filter.LicenseFilter;
//import com.metoo.sqlite.core.config.shiro.filter.MyAccessControlFilter;
//import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
//import org.apache.shiro.codec.Base64;
//import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
//import org.apache.shiro.mgt.DefaultSubjectDAO;
//import org.apache.shiro.mgt.SessionStorageEvaluator;
//import org.apache.shiro.realm.Realm;
//import org.apache.shiro.spring.LifecycleBeanPostProcessor;
//import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
//import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
//import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
//import org.apache.shiro.web.filter.mgt.FilterChainManager;
//import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
//import org.apache.shiro.web.mgt.CookieRememberMeManager;
//import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
//import org.apache.shiro.web.servlet.SimpleCookie;
//import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
//import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.DependsOn;
//
//import javax.servlet.Filter;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//
///**
// * <p>
// * Title: ShiroConfig.java
// * </p>
// *
// * <p>
// * Description: 整合Shiro框架相关的配置类; Web环境中，自动为SecurityUtil注入Securitymanagers
// * swagger-ui.html
// * </p>
// *
// * <p>
// * authen: hkk
// * </p>
// */
//@Configuration
//public class ShiroConfig {
//
//
//    // 1, 创建ShiroFilter  //负责拦截所有请求
//    // 配置访问资源所需要的权限
//    @Bean
//    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager) {
//        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
//
//        // 1,给过滤器设置安全管理器
//        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);
//
//
////         设置 FilterChainResolver
//        PathMatchingFilterChainResolver filterChainResolver = new PathMatchingFilterChainResolver();
//        FilterChainManager filterChainManager = new DefaultFilterChainManager();
////         定义过滤器链
//        filterChainManager.addFilter("maf", new MyAccessControlFilter());
//        filterChainManager.addFilter("lf", new LicenseFilter());
//        filterChainManager.createChain("/admin/**", "maf");
//        filterChainManager.createChain("/admin/captcha", "anon");
//        filterChainManager.createChain("/admin/login", "anon");
//        filterChainManager.createChain("/admin/logout", "anon");
//        filterChainManager.createChain("/admin/register", "anon");
//        filterChainResolver.setFilterChainManager(filterChainManager);
//
//        // 2,配置系统受限资源
////        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
////
//////        filterChainDefinitionMap.put("/admin/doLogin/**", "anon");
//////        filterChainDefinitionMap.put("/admin/index/**", "anon");
//////        shiroFilterFactoryBean.setLoginUrl("/admin/doLogin");
//////        shiroFilterFactoryBean.setSuccessUrl("/admin/index");
//////
////        // 配置过滤器链
////
////        filterChainDefinitionMap.put("/py/**", "anon");
////
////        filterChainDefinitionMap.put("/admin/captcha", "anon");
////        filterChainDefinitionMap.put("/admin/login", "anon");
////        filterChainDefinitionMap.put("/admin/logout", "anon");
////        filterChainDefinitionMap.put("/admin/register", "anon");
////        filterChainDefinitionMap.put("/swagger-ui.html", "anon");
////
////
////        filterChainDefinitionMap.put("/admin/**", "rmb");
////        filterChainDefinitionMap.put("/admin/**", "lf");
////
//////        filterChainDefinitionMap.put("/admin/license/**", "authc");
//////
//////        filterChainDefinitionMap.put("/admin/**", "authc");
//
//
//        // 配置URL过滤器链
//        shiroFilterFactoryBean.setLoginUrl("/admin/auth/401");
//        shiroFilterFactoryBean.setUnauthorizedUrl("/admin/auth/403");
////         shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
//
//        return shiroFilterFactoryBean;
//    }
//
//    //2, 创建安全管理器 web环境中配置webSecurity
//    // getDefaultWevSecurityManager(Realm realm)
//    @Bean
//    public DefaultWebSecurityManager getDefaultWebSecurityManager() {
//        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
//
//        // 1-1, 给安全管理器设置Realm
//        defaultWebSecurityManager.setRealm(getRealm());
//        // 1-2，给安全管理器设置Realms
////        List<Realm> realms = new ArrayList<Realm>();
////        realms.add(jwtRealm());
////        realms.add(getRealm());
////        defaultWebSecurityManager.setRealms(realms);
//        // 2，给安全管理器设置SessionManager
//        //  getDefaultWebSessionManager (isAuthenticated:false)
//        defaultWebSecurityManager.setSessionManager(getDefaultSessionManager());
//        // 3，给安全管理器设置RememberMeManager
////        defaultWebSecurityManager.setRememberMeManager(rememberMeManager());
//
//        // 3.关闭shiro自带的session
//        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
//        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator());
//        defaultWebSecurityManager.setSubjectDAO(subjectDAO);
//
//        return defaultWebSecurityManager;
//    }
//
//    // 3, 自定义realm
//    @Bean
//    public Realm getRealm() {
//        MyRealm myRealm = new MyRealm();
//        // 设置Realm使用hash凭证校验匹配器; 问：Realm 不设置hash凭证器会出现什么
//        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
//        // 设置加密算法 SHA-1、md5
//        hashedCredentialsMatcher.setHashAlgorithmName("md5");
//        // 设置加密次数（散列次数）
//        hashedCredentialsMatcher.setHashIterations(1024);
//        myRealm.setCredentialsMatcher(hashedCredentialsMatcher);
//
//        // 开启缓存管理
////        myRealm.setCachingEnabled(true);// 开启全局缓存
//        // 方式一：EhCache
////        myRealm.setCacheManager(new EhCacheManager());// EhCache
//////        方式二：Redis
////        myRealm.setCacheManager(new RedisCacheManager());// RedisCacheManager
////        myRealm.setAuthenticationCachingEnabled(true);// 认证缓存
////        myRealm.setAuthenticationCacheName("authenticationCache");
////        myRealm.setAuthorizationCachingEnabled(true);// 授权缓存
////        myRealm.setAuthorizationCacheName("authorizationCache");
//        return myRealm;
//    }
//    @Bean("sessionManager")
//    public DefaultWebSessionManager getDefaultSessionManager() {
//        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
//       // defaultWebSessionManager.setGlobalSessionTimeout(1000 * 60 * 60 * 24*7);// 会话过期时间，单位：毫秒(在无操作时开始计时)
//        defaultWebSessionManager.setGlobalSessionTimeout(-1000L);// -1000L,永不过期 1000 * 60 * 20
//        defaultWebSessionManager.setSessionValidationSchedulerEnabled(true);
//        defaultWebSessionManager.setSessionIdCookieEnabled(true);
//        defaultWebSessionManager.setSessionIdUrlRewritingEnabled(false);// 移除自带的JSESSIONID，方式第二次打开浏览器是进行注销操作发生
//        defaultWebSessionManager.setSessionIdCookie(sessionIdCookie());
//        defaultWebSessionManager.setSessionIdCookieEnabled(true);
//        return defaultWebSessionManager;
//    }
//
//    /**
//     * 设置cookie
//     * @return
//     */
//    private SimpleCookie sessionIdCookie() {
//        SimpleCookie cookie = new SimpleCookie();
//        cookie.setName("JSESSIONID");
//        cookie.setHttpOnly(true);
//        cookie.setMaxAge(-1);
//        return cookie;
//    }
//
////    @Bean("sessionManager")
////    public DefaultSessionManager getDefaultSessionManager() {
////        DefaultSessionManager defaultSessionManager = new DefaultSessionManager();
////        // defaultSessionManager.setGlobalSessionTimeout(1000 * 60 * 60 * 24*7);// 会话过期时间，单位：毫秒(在无操作时开始计时)
////        defaultSessionManager.setGlobalSessionTimeout(-1000L);// -1000L,永不过期
////        defaultSessionManager.setSessionValidationSchedulerEnabled(true);
//////        defaultSessionManager.setSessionIdCookieEnabled(true);
////        return defaultSessionManager;
////    }
//
//    /**
//     * 禁用session, 不保存用户登录状态。保证每次请求都重新认证
//     */
//    @Bean
//    protected SessionStorageEvaluator sessionStorageEvaluator() {
//        DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
//        sessionStorageEvaluator.setSessionStorageEnabled(false);
//        return sessionStorageEvaluator;
//    }
//
//
//    /**
//     * 　　id：就是session id；
//     *
//     * 　　startTimestamp：session的创建时间；
//     *
//     * 　　stopTimestamp：session的失效时间；
//     *
//     * 　　lastAccessTime：session的最近一次访问时间，初始值是startTimestamp
//     *
//     * 　　timeout：session的有效时长，默认30分钟
//     *
//     * 　　expired：session是否到期
//     *
//     * 　　attributes：session的属性容器
//     * @return
//     */
//    // 创建一个简单的Cookie对象；创建cookie模板
//    @Bean
//    public SimpleCookie rememberMeCookie(){
//        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
//        // 只能通过http访问cookie，js不能
//        // XSS:保证该系统不会受到跨域的脚本操作攻击
//        simpleCookie.setHttpOnly(true);
//        //simpleCookie.setName("simpleCookie");
//        //<!-- 记住我cookie生效时间30天 ,单位秒;如果设置为-1标识浏览器关闭就失效 -->
//        simpleCookie.setMaxAge(2678400); // 5000   2678400
//        return simpleCookie;
//    }
//
//    /**
//     * cookie管理对象;
//     * rememberMeManager()方法是生成rememberMe管理器，而且要将这个rememberMe管理器设置到securityManager中
//     * @return
//     */
//    @Bean
//    public CookieRememberMeManager rememberMeManager() {
//        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
//        cookieRememberMeManager.setCookie(rememberMeCookie());
//        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
//        cookieRememberMeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
//        return cookieRememberMeManager;
//    }
//
//    /**
//     *  开启shiro aop 注解支持. 否则注解不生效
//     *  使用代理方式;所以需要开启代码支持;
//     * @return
//     */
//    @Bean
//    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
//        return new LifecycleBeanPostProcessor();
//    }
//    @Bean
//    @DependsOn({"lifecycleBeanPostProcessor"})
//    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator(){
//        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
//        advisorAutoProxyCreator.setProxyTargetClass(true);
//        return advisorAutoProxyCreator;
//    }
//
//    @Bean
//    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager defaultWebSecurityManager){
//        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
//        authorizationAttributeSourceAdvisor.setSecurityManager(defaultWebSecurityManager);
//        return authorizationAttributeSourceAdvisor;
//    }
//
//}
