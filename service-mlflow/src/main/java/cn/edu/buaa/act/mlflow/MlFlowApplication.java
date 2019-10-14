package cn.edu.buaa.act.mlflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * MlFlowApplication
 *
 * @author wsj
 * @date 2018/10/8
 */
@EnableAsync
@SpringBootApplication
@EnableEurekaClient
public class MlFlowApplication {
    public static void main(String[] args) {
        SpringApplication.run(MlFlowApplication.class, args);
    }
}
