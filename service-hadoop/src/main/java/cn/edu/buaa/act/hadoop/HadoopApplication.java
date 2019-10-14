package cn.edu.buaa.act.hadoop;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;


/**
 * @author wsj
 */
@SpringBootApplication
public class HadoopApplication {
	public static void main(String[] args) {
		SpringApplication.run(HadoopApplication.class, args);
	}
}
