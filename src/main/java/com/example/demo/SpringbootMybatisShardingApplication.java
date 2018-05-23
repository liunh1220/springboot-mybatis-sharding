package com.example.demo;

import com.example.demo.config.BaseConfiguration;
import com.example.demo.db.DataSourceConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@Import({BaseConfiguration.class,DataSourceConfiguration.class})
@EnableAsync
public class SpringbootMybatisShardingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootMybatisShardingApplication.class, args);
	}
}
