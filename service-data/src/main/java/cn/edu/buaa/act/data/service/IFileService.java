package cn.edu.buaa.act.data.service;


import cn.edu.buaa.act.data.entity.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author wsj
 */
public interface IFileService {
    List<FileEntity> queryAll();

    List<FileEntity> queryAllByUser(String userId);

    Page<FileEntity> findByUser(String userId, Pageable pageable);

    FileEntity findByUserAndId(String fileId);

    FileEntity insertEntity(FileEntity fileEntity);
}
