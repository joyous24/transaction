package com.zxq.transaction;

/**
 * AT事务组
 *
 * @author zhaoxiqing
 */
public class ATTransactionGroup {
    /**
     * 事务组标识ID
     */
    private String groupId;

    /**
     * 事务枚举
     */
    private TransactionEnum transactionEnum;

    /**
     * 当前数据库事务连接
     */
    private ATConnection currentConnection;


}
