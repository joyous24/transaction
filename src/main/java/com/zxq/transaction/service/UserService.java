package com.zxq.transaction.service;

import com.zxq.transaction.annnoction.ATTransaction;
import com.zxq.transaction.mapper.UserMapper;
import com.zxq.transaction.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 * 服务层
 *
 * @author zhaoxq
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userDao;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    @ATTransaction
    public void save() {
        User user = new User();
        user.setUserName("李三");
        user.setAge(30);
        userDao.insert(user);

        User user2 = new User();
        user2.setUserName("李四");
        user2.setAge(31);
        restTemplate.postForEntity("", user2, String.class);
    }

}
