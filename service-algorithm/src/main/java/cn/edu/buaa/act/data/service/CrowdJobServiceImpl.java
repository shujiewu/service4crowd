package cn.edu.buaa.act.data.service;

import cn.buaa.act.datacore.entity.CrowdJobEntity;
import cn.buaa.act.datacore.repository.CrowdJobRepository;
import cn.buaa.act.datacore.service.api.ICrowdJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrowdJobServiceImpl implements ICrowdJobService {

    @Autowired
    private CrowdJobRepository crowdJobRepository;
    @Override
    public List<CrowdJobEntity> queryAllByUser(String userId) {
        return crowdJobRepository.findCrowdJobEntitiesByUserId(userId);
    }
    @Override
    public List<CrowdJobEntity> queryAllByProcessAndActivtity(String prcesssId, String actId) {
        return crowdJobRepository.findCrowdJobEntitiesByProcessInstanceIdAndActivityId(prcesssId,actId);
    }

    @Override
    public List<CrowdJobEntity> queryAllByProcessId(String prcesssId) {
        return crowdJobRepository.findCrowdJobEntitiesByProcessInstanceId(prcesssId);
    }

    @Override
    public CrowdJobEntity insertEntity(CrowdJobEntity crowdJobEntity){
        return crowdJobRepository.save(crowdJobEntity);
    }

    @Override
    public CrowdJobEntity queryByJobId(String jobId){
        return crowdJobRepository.findCrowdJobEntityByJobId(jobId);
    }

    @Override
    public CrowdJobEntity queryById(String Id){
        return crowdJobRepository.findOne(Id);
    }
}
