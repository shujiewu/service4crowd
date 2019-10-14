package cn.edu.buaa.act.data.service;


import cn.edu.buaa.act.data.entity.CrowdTaskEntity;

import java.util.List;

/**
 * @author wsj
 */
public interface ICrowdTaskService {
    List<CrowdTaskEntity> queryAllByUser(String userId);

    List<CrowdTaskEntity> queryAllByProcessAndActivtity(String prcesssId, String actId);


    List<CrowdTaskEntity> queryAllByProcessId(String prcesssId);

    CrowdTaskEntity insertEntity(CrowdTaskEntity crowdJobEntity);

    CrowdTaskEntity queryByJobId(String jobId);

    CrowdTaskEntity queryById(String Id);

    CrowdTaskEntity queryByTaskId(String taskId);
}
