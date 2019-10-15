package cn.edu.buaa.act.fastwash.common;

import cn.edu.buaa.act.fastwash.entity.DataSetEntity;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ReadJsonFile {
    public static DataSetEntity ReadFile(String path){
        File file = new File(path);
        BufferedReader reader = null;
        DataSetEntity result = null;
        try {
            StringBuilder res = new StringBuilder();
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                res.append(tempString);
            }
            reader.close();
            result = JSONObject.parseObject(res.toString(), DataSetEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return result;
    }
}