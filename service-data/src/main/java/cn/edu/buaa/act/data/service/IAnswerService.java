package cn.edu.buaa.act.data.service;


import cn.edu.buaa.act.data.entity.AnswerEntity;
import cn.edu.buaa.act.data.model.AnswerStatRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author wsj
 */
public interface IAnswerService {
    AnswerEntity insertAnswerEntity(AnswerEntity answerEntity);
    Page<AnswerEntity> queryPageByUser(String userId, Pageable pageable);
    List<AnswerEntity> queryAllByUser(String userId);
    String queryAnswerEntityType(String answerEntityId);

    void deleteAnswerEntity(String id);

    AnswerEntity queryById(String id);

    AnswerEntity queryByName(String name);

    AnswerStatRepresentation insertDataStat(String dataId);
}
