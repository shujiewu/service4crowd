package cn.edu.buaa.act.data.service;


import cn.edu.buaa.act.data.entity.MetaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author wsj
 */
public interface IMetaService {
    List<MetaEntity> queryAll();

    List<MetaEntity> queryAllByUser(String userId);

    Page<MetaEntity> queryPageByUser(String userId, Pageable pageable);

    Page<MetaEntity> findByUser(String userId, Pageable pageable);

    MetaEntity insertEntity(MetaEntity metaEntity);

    Boolean isMetaNameExist(String metaName);

    MetaEntity findByName(String metaName, String userId);

    MetaEntity findById(String id);

    Boolean deleteMeta(String metaId);
}
