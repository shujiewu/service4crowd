package cn.edu.buaa.act.data.service;

import cn.buaa.act.crowd.common.context.BaseContextHandler;
import cn.buaa.act.datacore.entity.FileEntity;
import cn.buaa.act.datacore.repository.FileRepository;
import cn.buaa.act.datacore.service.api.IFileService;
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
        FileEntity fileEntity = fileRepository.findFileEntityByIdAndUserId(fileId,BaseContextHandler.getUserID());
        return fileEntity;
    }

    @Override
    public FileEntity insertEntity(FileEntity metaEntity) {
        return fileRepository.save(metaEntity);
    }
}
