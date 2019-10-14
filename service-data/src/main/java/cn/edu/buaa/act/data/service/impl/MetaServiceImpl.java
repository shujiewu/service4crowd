package cn.edu.buaa.act.data.service.impl;


import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.data.entity.MetaEntity;
import cn.edu.buaa.act.data.entity.UnitEntity;
import cn.edu.buaa.act.data.repository.FileRepository;
import cn.edu.buaa.act.data.repository.MetaRepository;
import cn.edu.buaa.act.data.repository.UnitRepository;
import cn.edu.buaa.act.data.service.IMetaService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static cn.edu.buaa.act.data.common.Constant.META_FILE;


@Service
public class MetaServiceImpl implements IMetaService {
    @Autowired
    private MetaRepository metaRepository;
    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private FileRepository fileRepository;
    @Override
    public List<MetaEntity> queryAll() {
        return metaRepository.findAll();
    }

    @Override
    public List<MetaEntity> queryAllByUser(String userId) {
//        List<MetaEntity> result= metaRepository.findMetaEntitiesByUserId(userId);
//        result.stream().forEach(metaEntity ->{
//            metaEntity.setType(Constant.META_TEXT);
//        });
//        metaRepository.save(result);
        return metaRepository.findMetaEntitiesByUserId(userId);
    }
    @Override
    public Page<MetaEntity> queryPageByUser(String userId, Pageable pageable) {
        Page<MetaEntity> result= metaRepository.findMetaEntitiesByUserId(userId,pageable);
        // System.out.println(result.getTotalElements());
        return result;
        // return metaRepository.findMetaEntitiesByUserId(userId);
    }

    @Override
    public Page<MetaEntity> findByUser(String userId, Pageable pageable) {
        return null;
    }

    @Override
    public MetaEntity insertEntity(MetaEntity metaEntity) {
        return metaRepository.save(metaEntity);
    }

    @Override
    public Boolean isMetaNameExist(String metaName) {
        if(metaRepository.findMetaEntitiesByMetaNameAndUserId(metaName, BaseContextHandler.getUserID()).size()!=0){
            // System.out.println("CUNZAI");
            return true;
        } else
            return false;
    }

    @Override
    public MetaEntity findByName(String metaName, String userId) {
        return metaRepository.findMetaEntityByMetaNameAndUserId(metaName, userId);
    }

    @Override
    public MetaEntity findById(String id) {
        return metaRepository.findById(id).get();
    }

    @Override
    public Boolean deleteMeta(String metaId) {
        MetaEntity metaEntity = metaRepository.findById(metaId).get();
        List<String> dataId = metaEntity.getDataId();
        if(metaEntity.getType().equals(META_FILE)){
            if(dataId!=null && dataId.size()!=0)
            {
                for(String id: dataId)
                {
                    UnitEntity unitEntity = unitRepository.findById(id).get();
                    JSONObject jsonObject= (JSONObject) unitEntity.getData();
                    for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                        if(entry.getKey().contains("FileId")){
                            System.out.println(entry.getKey() + ":" + entry.getValue());
                            fileRepository.deleteById((String) entry.getValue());
                        }
                    }
                    unitRepository.deleteById(id);
                }
            }
            //metaEntity.getFileId()
        }
        else{
            if(dataId!=null && dataId.size()!=0)
            {
                for(String id: dataId)
                {
                    unitRepository.deleteById(id);
                }
            }
        }
        metaRepository.deleteById(metaId);
        return true;
    }
}
