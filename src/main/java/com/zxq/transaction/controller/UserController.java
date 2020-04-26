package com.zxq.transaction.controller;

import com.zxq.transaction.model.User;
import com.zxq.transaction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 控制层
 * @author zxq
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/saveUser")
    private Object saveUser(){
        User user = new User();
        user.setUserName("李三");
        user.setAge(30);

        userService.save(user);
        return user;
    }
}
