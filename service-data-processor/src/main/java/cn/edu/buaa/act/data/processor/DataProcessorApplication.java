package cn.edu.buaa.act.data.processor;

import cn.edu.buaa.act.auth.client.EnableAuthClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * DataApplication
 *
 * @author wsj
 * @date 2018/10/19
 */

@SpringBootApplication
@EnableEurekaClient
@EnableAsync
@EnableAuthClient
@EnableFeignClients({"cn.edu.buaa.act.auth.client.feign","cn.edu.buaa.act.data.processor.feign"})
public class DataProcessorApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataProcessorApplication.class, args);
    }
}
