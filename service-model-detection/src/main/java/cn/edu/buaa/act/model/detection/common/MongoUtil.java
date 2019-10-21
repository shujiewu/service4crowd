package cn.edu.buaa.act.model.detection.common;

import com.alibaba.fastjson.JSON;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;

public class MongoUtil {
    public static <T> Document toDocument(T object){
        String json = JSON.toJSONString(object);
        return Document.parse(json);
    }
    public <T> T toBean(Document document,Class<T> clzss){
        String realJson = document.toJson(JsonWriterSettings.builder().build());
        return JSON.parseObject(realJson,clzss);
    }

}
