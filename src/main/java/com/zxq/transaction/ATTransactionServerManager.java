package com.zxq.transaction;

import java.util.UUID;

/**
 * 记录事务组
 *
 * @author zhaoxiqing
 */
public class ATTransactionServerManager {

    /**
     * 创建AT事务
     */
    public static void createATTransaction() {
        String groupId = getTransactionGroupId();


    }

    /**
     * 获取事务组ID
     *
     * @return
     */
    public static String getTransactionGroupId() {
        return UUID.randomUUID().toString();
    }
}
