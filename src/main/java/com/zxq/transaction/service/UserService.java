package com.zxq.transaction.service;

import com.zxq.transaction.mapper.UserMapper;
import com.zxq.transaction.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务层
 *
 * @author zhaoxq
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userDao;

    @Transactional
    public void save(User user) {
        userDao.insert(user);

        throw new RuntimeException("回滚用户");
    }

}
