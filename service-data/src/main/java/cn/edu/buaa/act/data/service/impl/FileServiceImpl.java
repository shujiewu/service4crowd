package cn.edu.buaa.act.data.service.impl;


import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.data.entity.FileEntity;
import cn.edu.buaa.act.data.repository.FileRepository;
import cn.edu.buaa.act.data.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileServiceImpl implements IFileService {
    @Autowired
    private FileRepository fileRepository;

    @Override
    public List<FileEntity> queryAll() {
        return fileRepository.findAll();
    }

    @Override
    public List<FileEntity> queryAllByUser(String userId) {
        return fileRepository.findFileEntitiesByUserId(userId);
    }

    @Override
    public Page<FileEntity> findByUser(String userId, Pageable pageable) {
        return null;
    }

    @Override
    public FileEntity findByUserAndId(String fileId) {
        FileEntity fileEntity = fileRepository.findFileEntityByIdAndUserId(fileId, BaseContextHandler.getUserID());
        return fileEntity;
    }
    @Override
    public FileEntity insertEntity(FileEntity metaEntity) {
        return fileRepository.save(metaEntity);
    }
}
