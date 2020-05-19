package com.zxq.transaction;

import com.zxq.transaction.netty.NettyServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 系统服务运行入口
 *
 * @author zhaoxq
 */
@Configuration
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.zxq.transaction.mapper")
public class TransactionApplication {

    @Bean
    public NettyServer nettyServer() {
        NettyServer nettyServer = new NettyServer();
        nettyServer.bind();
        return nettyServer;
    }

    public static void main(String[] args) {
        SpringApplication.run(TransactionApplication.class, args);
    }

}
