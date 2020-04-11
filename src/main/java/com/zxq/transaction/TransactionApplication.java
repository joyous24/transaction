package com.zxq.transaction;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *	系统服务运行入口
 * @author zhaoxq
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.zxq.transaction.mapper")
public class TransactionApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionApplication.class, args);
	}

}
