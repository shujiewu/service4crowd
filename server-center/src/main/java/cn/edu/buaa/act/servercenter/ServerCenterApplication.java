package cn.edu.buaa.act.servercenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author wsj
 */
@EnableEurekaServer
@SpringBootApplication
public class ServerCenterApplication {
	public static void main(String[] args) {
		SpringApplication.run(ServerCenterApplication.class, args);
	}
}
