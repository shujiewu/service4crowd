package cn.edu.buaa.act.mongodb.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello
 *
 * @author wsj
 * @date 2018/10/6
 */
@RestController
public class Hello {

    @RequestMapping("/hello")
    public String hello(){
        return "heloo";
    }
}
