package cn.edu.buaa.act.model.detection;


import cn.edu.buaa.act.auth.client.EnableAuthClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableAuthClient
@EnableFeignClients({"cn.edu.buaa.act.auth.client.feign"})
public class DetectionModelApplication {
    public static void main(String[] args) {
        SpringApplication.run(DetectionModelApplication.class, args);
    }
}
