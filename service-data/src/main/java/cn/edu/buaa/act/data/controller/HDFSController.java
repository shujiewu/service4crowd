package cn.edu.buaa.act.data.controller;

import cn.edu.buaa.act.common.msg.BaseResponse;
import cn.edu.buaa.act.data.service.impl.HdfsService;
import org.apache.hadoop.conf.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * HDFSController
 *
 * @author wsj
 * @date 2018/10/7
 */
@RestController
@RequestMapping("/HDFS")
public class HDFSController {

    @Autowired
    private Configuration conf;

    @Value("${hadoop.hdfs.user}")
    private String user;
    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/upload")
    public BaseResponse upLoadFile(@RequestParam(name = "file", required = true) MultipartFile file, @RequestParam(name = "destPath") String destPath)
            throws Exception {
        HdfsService api = new HdfsService(conf, user);
        InputStream is = file.getInputStream();
        String name = file.getOriginalFilename();
        api.upLoadFile(is, destPath + "/" + name);
        api.close();
        return new BaseResponse();
    }
}
