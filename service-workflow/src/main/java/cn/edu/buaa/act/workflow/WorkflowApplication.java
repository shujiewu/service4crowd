package cn.edu.buaa.act.workflow;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import cn.edu.buaa.act.auth.client.EnableAuthClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * @author wsj
 */
@EnableEurekaClient
@EnableAuthClient
@EnableFeignClients({"cn.edu.buaa.act.auth.client.feign","cn.edu.buaa.act.workflow.feign"})
@SpringBootApplication
@EnableAutoConfiguration(exclude={LiquibaseAutoConfiguration.class,org.activiti.spring.boot.SecurityAutoConfiguration.class})
@EnableScheduling
public class WorkflowApplication {
	public static void main(String[] args) {
		SpringApplication.run(WorkflowApplication.class, args);
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		RestTemplate restTemplate = new RestTemplate();
		// restTemplate.setInterceptors(Collections.singletonList(new U()));
		return new RestTemplate();
	}
}
