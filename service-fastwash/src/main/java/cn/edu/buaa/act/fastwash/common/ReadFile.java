package cn.edu.buaa.act.fastwash.common;

import cn.edu.buaa.act.fastwash.entity.DataSetEntity;
import com.alibaba.fastjson.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ReadFile {
    public static DataSetEntity readDataSet(String path){
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

    public  static byte[] getImageBinary(String path, String imgType) {
        File f = new File(path);
        BufferedImage bi;
        try {
            bi = ImageIO.read(f);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, imgType, baos);  //经测试转换的图片是格式这里就什么格式，否则会失真
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}