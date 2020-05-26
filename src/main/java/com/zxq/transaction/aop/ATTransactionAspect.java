package com.zxq.transaction.aop;

import com.zxq.transaction.ATTransaction;
import com.zxq.transaction.ATTransactionServerManager;
import com.zxq.transaction.ATTransactionType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 分布式事务注解切面
 *
 * @author ZhaoXQ
 */
@Slf4j
@Aspect
@Component
public class ATTransactionAspect implements Ordered {

    @Around("@annotation(com.zxq.transaction.annnoction.ATTransaction)")
    public void around(ProceedingJoinPoint point) {
        String groupId = ATTransactionServerManager.createATTransactionGroup();

        log.info("开始切面" + groupId);
        ATTransactionServerManager.setGroupId(groupId);

        ATTransaction atTransaction;
        try {
            point.proceed();
            atTransaction = ATTransactionServerManager.createATTransaction(groupId, ATTransactionType.COMMIT);
        } catch (Throwable throwable) {
            atTransaction = ATTransactionServerManager.createATTransaction(groupId, ATTransactionType.ROLLBACK);
            throwable.printStackTrace();
        }
        ATTransactionServerManager.addATTransaction(atTransaction);

        log.info("结束切面" + groupId);
    }

    @Override
    public int getOrder() {
        return 10000;
    }
}
