package com.zxq.transaction;

import com.alibaba.fastjson.JSONObject;
import com.zxq.transaction.netty.NettyClient;
import com.zxq.transaction.util.SpringContextUtil;

import java.util.UUID;

/**
 * 记录事务组
 *
 * @author zhaoxiqing
 */
public class ATTransactionServerManager {
    private static ATTransaction atTransaction;

    private static NettyClient nettyClient = (NettyClient) SpringContextUtil.getBean("nettyClient");

    private static String groupId;

    /**
     * 创建AT事务
     */
    public static String createATTransactionGroup() {
        String groupId = getTransactionGroupId();

        JSONObject transaction = new JSONObject();
        transaction.put("command", "create");
        transaction.put("groupId", groupId);

        nettyClient.writeMsg(transaction.toJSONString());
        return groupId;
    }

    /**
     * 创建事务
     *
     * @param groupId
     * @param ATTransactionType
     * @return
     */
    public static ATTransaction createATTransaction(String groupId, ATTransactionType ATTransactionType) {
        String transactionId = getTransactionGroupId();
        ATTransaction atTransaction = new ATTransaction(transactionId, groupId, ATTransactionType);
        return atTransaction;
    }

    /**
     * 新增事务
     *
     * @param atTransaction
     */
    public static void addATTransaction(ATTransaction atTransaction) {
        JSONObject transaction = new JSONObject();
        transaction.put("groupId", atTransaction.getGroupId());
        transaction.put("transactionId", atTransaction.getTransactionId());
        transaction.put("transactionType", atTransaction.getATTransactionType());
        transaction.put("command", "add");

        ATTransactionServerManager.atTransaction = atTransaction;

        nettyClient.writeMsg(transaction.toJSONString());
    }

    public static ATTransaction getATTransaction(String groupId) {
        return ATTransactionServerManager.atTransaction;
    }


    /**
     * 获取事务组ID
     *
     * @return
     */
    public static String getTransactionGroupId() {
        return UUID.randomUUID().toString();
    }

    public static String getGroupId() {
        return groupId;
    }

    public static void setGroupId(String groupId) {
        ATTransactionServerManager.groupId = groupId;
    }
}
