package com.xxl.job.admin;

import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author xuxueli 2018-10-28 00:38:13
 */
@EnableDiscoveryClient //开启服务注册与发现
@EnableTransactionManagement
@EnableAutoDataSourceProxy //mybatis 如果要集成seata需要开启数据源自动代理
@SpringBootApplication
public class XxlJobAdminApplication {

	public static void main(String[] args) {
        SpringApplication.run(XxlJobAdminApplication.class, args);
	}

}