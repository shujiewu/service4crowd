package cn.edu.buaa.act.data.service;


import cn.edu.buaa.act.data.entity.MachineTaskEntity;

import java.util.List;

/**
 * @author wsj
 */
public interface IMachineTaskService {
    List<MachineTaskEntity> queryAllByUser(String userId);

    List<MachineTaskEntity> queryAllByProcessAndActivtity(String prcesssId, String actId);

    MachineTaskEntity queryByTaskId(String taskId);

    List<MachineTaskEntity> queryAllByProcessId(String prcesssId);

    MachineTaskEntity insertEntity(MachineTaskEntity machineJobEntity);
}
