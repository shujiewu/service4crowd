package cn.edu.buaa.act.workflow.config;

import cn.edu.buaa.act.auth.client.config.UserAuthConfig;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

//@Configuration
//public class FeignConfiguration implements RequestInterceptor {
//
//    @Autowired
//    private UserAuthConfig userAuthConfig;
//    private HttpServletRequest getHttpServletRequest() {
//        try {
//            // 这种方式获取的HttpServletRequest是线程安全的
//            return ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
//        } catch (Exception e) {
//            return null;
//        }
//    }
//    @Override
//    public void apply(RequestTemplate requestTemplate) {
//        HttpServletRequest request = getHttpServletRequest();
//        if (Objects.isNull(request)) {
//            return;
//        }
//        requestTemplate.header(userAuthConfig.getTokenHeader(),request.getHeader(userAuthConfig.getTokenHeader()));
//    }
//}

//@Configuration // 加上该注解 ，则不需要FeignClient里面加属性configuration
//public class FeignConfiguration implements RequestInterceptor {
//
//    @Override public void apply(RequestTemplate template) {
//
//        HttpServletRequest request = getHttpServletRequest();
//
//        if (Objects.isNull(request)) {
//            return;
//        }
//
//        Map<String, String> headers = getHeaders(request);
//        if (headers.size() > 0) {
//            Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
//            while (iterator.hasNext()) {
//                Map.Entry<String, String> entry = iterator.next();
//                // 把请求过来的header请求头 原样设置到feign请求头中
//                // 包括token
//                template.header(entry.getKey(), entry.getValue());
//            }
//        }
//    }
//
//    private HttpServletRequest getHttpServletRequest() {
//
//        try {
//            // 这种方式获取的HttpServletRequest是线程安全的
//            return ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
//        } catch (Exception e) {
//
//            return null;
//        }
//    }
//
//    private Map<String, String> getHeaders(HttpServletRequest request) {
//
//        Map<String, String> map = new LinkedHashMap<>();
//        Enumeration<String> enums = request.getHeaderNames();
//        while (enums.hasMoreElements()) {
//            String key = enums.nextElement();
//            String value = request.getHeader(key);
//            map.put(key, value);
//        }
//        return map;
//    }
//}