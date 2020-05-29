package com.zxq.transaction.aop;

import com.zxq.transaction.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分布式事务注解切面
 *
 * @author ZhaoXQ
 */
@Slf4j
@Aspect
@Component
public class ATTransactionAspect implements Ordered {
    private ATConnectionCache atConnectionCache;

    public ATTransactionAspect(ATConnectionCache atConnectionCache) {
        this.atConnectionCache = atConnectionCache;
    }

    @Around("@annotation(com.zxq.transaction.annnoction.ATTransaction)")
    public void around(ProceedingJoinPoint point) {
        String groupId = ATTransactionServerManager.getGroupId();
        if (groupId == null || "".equals(groupId)) {
            groupId = ATTransactionServerManager.createATTransactionGroup();
        }

        log.info("开始切面" + groupId);

        ATTransaction atTransaction;
        try {
            atTransaction = ATTransactionServerManager.createATTransaction(groupId, ATTransactionType.COMMIT);
            atConnectionCache.setGroupId(groupId);
            point.proceed();

            //处理切面事务冲突
            if (!groupId.equals(atConnectionCache.getGroupId())) {
                log.info("处理切面事务冲突：" + groupId + "/" + atConnectionCache.getGroupId());
            }
        } catch (Throwable throwable) {
            atTransaction = ATTransactionServerManager.createATTransaction(groupId, ATTransactionType.ROLLBACK);
            throwable.printStackTrace();
        } finally {
            ATTransactionServerManager.clearGroupId();
        }

        ATTransactionServerManager.addATTransaction(atTransaction);
        log.info("结束切面" + groupId);
    }

    @Override
    public int getOrder() {
        return 10000;
    }
}
