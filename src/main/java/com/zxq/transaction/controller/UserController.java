package com.zxq.transaction.controller;

import com.zxq.transaction.model.User;
import com.zxq.transaction.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 控制层
 *
 * @author zxq
 */
@Slf4j
@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/saveUser")
    private Object saveUser() {
        log.info("==============saveUser=============");
        userService.save();
        return true;
    }

    @RequestMapping("/saveUser2")
    private Object saveUser2() {
        log.info("==============saveUser 222222=============");
        userService.save2();
        return true;
    }
}
