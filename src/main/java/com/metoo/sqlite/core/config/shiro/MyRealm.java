package com.metoo.sqlite.core.config.shiro;

import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.core.config.shiro.salt.MyByteSource;
import com.metoo.sqlite.entity.User;
import com.metoo.sqlite.service.IUserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.util.ObjectUtils;

/**
 * 自定义Realm 将认证/授权数据来源设置为数据库的实现
 */
public class MyRealm extends AuthorizingRealm {

    /**
     * 限定这个 Realm 只处理 UsernamePasswordToken
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;}

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        String username = (String) authenticationToken.getPrincipal();

        IUserService userService = (IUserService) ApplicationContextUtils.getBean("userServiceImpl");

        User user = userService.selectObjByName(username);

        if(!ObjectUtils.isEmpty(user)){
            if(username.equals(user.getUsername())){
                /**
                 * 将获取到的用户信息封装成AuthticationInfo对象返回，此处封装成SimpleAuthticationInfo对象
                 * 参数一：认证的实体信息，可以是从数据库中查询得到的实体类或用户名
                 * 参数二：查询获得的登陆密码(md5 + salt)
                 * 参数三：盐值(随即盐)
                 * 参数四：当前Realm对象的名称，直接调用父类的getName()方法即可
                 */
                String userName = user.getUsername();
                return new SimpleAuthenticationInfo(userName, user.getPassword(),  new MyByteSource(user.getSalt()), this.getName());
            }
        }
        return null;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }
}
