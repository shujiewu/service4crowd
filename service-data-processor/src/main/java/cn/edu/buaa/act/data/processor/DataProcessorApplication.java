package cn.edu.buaa.act.data.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
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
public class DataProcessorApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataProcessorApplication.class, args);
    }
}
