package com.zxq.transaction;

import com.zxq.transaction.netty.NettyClient;
import com.zxq.transaction.util.SpringContextUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.RedissonRxClient;
import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

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
    public RedissonClient redisson() throws IOException {
        Config config = new Config();
        config.useClusterServers()
                .addNodeAddress("redis://127.0.0.1:6379");

        File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "redisson.yaml");
        config = Config.fromYAML(file);
        return Redisson.create(config);
    }

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
        ApplicationContext app = SpringApplication.run(TransactionApplication.class, args);
        SpringContextUtil.setApplicationContext(app);
    }

}
