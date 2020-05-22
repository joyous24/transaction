package com.zxq.transaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 记录事务组
 *
 * @author zhaoxiqing
 */
public class RecordTransactionGroupUtil {
    private static Map<String, ATTransactionGroup> recordGransactionGroupMap = new ConcurrentHashMap<>(16);

    public static void put(String groupId, ATTransactionGroup atTransactionGroup) {
        recordGransactionGroupMap.put(groupId, atTransactionGroup);
    }

    public static ATTransactionGroup get(String groupId) {
        return recordGransactionGroupMap.get(groupId);
    }

    public static Map<String, ATTransactionGroup> getRecordGransactionGroupMap() {
        return recordGransactionGroupMap;
    }
}
