package cn.edu.buaa.act.data.service.api;

import cn.buaa.act.datacore.entity.CrowdJobEntity;

import java.util.List;

public interface ICrowdJobService {
    List<CrowdJobEntity> queryAllByUser(String userId);

    List<CrowdJobEntity> queryAllByProcessAndActivtity(String prcesssId, String actId);


    List<CrowdJobEntity> queryAllByProcessId(String prcesssId);

    CrowdJobEntity insertEntity(CrowdJobEntity crowdJobEntity);

    CrowdJobEntity queryByJobId(String jobId);

    CrowdJobEntity queryById(String Id);
}
