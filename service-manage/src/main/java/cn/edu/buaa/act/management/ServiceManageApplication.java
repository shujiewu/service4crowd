package cn.edu.buaa.act.management;


import cn.edu.buaa.act.auth.client.EnableAuthClient;
import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
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
@EnableAuthClient
@EnableFeignClients({"cn.edu.buaa.act.auth.client.feign"})
public class ServiceManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceManageApplication.class, args);
    }
}
