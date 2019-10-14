package cn.edu.buaa.act.data.service;


import cn.edu.buaa.act.data.entity.UnitEntity;

import java.util.List;

public interface IUnitService {
    List<UnitEntity> insertUnits(List<UnitEntity> unitEntityList);

    List<UnitEntity> findUnitByIdList(List<String> unitIdList);

    Boolean deleteData(String metaId, String dataId);
}
