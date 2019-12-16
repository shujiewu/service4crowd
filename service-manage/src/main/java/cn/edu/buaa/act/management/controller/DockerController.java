package cn.edu.buaa.act.management.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DockerController {

//    @RequestMapping(value = "/add_container")
//    @ResponseBody
//    public String addContainer(@RequestParam("war") MultipartFile file, @RequestParam("tomVersion") String tomVersion,Integer cpu,Long mem) {
//        try {
//
//            if (file.isEmpty()) {
//                return jsonResultFail("请选择war文件");
//            }
//            File localApp = new File(appLocalDir + file.getOriginalFilename());
//            file.transferTo(localApp);
//            String containerName = dockerOper.createAndStartrContainerAnddeployApp(tomVersion,localApp,cpu,mem);
//            return jsonResultSuccess("创建成功,容器名："+containerName);
//
//        } catch (Exception e) {
//            return jsonResultFail("创建失败,"+e.getMessage());
//        }
//    }
}
