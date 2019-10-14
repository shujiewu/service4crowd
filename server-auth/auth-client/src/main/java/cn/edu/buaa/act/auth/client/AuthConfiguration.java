package cn.edu.buaa.act.auth.client;

import cn.edu.buaa.act.auth.client.config.ClientConfig;
import cn.edu.buaa.act.auth.client.config.UserAuthConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan({"cn.edu.buaa.act.auth.client"})
public class AuthConfiguration {

    @Bean
    UserAuthConfig getUserAuthConfig(){
        return new UserAuthConfig();
    }

    @Bean
    ClientConfig getClientConfig(){
        return new ClientConfig();
    }
}
