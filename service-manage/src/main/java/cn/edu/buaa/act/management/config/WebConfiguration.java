package cn.edu.buaa.act.management.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Collections;

import static org.aspectj.weaver.Shadow.ExceptionHandler;


@Configuration("WebConfig")
@Primary
public class WebConfiguration implements WebMvcConfigurer {


    @Autowired
    UserAuthRestInterceptor userAuthRestInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        System.out.println(1212121212);
        registry.addInterceptor(userAuthRestInterceptor).addPathPatterns(getIncludePathPatterns());
    }
//
//    @Bean
//    UserAuthRestInterceptor getUserAuthRestInterceptor() {
//        return new UserAuthRestInterceptor();
//    }

    /**
     * 需要用户和服务认证判断的路径
     * @return
     */
    private ArrayList<String> getIncludePathPatterns() {
        ArrayList<String> list = new ArrayList<>();
        String[] urls = {
                "/user/login"
        };
        Collections.addAll(list, urls);
        return list;
    }
}
