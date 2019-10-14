package cn.edu.buaa.act.servergate;

import cn.edu.buaa.act.auth.client.EnableAuthClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableZuulProxy
@SpringBootApplication
@EnableAuthClient
@EnableDiscoveryClient
@EnableFeignClients({"cn.edu.buaa.act.auth.client"})
@EnableScheduling
public class ServerGateApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerGateApplication.class, args);
	}
}
