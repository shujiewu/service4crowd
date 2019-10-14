package cn.edu.buaa.act.data.service.impl;


import cn.edu.buaa.act.data.entity.CrowdTaskEntity;
import cn.edu.buaa.act.data.repository.CrowdTaskRepository;
import cn.edu.buaa.act.data.service.ICrowdTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrowdTaskServiceImpl implements ICrowdTaskService {

    @Autowired
    private CrowdTaskRepository crowdJobRepository;
    @Override
    public List<CrowdTaskEntity> queryAllByUser(String userId) {
        return crowdJobRepository.findCrowdTaskEntitiesByUserId(userId);
    }
    @Override
    public List<CrowdTaskEntity> queryAllByProcessAndActivtity(String prcesssId, String actId) {
        return crowdJobRepository.findCrowdTaskEntitiesByProcessInstanceIdAndActivityId(prcesssId,actId);
    }

    @Override
    public List<CrowdTaskEntity> queryAllByProcessId(String prcesssId) {
        return crowdJobRepository.findCrowdTaskEntitiesByProcessInstanceId(prcesssId);
    }

    @Override
    public CrowdTaskEntity insertEntity(CrowdTaskEntity crowdJobEntity){
        return crowdJobRepository.save(crowdJobEntity);
    }

    @Override
    public CrowdTaskEntity queryByJobId(String jobId){
        return crowdJobRepository.findCrowdTaskEntityByJobId(jobId);
    }

    @Override
    public CrowdTaskEntity queryById(String Id){
        return crowdJobRepository.findById(Id).get();
    }

    @Override
    public CrowdTaskEntity queryByTaskId(String taskId){
        return crowdJobRepository.findCrowdTaskEntityByTaskId(taskId);
    }
}
