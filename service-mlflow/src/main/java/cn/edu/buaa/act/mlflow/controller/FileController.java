package cn.edu.buaa.act.mlflow.controller;

import cn.edu.buaa.act.common.msg.BaseResponse;
import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    private final static String filePath = "D://filedata/";
    @RequestMapping(value = "/{experimentId}/upload",method = RequestMethod.POST)
    public ObjectRestResponse<Map> upload(@PathVariable String experimentId,@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId){
        System.out.println(file.getSize());
        // 文件名
        String fileName = file.getOriginalFilename();
        System.out.println("文件名： " + fileName);

        // 文件后缀
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        System.out.println("文件后缀名： " + suffixName);

        // 重新生成唯一文件名，用于存储数据库
        String newFileName = UUID.randomUUID().toString()+suffixName;
        System.out.println("新的文件名： " + newFileName);

        //创建文件
        File dir = new File(filePath+userId+"/"+experimentId+"/");
        if(!dir.exists()){
            dir.mkdirs();
        }
        File dest = new File(dir.getAbsolutePath()+"/"+newFileName);

        Map map = new HashMap();
        map.put("filePath", dest.getAbsolutePath());
        map.put("name", newFileName);
        try {
            file.transferTo(dest);
            return new ObjectRestResponse<>().data(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ObjectRestResponse<>();
    }
}