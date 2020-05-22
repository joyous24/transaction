package com.zxq.transaction.controller;

import com.zxq.transaction.ATConnection;
import com.zxq.transaction.RecordTransactionGroupUtil;
import com.zxq.transaction.model.User;
import com.zxq.transaction.netty.NettyClient;
import com.zxq.transaction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

/**
 * 控制层
 *
 * @author zxq
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private NettyClient nettyClient;

    @RequestMapping("/saveUser")
    private Object saveUser() {
        User user = new User();
        user.setUserName("李三");
        user.setAge(30);

        userService.save(user);
        return user;
    }

    @RequestMapping("/nettyMsg")
    private Object nettyMsg() {
        nettyClient.writeMsg("来自客户端的消息");
        return true;
    }

    @RequestMapping("/rollbackUser")
    private Object rollbackUser() {
        RecordTransactionGroupUtil.getRecordGransactionGroupMap().forEach((k, v) -> {
            ATConnection connection = v.getCurrentConnection();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
        return true;
    }
}
