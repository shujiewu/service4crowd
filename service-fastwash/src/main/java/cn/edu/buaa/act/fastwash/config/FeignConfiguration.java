package cn.edu.buaa.act.fastwash.config;

import cn.edu.buaa.act.auth.client.config.UserAuthConfig;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


public class FeignConfiguration implements RequestInterceptor {

    @Autowired
    private UserAuthConfig userAuthConfig;
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        requestTemplate.header(userAuthConfig.getTokenHeader(),request.getHeader(userAuthConfig.getTokenHeader()));
    }
}
