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
//@RequestParam(value = "random", required = false) String classValue
    @RequestMapping(value = "/image/{action}", method = RequestMethod.GET, produces = "application/json")
    public ObjectRestResponse annotation(@PathVariable String action, @RequestParam String projectName, @RequestParam String dataSetName,
                                         @RequestParam(defaultValue = "random", required = false) String imageId,
                                         @RequestParam(defaultValue = "random", required = false) String classValue
                                         ) throws Exception {
        if("groundtruth".equals(action)){
            CrowdAnnotationTask crowdAnnotationTask = annotationService.findGroundTruthList(projectName,dataSetName,imageId);
            return new ObjectRestResponse<CrowdAnnotationTask>().data(crowdAnnotationTask).success(true);
        }
        //如果没有
        if("improve".equals(action)){
            CrowdAnnotationTask crowdAnnotationTask = annotationService.findLastAnnotationList(projectName,dataSetName,imageId);
            return new ObjectRestResponse<CrowdAnnotationTask>().data(crowdAnnotationTask).success(true);
        }

        if("annotate".equals(action)){
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

    @RequestMapping(value = "/task/next", method = RequestMethod.GET, produces = "application/json")
    public ObjectRestResponse getNextTask(@RequestParam String projectName, @RequestParam String dataSetName,@RequestParam(defaultValue = "0", required = false) String classId){
        CrowdAnnotationTask crowdAnnotationTask = annotationService.findLastAnnotationList(projectName,classId);
        if(crowdAnnotationTask.getDetImg()!=null){
            return new ObjectRestResponse<CrowdAnnotationTask>().data(crowdAnnotationTask).success(true);
        }else{
            return new ObjectRestResponse<CrowdAnnotationTask>().data(crowdAnnotationTask).success(false);
        }
    }
    @RequestMapping(value = "/task/submit", method = RequestMethod.POST, produces = "application/json")
    public ObjectRestResponse submitTask(@RequestBody CrowdAnnotationTask crowdAnnotationTask,@RequestParam String projectName){
        annotationService.submitCrowdTask(projectName,crowdAnnotationTask);
        return new ObjectRestResponse<>().success(true);
    }

    @RequestMapping(value = "/task", method = RequestMethod.GET, produces = "application/json")
    public ObjectRestResponse getTask(@RequestParam String projectName, @RequestParam String dataSetName,@RequestParam String imageId, @RequestParam String classId){
        CrowdAnnotationTask crowdAnnotationTask = annotationService.findLastAnnotationList(projectName,dataSetName, imageId, classId);
        if(crowdAnnotationTask.getDetImg()!=null){
            return new ObjectRestResponse<CrowdAnnotationTask>().data(crowdAnnotationTask).success(true);
        }else{
            return new ObjectRestResponse<CrowdAnnotationTask>().data(crowdAnnotationTask).success(false);
        }
    }

    //new API
    @RequestMapping(value = "/task/submits", method = RequestMethod.POST, produces = "application/json")
    public ObjectRestResponse submitTasks(@RequestBody CrowdAnnotationTask crowdAnnotationTask,@RequestParam String projectName){
        annotationService.submitCrowdTaskComplete(projectName,crowdAnnotationTask);
        return new ObjectRestResponse<>().success(true);
    }
    @RequestMapping(value = "/task/nexts", method = RequestMethod.GET, produces = "application/json")
    public ObjectRestResponse getNextTasks(@RequestParam String projectName, @RequestParam String dataSetName,@RequestParam(defaultValue = "0", required = false) String classId){
        CrowdAnnotationTask crowdAnnotationTask = annotationService.findLastAnnotationListNew(projectName,classId);
        if(crowdAnnotationTask.getDetImg()!=null){
            return new ObjectRestResponse<CrowdAnnotationTask>().data(crowdAnnotationTask).success(true);
        }else{
            return new ObjectRestResponse<CrowdAnnotationTask>().data(crowdAnnotationTask).success(false);
        }
    }
}
