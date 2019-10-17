package cn.edu.buaa.act.fastwash.service.api;

import cn.edu.buaa.act.fastwash.entity.CrowdAnnotationTask;

public interface IAnnotationService {
    CrowdAnnotationTask findGroundTruthList(String projectName,String dataSetName,String imageId);
    void submitCrowdAnnotation(String projectName, CrowdAnnotationTask crowdAnnotationTask);
    CrowdAnnotationTask findLastAnnotationList(String projectName,String dataSetName,String imageId);
}
