package com.zxq.transaction;

import java.sql.Connection;

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
     * 当前事务阶段
     */
    private TransactionStageEnum currentTransactionStageEnum;

    /**
     * 当前数据库事务连接
     */
    private ATConnection currentConnection;


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public ATConnection getCurrentConnection() {
        return currentConnection;
    }

    public void setCurrentConnection(ATConnection currentConnection) {
        this.currentConnection = currentConnection;
    }

    public TransactionStageEnum getCurrentTransactionStageEnum() {
        return currentTransactionStageEnum;
    }

    public void setCurrentTransactionStageEnum(TransactionStageEnum currentTransactionStageEnum) {
        this.currentTransactionStageEnum = currentTransactionStageEnum;
    }
}
