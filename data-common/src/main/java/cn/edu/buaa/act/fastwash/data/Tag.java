package cn.edu.buaa.act.fastwash.data;

import cn.edu.buaa.act.fastwash.data.Box;
import cn.edu.buaa.act.fastwash.data.Classification;
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
