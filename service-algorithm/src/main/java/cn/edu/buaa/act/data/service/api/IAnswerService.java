package cn.edu.buaa.act.data.service.api;

import cn.buaa.act.crowd.common.entity.AnswerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAnswerService {

    AnswerEntity insertAnswerEntity(AnswerEntity answerEntity);
    Page<AnswerEntity> queryPageByUser(String userId, Pageable pageable);
    List<AnswerEntity> queryAllByUser(String userId);
    String queryAnswerEntityType(String answerEntityId);
}
