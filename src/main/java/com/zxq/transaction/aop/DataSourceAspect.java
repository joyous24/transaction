package com.zxq.transaction.aop;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.zxq.transaction.ATConnection;
import com.zxq.transaction.ATConnectionCache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
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
@Slf4j
@Aspect
@Component
public class DataSourceAspect {
    private ATConnectionCache atConnectionCache;

    public DataSourceAspect(ATConnectionCache atConnectionCache) {
        this.atConnectionCache = atConnectionCache;
    }

    /**
     * 获取Connection
     * <p>
     * 同一个事务只会产生一个【connection】，未开始事务时会产生多个【connection】。
     *
     * @param point
     * @return
     * @throws SQLException
     */
    @Around("execution(* javax.sql.DataSource.getConnection(..))")
    public Connection around(ProceedingJoinPoint point) throws Throwable {
        log.info("初始ATConnection：" + atConnectionCache.getGroupId());
        //从数据源获取Connection
        DruidPooledConnection druidPooledConnection = (DruidPooledConnection) point.proceed();
        Connection conn = druidPooledConnection.getConnection();
        ATConnection atConnection = new ATConnection(conn, atConnectionCache.getGroupId());
        return atConnection;
    }

}
