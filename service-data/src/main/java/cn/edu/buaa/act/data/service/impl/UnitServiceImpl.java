package cn.edu.buaa.act.data.service.impl;


import cn.edu.buaa.act.data.entity.MetaEntity;
import cn.edu.buaa.act.data.entity.UnitEntity;
import cn.edu.buaa.act.data.repository.MetaRepository;
import cn.edu.buaa.act.data.repository.UnitRepository;
import cn.edu.buaa.act.data.service.IUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitServiceImpl implements IUnitService {
    @Autowired
    private MetaRepository metaRepository;
    @Autowired
    private UnitRepository unitRepository;

    @Override
    public List<UnitEntity> insertUnits(List<UnitEntity> unitEntityList) {
        return unitRepository.saveAll(unitEntityList);
    }

    @Override
    public List<UnitEntity> findUnitByIdList(List<String> unitIdList) {
        return unitRepository.findUnitEntitiesByIdIn(unitIdList);
    }

    @Override
    public Boolean deleteData(String metaId, String dataId) {
        MetaEntity metaEntity = metaRepository.findById(metaId).get();
        if (metaEntity.getDataId().remove(dataId)) {
            metaRepository.save(metaEntity);
            // System.out.println(metaEntity.getDataId());
        }
        unitRepository.deleteById(dataId);
        // System.out.println(metaId+" "+dataId);
        return true;
    }
}

