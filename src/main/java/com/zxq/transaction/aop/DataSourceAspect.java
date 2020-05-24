package com.zxq.transaction.aop;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.zxq.transaction.ATConnection;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 切面获取DataSource
 *
 * @author ZhaoXQ
 */
@Aspect
@Component
public class DataSourceAspect {

    /**
     * 获取Connection
     *
     * @param point
     * @return
     * @throws SQLException
     */
    @Around("execution(* javax.sql.DataSource.getConnection(..))")
    public Connection around(ProceedingJoinPoint point) throws Throwable {
        //从数据源获取Connection
        DruidPooledConnection druidPooledConnection = (DruidPooledConnection) point.proceed();
        Connection conn = druidPooledConnection.getConnection();
        ATConnection atConnection = new ATConnection(conn);
        return atConnection;
    }

    /**
     * 异常回滚
     *
     * @param joinPoint
     * @param ex
     */
    @AfterThrowing(throwing = "ex", pointcut = "execution(* com.zxq.transaction.service.*.*(..))")
    public void exceptionDispose(JoinPoint joinPoint, Throwable ex) {
        // 切入方法所属类名
        String className = joinPoint.getTarget().getClass().getName();
        // 切入的方法名
        String methodName = joinPoint.getSignature().getName();

        System.out.println("报错类:" + className + "." + methodName + "(),异常信息：" + ex);
    }
}
