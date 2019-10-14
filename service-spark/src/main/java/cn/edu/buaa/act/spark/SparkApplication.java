package cn.edu.buaa.act.spark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * @author wsj
 */
@EnableAsync
@SpringBootApplication
public class SparkApplication {
	public static void main(String[] args) {
		SpringApplication.run(SparkApplication.class, args);
	}
}
