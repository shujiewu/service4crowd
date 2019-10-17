package cn.edu.buaa.act.model.detection.controller;



import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.model.detection.entity.DataItemEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AnnotationController
 *
 * @author wsj
 * @date 2019/9/8
 */
@RestController
@RequestMapping("/annotation")
public class AnnotationController {
    @RequestMapping(value = "/model/inference", method = RequestMethod.GET, produces = "application/json")
    public ObjectRestResponse inference(@RequestParam String projectName, @RequestParam String dataSetName, @RequestParam String imageId) throws Exception {

    }
}
