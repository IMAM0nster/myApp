package com.example.service.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by fy on 2017/12/29.
 */
@Controller
public class UserController {

    protected static Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping("/login")
    public String userLogin(String username, String password){
        System.out.println(username+" "+password);
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject subject = SecurityUtils.getSubject();

        try {
            subject.login(token);
            subject.getSession().setAttribute("username", username);
            return "redirect:/";
        }catch (Exception e){
            return "login";
        }

    }
}
