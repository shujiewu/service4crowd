package cn.edu.buaa.act.data.service.api;

import cn.buaa.act.datacore.entity.UIEntity;

import java.util.List;

public interface IUIService {
    UIEntity insertUi(UIEntity uiEntity);

    List<UIEntity> findUIByIdList(List<String> uiIdList);

    List<UIEntity> queryAllByUser(String userId);

    UIEntity queryById(String Id);


    void deleteUIById(String Id);

    void updateUI(UIEntity uiEntity);
}
