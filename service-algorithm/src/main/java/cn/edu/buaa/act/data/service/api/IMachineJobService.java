package cn.edu.buaa.act.data.service.api;

import cn.buaa.act.datacore.entity.MachineJobEntity;

import java.util.List;

public interface IMachineJobService {
    List<MachineJobEntity> queryAllByUser(String userId);

    List<MachineJobEntity> queryAllByProcessAndActivtity(String prcesssId, String actId);


    List<MachineJobEntity> queryAllByProcessId(String prcesssId);

    MachineJobEntity insertEntity(MachineJobEntity machineJobEntity);
}
