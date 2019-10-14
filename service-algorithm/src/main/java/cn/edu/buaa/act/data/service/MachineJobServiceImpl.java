package cn.edu.buaa.act.data.service;

import cn.buaa.act.datacore.entity.MachineJobEntity;
import cn.buaa.act.datacore.repository.MachineJobRepository;
import cn.buaa.act.datacore.service.api.IMachineJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MachineJobServiceImpl implements IMachineJobService {
    @Autowired
    private MachineJobRepository machineJobRepository;
    @Override
    public List<MachineJobEntity> queryAllByUser(String userId) {
        return machineJobRepository.findMachineJobEntitiesByUserId(userId);
    }
    @Override
    public List<MachineJobEntity> queryAllByProcessAndActivtity(String prcesssId, String actId) {
        return machineJobRepository.findMachineJobEntitiesByProcessInstanceIdAndActivityId(prcesssId,actId);
    }

    @Override
    public List<MachineJobEntity> queryAllByProcessId(String prcesssId) {
        return machineJobRepository.findMachineJobEntitiesByProcessInstanceId(prcesssId);
    }

    @Override
    public MachineJobEntity insertEntity(MachineJobEntity machineJobEntity){
        return machineJobRepository.save(machineJobEntity);
    }
}
