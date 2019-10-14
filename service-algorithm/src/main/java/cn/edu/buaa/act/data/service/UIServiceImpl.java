package cn.edu.buaa.act.data.service;


import cn.buaa.act.datacore.entity.UIEntity;
import cn.buaa.act.datacore.repository.UIRepository;
import cn.buaa.act.datacore.service.api.IUIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UIServiceImpl implements IUIService {

    @Autowired
    private UIRepository uiRepository;


    @Override
    public UIEntity insertUi(UIEntity uiEntity) {
        return uiRepository.insert(uiEntity);
    }

    @Override
    public List<UIEntity> findUIByIdList(List<String> uiIdList) {
        return uiRepository.findUItEntitiesByIdIn(uiIdList);
    }

    @Override
    public List<UIEntity> queryAllByUser(String userId) {
        return uiRepository.findUIEntityByUserId(userId);
    }

    @Override
    public UIEntity queryById(String Id) {
        return uiRepository.findUIEntityById(Id);
    }
    @Override
    public void deleteUIById(String Id){
        uiRepository.delete(Id);
    }

    @Override
    public void updateUI(UIEntity uiEntity){
        uiRepository.save(uiEntity);
    }
}

