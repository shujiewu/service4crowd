package cn.edu.buaa.act.data.service;

import cn.edu.buaa.act.data.entity.UIEntity;

import java.util.List;

/**
 * @author wsj
 */
public interface IUIService {
    UIEntity insertUi(UIEntity uiEntity);

    List<UIEntity> findUIByIdList(List<String> uiIdList);

    List<UIEntity> queryAllByUser(String userId);

    UIEntity queryById(String id);

    void deleteUIById(String id);

    void updateUI(UIEntity uiEntity);
}
