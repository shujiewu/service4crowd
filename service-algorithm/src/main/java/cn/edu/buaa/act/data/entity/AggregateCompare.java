package cn.edu.buaa.act.data.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AggregateCompare
 *
 * @author wsj
 * @date 2018/6/22
 */
@Getter
@Setter
public class AggregateCompare {
    List<String> algorithmName = new ArrayList<>();
    String dataId;
    String dataType;
    String userId;
    Map<String,JSONObject> resultPredict = new HashMap<>();
    List<Object> aggregateStat = new ArrayList<>();
}
