package cn.edu.buaa.act.data.service.impl;


import cn.edu.buaa.act.data.entity.MachineTaskEntity;
import cn.edu.buaa.act.data.repository.MachineTaskRepository;
import cn.edu.buaa.act.data.service.IMachineTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MachineTaskServiceImpl implements IMachineTaskService {
    @Autowired
    private MachineTaskRepository machineJobRepository;
    @Override
    public List<MachineTaskEntity> queryAllByUser(String userId) {
        return machineJobRepository.findMachineTaskEntitiesByUserId(userId);
    }
    @Override
    public List<MachineTaskEntity> queryAllByProcessAndActivtity(String prcesssId, String actId) {
        return machineJobRepository.findMachineTaskEntitiesByProcessInstanceIdAndActivityId(prcesssId,actId);
    }

    @Override
    public MachineTaskEntity queryByTaskId(String taskId) {
        return machineJobRepository.findMachineTaskEntityByTaskId(taskId);
    }

    @Override
    public List<MachineTaskEntity> queryAllByProcessId(String prcesssId) {
        return machineJobRepository.findMachineTaskEntitiesByProcessInstanceId(prcesssId);
    }

    @Override
    public MachineTaskEntity insertEntity(MachineTaskEntity machineJobEntity){
        return machineJobRepository.save(machineJobEntity);
    }
}
