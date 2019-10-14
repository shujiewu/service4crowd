package cn.edu.buaa.act.management;


import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author wsj
 */
@SpringBootApplication
@EnableEurekaClient
@EnableAutoConfiguration
@EnableSwagger2Doc
@EnableAsync
public class ServiceManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceManageApplication.class, args);
    }
}
