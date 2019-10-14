package cn.edu.buaa.act.auth.center;


import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author wsj
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan("cn.edu.buaa.act.auth.center.dao")
@EnableAutoConfiguration
@EnableScheduling
@EnableSwagger2Doc
public class AuthCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthCenterApplication.class, args);
    }
}
