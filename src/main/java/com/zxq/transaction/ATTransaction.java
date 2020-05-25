package com.zxq.transaction;

import com.zxq.transaction.util.Task;

/**
 * AT事务组
 *
 * @author zhaoxiqing
 */
public class ATTransaction {
    /**
     * 事务组标识ID
     */
    private String groupId;

    /**
     * 事务ID
     */
    private String transactionId;

    /**
     * 事务枚举
     */
    private ATTransactionType ATTransactionType;

    /**
     * 锁
     */
    private Task task;


    public ATTransaction(String transactionId, String groupId, ATTransactionType ATTransactionType) {
        this.transactionId = transactionId;
        this.groupId = groupId;
        this.ATTransactionType = ATTransactionType;
        this.task = new Task();
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public ATTransactionType getATTransactionType() {
        return ATTransactionType;
    }

    public void setATTransactionType(ATTransactionType ATTransactionType) {
        this.ATTransactionType = ATTransactionType;
    }

    public Task getTask() {
        return task;
    }
}
