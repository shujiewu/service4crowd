package cn.edu.buaa.act.auth.client;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(AuthConfiguration.class)
@Documented
@Inherited
public @interface EnableAuthClient {
}
