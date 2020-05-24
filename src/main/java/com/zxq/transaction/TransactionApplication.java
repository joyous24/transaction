package com.zxq.transaction;

import com.zxq.transaction.netty.NettyClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

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
    public NettyClient nettyClient() {
        NettyClient nettyClient = new NettyClient("127.0.0.1", 20803);
        nettyClient.start();
        return nettyClient;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(TransactionApplication.class, args);
    }

}
