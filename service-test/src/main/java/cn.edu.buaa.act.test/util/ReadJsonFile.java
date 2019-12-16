package cn.edu.buaa.act.test.util;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadJsonFile {
    //读文件，返回字符串
    public static JSONObject ReadFile(String path){
        File file = new File(path);
        BufferedReader reader = null;
        JSONObject result =null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            StringBuilder res = new StringBuilder();
            while ((tempString = reader.readLine()) != null) {
                //显示行号
                res.append(tempString);
            }
            result = JSONObject.parseObject(res.toString());
            reader.close();
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