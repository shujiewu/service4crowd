package cn.edu.buaa.act.workflow.service;

import cn.edu.buaa.act.workflow.domain.AbstractModel;
import cn.edu.buaa.act.workflow.domain.Model;
import cn.edu.buaa.act.workflow.domain.ModelHistory;
import cn.edu.buaa.act.workflow.domain.Template;
import cn.edu.buaa.act.workflow.model.ModelRepresentation;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.identity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface ModelService {

    Model getModel(String modelId);

    Model getModelByProcessInstanceID(String ProcessInstanceID);

    List<AbstractModel> getModelsByModelType(Integer modelType);

    Boolean validateModelKey(Model model, Integer modelType, String key);

    byte[] getBpmnXML(BpmnModel bpmnModel);

    ModelHistory getModelHistory(String modelId, String modelHistoryId);

    Model changeModel(Model model);

    Model persistModel(Model model);

    BpmnModel getBpmnModel(AbstractModel model);



    BpmnModel getBpmnModel(AbstractModel model, Map<String, Model> formMap, Map<String, Model> decisionTableMap);

    Template createTemplate(Template template);

    Model createModel(ModelRepresentation model, String editorJson, User createdBy);

    Model createModel(Model newModel, User createdBy);

    Model saveModel(Model modelObject);

    Model saveModel(Model modelObject, String editorJson, byte[] imageBytes, boolean newVersion, String newVersionComment, User updatedBy);

    Model saveModel(String modelId, String name, String key, String description, String editorJson, boolean newVersion, String newVersionComment, User updatedBy);

    Model createModel(String name, String key, String description, String editorJson, User createdBy);

    Model createModelTemplate(String name, String key, String description, String editorJson, User createdBy);

    void deleteModel(String modelId, boolean cascadeHistory, boolean deleteRuntimeApp);
}
