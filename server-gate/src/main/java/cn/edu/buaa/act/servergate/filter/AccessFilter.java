package cn.edu.buaa.act.servergate.filter;

import cn.edu.buaa.act.auth.client.config.UserAuthConfig;
import cn.edu.buaa.act.auth.client.util.UserAuthUtil;
import cn.edu.buaa.act.auth.common.exception.TokenErrorResponse;
import cn.edu.buaa.act.auth.common.util.IJWTInfo;
import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.common.util.ClientUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.alibaba.fastjson.JSON;

/**
 *
 * @author wsj
 */
@Component
@Slf4j
public class AccessFilter extends ZuulFilter {

    @Value("${gate.ignore.startWith}")
    private String startWith;

    @Value("${zuul.prefix}")
    private String zuulPrefix;

    @Autowired
    private UserAuthConfig userAuthConfig;

    @Autowired
    private UserAuthUtil userAuthUtil;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        final String method = request.getMethod();
        log.info("send {} request to {}",method,request.getRequestURL().toString());
        final String requestUri = request.getRequestURI().substring(zuulPrefix.length());

        HttpServletResponse response = ctx.getResponse();
        // 这些是对请求头的匹配，网上有很多解释
        response.setHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials","true");
        response.setHeader("Access-Control-Allow-Methods","GET, HEAD, POST, PUT, DELETE, OPTIONS, PATCH");
        response.setHeader("Access-Control-Allow-Headers","authorization, content-type");
        response.setHeader("Access-Control-Expose-Headers","X-forwared-port, X-forwarded-host");
        response.setHeader("Vary","Origin,Access-Control-Request-Method,Access-Control-Request-Headers");

        // 跨域请求一共会进行两次请求 先发送options 是否可以请求
        // 调试可发现一共拦截两次 第一次请求为options，第二次为正常的请求 eg：get请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())){
            ctx.setSendZuulResponse(false); //验证请求不进行路由
            ctx.setResponseStatusCode(HttpStatus.OK.value());//返回验证成功的状态码
            ctx.set("isSuccess", true);
            return null;
        }
        BaseContextHandler.setToken(null);
        // 不进行拦截的地址
        if (isStartWith(requestUri)) {
            return null;
        }
        IJWTInfo user = null;
        try {
            user = getJWTUser(request, ctx);
        } catch (Exception e) {
            setFailedRequest(JSON.toJSONString(new TokenErrorResponse(e.getMessage())), 200);
            return null;
        }
        /// System.out.println(BaseContextHandler.getToken());
        if(user!=null){
            ctx.addZuulRequestHeader(userAuthConfig.getTokenHeader(), BaseContextHandler.getToken());
            ctx.addZuulRequestHeader("userId", user.getId());
            ctx.addZuulRequestHeader("userName", URLEncoder.encode(user.getName()));
            ctx.addZuulRequestHeader("userHost", ClientUtil.getClientIp(ctx.getRequest()));
        }
        BaseContextHandler.remove();
        return null;
    }
    private IJWTInfo getJWTUser(HttpServletRequest request, RequestContext ctx) throws Exception {
        String authToken = request.getHeader(userAuthConfig.getTokenHeader());
        if (StringUtils.isBlank(authToken)) {
            authToken = request.getParameter("token");
        }
        /// System.out.println("1111"+authToken);
        BaseContextHandler.setToken(authToken);
        return userAuthUtil.getInfoFromToken(authToken);
    }

    private boolean isStartWith(String requestUri) {
        boolean flag = false;
        for (String s : startWith.split(",")) {
            if (requestUri.startsWith(s)) {
                return true;
            }
        }
        return flag;
    }

    private void setFailedRequest(String body, int code) {
        log.debug("Reporting error ({}): {}", code, body);
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setResponseStatusCode(code);
        if (ctx.getResponseBody() == null) {
            ctx.setResponseBody(body);
            ctx.setSendZuulResponse(false);  //不进行路由
        }
    }

//    private void setOptions(String body, int code) {
//        log.debug("Reporting error ({}): {}", code, body);
//        RequestContext ctx = RequestContext.getCurrentContext();
//        ctx.setResponseStatusCode(code);
//        ctx.getResponse().setHeader("Access-Control-Allow-Origin", "*");
//        ctx.getResponse().setHeader("Access-Control-Allow-Headers", "access-control-allow-origin, X-Requested-With, Content-Type, Accept, x-token");
//
//        if (ctx.getResponseBody() == null) {
//            ctx.setResponseBody(body);
//            ctx.setSendZuulResponse(false);  //不进行路由
//        }
//    }
}
