package com.zxq.transaction.controller;

import com.zxq.transaction.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
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

    private RedissonClient redissonClient;

    @Autowired
    public UserController(UserService userService, RedissonClient redissonClient) {
        this.userService = userService;
        this.redissonClient = redissonClient;
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

    @RequestMapping("/clock")
    private void clock() throws InterruptedException {
        // RLock 继承了 java.util.concurrent.locks.Lock 接口
        RLock lock = redissonClient.getLock("lock");

        lock.lock();
        System.out.println("lock acquired");

        Thread t = new Thread(() -> {
            RLock lock1 = redissonClient.getLock("lock");
            lock1.lock();
            System.out.println("lock acquired by thread");
            lock1.unlock();
            System.out.println("lock released by thread");
        });

        t.start();
        t.join(1000);

        lock.unlock();
        System.out.println("lock released");

        t.join();

        redissonClient.shutdown();
    }
}
