package cn.edu.buaa.act.test.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    private Integer id;
    private String username;
    private String password;
    private String type;
}