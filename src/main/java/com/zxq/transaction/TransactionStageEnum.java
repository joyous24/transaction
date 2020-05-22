package com.zxq.transaction;

/**
 * 事务阶段枚举
 *
 * @author zhaoxiqing
 */
public enum TransactionStageEnum {
    /**
     * 初始事务
     */
    INIT_COMMIT,

    /**
     * 准备提交事务
     */
    PREPARE_COMMIT,

    /**
     * 提交事务
     */
    DO_COMMIT;

    private String stageName;

    TransactionStageEnum() {
    }

}
