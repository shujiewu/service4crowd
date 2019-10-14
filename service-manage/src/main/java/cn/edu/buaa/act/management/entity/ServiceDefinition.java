package cn.edu.buaa.act.management.entity;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wsj
 */
@Getter
@Setter
@Entity
@Table(name = "service_definition")
public class ServiceDefinition {
    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //这个是hibernate的注解/生成32位UUID
    @GeneratedValue(generator="idGenerator")
    private String id;

    private String name;
    private String registeredId;
    private String registeredName;
    @Lob
    private String properties;

    public ServiceDefinition(){

    }

    public ServiceDefinition(String registeredName, String name, String properties) {
        this.registeredName= registeredName;
        this.name = name;
        this.properties = properties;
    }


    @Override
    public String toString() {
        return new StringBuilder().append("name").append(this.name).append("properties").append(this.properties).toString();
    }
}