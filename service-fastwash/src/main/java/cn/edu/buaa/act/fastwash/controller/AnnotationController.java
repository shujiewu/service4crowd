package cn.edu.buaa.act.fastwash.controller;


import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.fastwash.entity.CrowdAnnotationTask;
import cn.edu.buaa.act.fastwash.service.api.IAnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * AnnotationController
 *
 * @author wsj
 * @date 2019/9/8
 */
@RestController
@RequestMapping("/annotation")
public class AnnotationController {

    @Autowired
    IAnnotationService annotationService;

    @RequestMapping(value = "/image/{action}", method = RequestMethod.GET, produces = "application/json")
    public ObjectRestResponse annotation(@PathVariable String action, @RequestParam String projectName, @RequestParam String dataSetName, @RequestParam String imageId) throws Exception {
        if("groundtruth".equals(action)){
            CrowdAnnotationTask crowdAnnotationTask = annotationService.findGroundTruthList(projectName,dataSetName,imageId);
            return new ObjectRestResponse<CrowdAnnotationTask>().data(crowdAnnotationTask).success(true);
        }
        if("improve".equals(action)){
            CrowdAnnotationTask crowdAnnotationTask = annotationService.findLastAnnotationList(projectName,dataSetName,imageId);
            return new ObjectRestResponse<CrowdAnnotationTask>().data(crowdAnnotationTask).success(true);
        }
        return new ObjectRestResponse<CrowdAnnotationTask>().success(false);
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST, produces = "application/json")
    public ObjectRestResponse crowdSubmit(@RequestBody CrowdAnnotationTask crowdAnnotationTask,@RequestParam String projectName){
        // System.out.println(crowdAnnotationTask.getItems().size());
        // System.out.println(crowdAnnotationTask.getDetImg().getDataSetName()+ " " +crowdAnnotationTask.getDetImg().getFile_name());
        annotationService.submitCrowdAnnotation(projectName,crowdAnnotationTask);
        return new ObjectRestResponse<>().success(true);
    }
//    @RequestMapping(value = "/model/inference", method = RequestMethod.POST, produces = "application/json")
//    public ResponseEntity<Object> getTruthInferenceAlgorithm(@RequestBody List<DataItemEntity> dataItemEntityList) throws Exception {
//        return new ResponseEntity<Object>(truthInferenceService.queryTruthInferenceEntityById(algorithmId), HttpStatus.OK);
//    }
}
