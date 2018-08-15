package com.software.hdfs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * 描述：
 *
 * @ClassName HdfsApplication
 * @Author 徐旭
 * @Date 2018/8/13 15:04
 * @Version 1.0
 */
@SpringBootApplication
@PropertySource(value = {"classpath:/application.yml"}, encoding = "utf-8")
@MapperScan("com.software.hdfs.dao")
public class HdfsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HdfsApplication.class, args);
	}
}
