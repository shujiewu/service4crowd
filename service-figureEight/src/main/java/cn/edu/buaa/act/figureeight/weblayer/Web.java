package cn.edu.buaa.act.figureeight.weblayer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;


@Target({ElementType.METHOD})

public @interface Web {

    String info() default "[ Web Call] to crowdflower";
}
