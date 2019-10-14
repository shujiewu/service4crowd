package cn.edu.buaa.act.data.service;


import cn.buaa.act.datacore.entity.MetaEntity;
import cn.buaa.act.datacore.entity.UnitEntity;
import cn.buaa.act.datacore.repository.MetaRepository;
import cn.buaa.act.datacore.repository.UnitRepository;
import cn.buaa.act.datacore.service.api.IUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitServiceImpl implements IUnitService{
    @Autowired
    private MetaRepository metaRepository;
    @Autowired
    private UnitRepository unitRepository;
    @Override
    public List<UnitEntity> insertUnits(List<UnitEntity> unitEntityList) {
        return unitRepository.save(unitEntityList);
    }

    @Override
    public List<UnitEntity> findUnitByIdList(List<String> unitIdList) {
        return unitRepository.findUnitEntitiesByIdIn(unitIdList);
    }

    @Override
    public Boolean deleteData(String metaId, String dataId) {

        MetaEntity metaEntity = metaRepository.findOne(metaId);
        if(metaEntity.getDataId().remove(dataId)){
            metaRepository.save(metaEntity);
            // System.out.println(metaEntity.getDataId());
        }
        unitRepository.delete(dataId);
        // System.out.println(metaId+" "+dataId);
        return true;
    }
}

