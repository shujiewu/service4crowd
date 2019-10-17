package cn.edu.buaa.act.fastwash.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tag {
    private Classification classification;
    private JSONObject property;
    private Box box;
}
