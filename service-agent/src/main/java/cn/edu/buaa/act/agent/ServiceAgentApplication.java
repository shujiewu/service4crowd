package cn.edu.buaa.act.agent;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * @author wsj
 */
@SpringBootApplication
@EnableEurekaClient
@EnableAutoConfiguration
@EnableAsync
public class ServiceAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceAgentApplication.class, args);
    }
}
