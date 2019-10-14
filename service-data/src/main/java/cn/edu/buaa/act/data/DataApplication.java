package cn.edu.buaa.act.data;

import cn.edu.buaa.act.auth.client.EnableAuthClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * DataApplication
 *
 * @author wsj
 * @date 2018/10/19
 */

@SpringBootApplication
@EnableEurekaClient
@EnableAuthClient
@EnableFeignClients({"cn.edu.buaa.act.auth.client.feign"})
public class DataApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataApplication.class, args);
    }
}
