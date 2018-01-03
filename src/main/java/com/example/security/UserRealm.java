package com.example.security;


import com.example.dao.mapper.UserMapper;
import com.example.entity.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by fy on 2017/12/29.
 */
@Component
public class UserRealm extends AuthorizingRealm {

    @Autowired
    UserMapper userMapper;


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String)principals.getPrimaryPrincipal();
        User user = userMapper.getByName(username);
        String role = user.getRole();
        Set<String> permissions = new HashSet<>();
        if("user".equals(role)){
            permissions.add("");
        } else if("admin".equals(role)){
            permissions.add("");
        }
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRole(role);
        info.addStringPermissions(permissions);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken)token;
        String username = upToken.getUsername();
        User user = userMapper.getByName(username);
        if(user == null)
            return null;
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user.getName(),user.getPassword(), getName());
        return info;
    }

}
