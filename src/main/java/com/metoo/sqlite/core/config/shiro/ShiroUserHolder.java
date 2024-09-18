package com.metoo.sqlite.core.config.shiro;

import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.entity.User;
import com.metoo.sqlite.service.IUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShiroUserHolder {

    public static User currentUser() {
        if (SecurityUtils.getSubject() != null){
            Subject subject = SecurityUtils.getSubject();
            if(subject == null){
                SecurityUtils.getSubject().logout();
            }
            if(subject.getPrincipal() != null && subject.isAuthenticated()){
                String userName = SecurityUtils.getSubject().getPrincipal().toString();
                IUserService userService = (IUserService) ApplicationContextUtils.getBean("userServiceImpl");
                User user = userService.selectObjByName(userName);
                if(user != null){
                    return user;
                }else{
                    SecurityUtils.getSubject().logout();
                }
            }
        }
        return null;
    }

}
