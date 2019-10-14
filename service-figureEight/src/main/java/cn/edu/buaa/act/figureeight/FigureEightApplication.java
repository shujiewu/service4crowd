package cn.edu.buaa.act.figureeight;

import cn.edu.buaa.act.auth.client.EnableAuthClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * DataApplication
 *
 * @author wsj
 * @date 2018/10/19
 */

@SpringBootApplication
@EnableEurekaClient
@EnableAsync
@EnableScheduling
@EnableAuthClient
@EnableFeignClients({"cn.edu.buaa.act.auth.client.feign","cn.edu.buaa.act.figureeight.feign"})
public class FigureEightApplication {
    public static void main(String[] args) {
        SpringApplication.run(FigureEightApplication.class, args);
    }
}
