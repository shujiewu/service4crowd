package cn.edu.buaa.act.auth.center.config;

import cn.edu.buaa.act.auth.center.handler.ExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Collections;


/**
 * @author wsj
 */
@Configuration("WebConfig")
@Primary
public class WebConfiguration implements WebMvcConfigurer {
    @Bean
    ExceptionHandler getExceptionHandler() {
        return new ExceptionHandler();
    }

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(getUserAuthRestInterceptor()).addPathPatterns(getIncludePathPatterns());
//    }
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
