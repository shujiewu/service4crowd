package cn.edu.buaa.act.fastwash;


import cn.edu.buaa.act.auth.client.EnableAuthClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@EnableAuthClient
public class FastWashApplication {
    public static void main(String[] args) {
        SpringApplication.run(FastWashApplication.class, args);
    }
}
