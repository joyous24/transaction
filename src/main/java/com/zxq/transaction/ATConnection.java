package com.zxq.transaction;

import lombok.extern.slf4j.Slf4j;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 接管connection
 *
 * @author zhaoxq
 */
@Slf4j
public class ATConnection implements Connection {
    private Connection atConn;

    private ATTransaction atTransaction = ATTransactionServerManager.getATTransaction("");

    public ATConnection(Connection atConn) {
        this.atConn = atConn;
    }

    /**
     * 创建一个 Statement对象，用于将SQL语句发送到数据库
     *
     * @return
     * @throws SQLException
     */
    @Override
    public Statement createStatement() throws SQLException {
        return atConn.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return atConn.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return atConn.prepareCall(sql);
    }

    /**
     * 将给定的SQL语句转换为系统的本机SQL语法
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    @Override
    public String nativeSQL(String sql) throws SQLException {
        return atConn.nativeSQL(sql);
    }

    /**
     * 自动提交
     *
     * @param autoCommit
     * @throws SQLException
     */
    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        log.info("已取消设置自动提交");
        atConn.setAutoCommit(false);
    }

    /**
     * 检索此 Connection对象的当前自动提交模式
     *
     * @return
     * @throws SQLException
     */
    @Override
    public boolean getAutoCommit() throws SQLException {
        boolean autoCommit = atConn.getAutoCommit();
        log.info("获取自动提交属性" + autoCommit);
        return autoCommit;
    }

    /**
     * 使自上次提交/回滚以来所做的所有更改都将永久性，并释放此 Connection对象当前持有的任何数据库锁
     *
     * @throws SQLException
     */
    @Override
    public void commit() throws SQLException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    atTransaction.getTask().waitTask();

                    if (atTransaction.getATTransactionType().equals(ATTransactionType.COMMIT)) {
                        atConn.commit();
                    } else {
                        atConn.rollback();
                    }

                    atConn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 撤消在当前事务中所做的所有更改，并释放此 Connection对象当前持有的任何数据库锁
     *
     * @throws SQLException
     */
    @Override
    public void rollback() throws SQLException {
    }

    /**
     * Connection发布此 Connection对象的数据库和JDBC资源，而不是等待它们自动释放
     *
     * @throws SQLException
     */
    @Override
    public void close() throws SQLException {
    }

    /**
     * 检索此 Connection对象是否已关闭
     *
     * @return
     * @throws SQLException
     */
    @Override
    public boolean isClosed() throws SQLException {
        boolean isClosed = atConn.isClosed();
        if (isClosed) {
            log.info("事务已关闭");
        }
        return isClosed;
    }

    /**
     * 检索 DatabaseMetaData对象包含有关哪个这个数据库的元数据 Connection对象表示的连接
     *
     * @return
     * @throws SQLException
     */
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return atConn.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        atConn.setReadOnly(readOnly);
    }

    /**
     * 检索此 Connection对象是否处于只读模式
     *
     * @return
     * @throws SQLException
     */
    @Override
    public boolean isReadOnly() throws SQLException {
        return atConn.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        atConn.setCatalog(catalog);
    }

    /**
     * 检索此 Connection对象的当前目录名称
     *
     * @return
     * @throws SQLException
     */
    @Override
    public String getCatalog() throws SQLException {
        return atConn.getCatalog();
    }

    /**
     * 尝试将此 Connection对象的事务隔离级别更改为给定的对象
     *
     * @param level
     * @throws SQLException
     */
    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        atConn.setTransactionIsolation(level);
    }

    /**
     * 获取此 Connection对象的当前事务隔离级别
     *
     * @return
     * @throws SQLException
     */
    @Override
    public int getTransactionIsolation() throws SQLException {
        return atConn.getTransactionIsolation();
    }

    /**
     * 检索通过此 Connection对象的呼叫报告的第一个警告
     *
     * @return
     * @throws SQLException
     */
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return atConn.getWarnings();
    }

    /**
     * 清除为此 Connection对象报告的所有警告
     *
     * @throws SQLException
     */
    @Override
    public void clearWarnings() throws SQLException {
        atConn.clearWarnings();
    }

    /**
     * 创建一个 Statement对象，将产生 ResultSet对象具有给定类型，并发性和可保存性
     *
     * @param resultSetType
     * @param resultSetConcurrency
     * @return
     * @throws SQLException
     */
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return atConn.createStatement(resultSetType, resultSetConcurrency);
    }

    /**
     * 创建一个 PreparedStatement对象，用于将参数化的SQL语句发送到数据库
     *
     * @param sql
     * @param resultSetType
     * @param resultSetConcurrency
     * @return
     * @throws SQLException
     */
    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return atConn.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    /**
     * 创建一个 CallableStatement对象，该对象将生成具有给定类型和并发性的 ResultSet对象
     *
     * @param sql
     * @param resultSetType
     * @param resultSetConcurrency
     * @return
     * @throws SQLException
     */
    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return atConn.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    /**
     * 检索 Map与此相关联的对象 Connection对象
     *
     * @return
     * @throws SQLException
     */
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return atConn.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        atConn.setTypeMap(map);
    }

    /**
     * 将使用此 Connection对象创建的 ResultSet对象的默认保持性更改为给定的可保存性
     *
     * @param holdability
     * @throws SQLException
     */
    @Override
    public void setHoldability(int holdability) throws SQLException {
        atConn.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return atConn.getHoldability();
    }

    /**
     * 在当前事务中创建一个未命名的保存点，并返回代表它的新的 Savepoint对象
     *
     * @return
     * @throws SQLException
     */
    @Override
    public Savepoint setSavepoint() throws SQLException {
        return atConn.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return atConn.setSavepoint(name);
    }

    /**
     * 撤消在当前事务中所做的所有更改，并释放此 Connection对象当前持有的任何数据库锁
     *
     * @param savepoint
     * @throws SQLException
     */
    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        atConn.rollback(savepoint);
    }

    /**
     * 删除指定的 Savepoint和随后 Savepoint从目前的交易对象
     *
     * @param savepoint
     * @throws SQLException
     */
    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        atConn.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return atConn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return atConn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    /**
     * 创建一个 CallableStatement对象，该对象将生成具有给定类型和并发性的 ResultSet对象
     *
     * @param sql
     * @param resultSetType
     * @param resultSetConcurrency
     * @param resultSetHoldability
     * @return
     * @throws SQLException
     */
    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return atConn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return atConn.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return atConn.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return atConn.prepareStatement(sql, columnNames);
    }

    /**
     * 构造实现的对象 Clob接口
     *
     * @return
     * @throws SQLException
     */
    @Override
    public Clob createClob() throws SQLException {
        return atConn.createClob();
    }

    /**
     * 构造实现的对象 Blob接口
     *
     * @return
     * @throws SQLException
     */
    @Override
    public Blob createBlob() throws SQLException {
        return atConn.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return atConn.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return atConn.createSQLXML();
    }

    /**
     * 如果连接尚未关闭并且仍然有效，则返回true
     *
     * @param timeout
     * @return
     * @throws SQLException
     */
    @Override
    public boolean isValid(int timeout) throws SQLException {
        return atConn.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        atConn.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        atConn.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return atConn.getClientInfo(name);
    }

    /**
     * 返回包含驱动程序支持的每个客户端信息属性的名称和当前值的列表
     *
     * @return
     * @throws SQLException
     */
    @Override
    public Properties getClientInfo() throws SQLException {
        return atConn.getClientInfo();
    }

    /**
     * 用于创建Array对象的Factory方法
     *
     * @param typeName
     * @param elements
     * @return
     * @throws SQLException
     */
    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return atConn.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return atConn.createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        atConn.setSchema(schema);
    }

    /**
     * 检索此 Connection对象的当前模式名称
     *
     * @return
     * @throws SQLException
     */
    @Override
    public String getSchema() throws SQLException {
        return atConn.getSchema();
    }

    /**
     * 终止打开的连接
     *
     * @param executor
     * @throws SQLException
     */
    @Override
    public void abort(Executor executor) throws SQLException {
        atConn.abort(executor);
    }

    /**
     * 设置最大周期a Connection或从 Connection创建的 Connection将等待数据库回复任何一个请求
     *
     * @param executor
     * @param milliseconds
     * @throws SQLException
     */
    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        atConn.setNetworkTimeout(executor, milliseconds);
    }

    /**
     * 检索驱动程序等待数据库请求完成的毫秒数
     *
     * @return
     * @throws SQLException
     */
    @Override
    public int getNetworkTimeout() throws SQLException {
        return atConn.getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return atConn.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return atConn.isWrapperFor(iface);
    }
}
