package cn.edu.buaa.act.workflow.service.impl;

import cn.edu.buaa.act.workflow.controller.ModelController;
import cn.edu.buaa.act.workflow.domain.AbstractModel;
import cn.edu.buaa.act.workflow.domain.Model;
import cn.edu.buaa.act.workflow.domain.ModelHistory;
import cn.edu.buaa.act.workflow.domain.Template;
import cn.edu.buaa.act.workflow.exception.InternalServerErrorException;
import cn.edu.buaa.act.workflow.exception.NotFoundException;
import cn.edu.buaa.act.workflow.model.ModelRepresentation;
import cn.edu.buaa.act.workflow.repository.ModelHistoryRepository;
import cn.edu.buaa.act.workflow.repository.ModelRelationRepository;
import cn.edu.buaa.act.workflow.repository.ModelRepository;
import cn.edu.buaa.act.workflow.repository.TemplateRepository;
import cn.edu.buaa.act.workflow.service.ModelService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.editor.language.json.converter.util.JsonConverterUtil;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.identity.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


import static cn.edu.buaa.act.workflow.common.Constant.MODEL_STATUS_UNPUBLISH;
import static cn.edu.buaa.act.workflow.common.Constant.PROCESS_NOT_FOUND_MESSAGE_KEY;

/**
 * ModelServiceImpl
 *
 * @author wsj
 * @date 2018/10/18
 */
@Service
public class ModelServiceImpl implements ModelService {

    private static final Logger log = LoggerFactory.getLogger(ModelServiceImpl.class);

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected ModelImageService modelImageService;

    @Autowired
    protected ModelRepository modelRepository;

    @Autowired
    protected TemplateRepository templateRepository;
    @Autowired
    protected ModelHistoryRepository modelHistoryRepository;

    @Autowired
    protected ModelRelationRepository modelRelationRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected HistoryService historyService;
    @Override
    public Model getModel(String modelId) {
        Model model = modelRepository.findById(modelId).get();
        if (model == null) {
            NotFoundException modelNotFound = new NotFoundException("No model found with the given id: " + modelId);
            modelNotFound.setMessageKey(PROCESS_NOT_FOUND_MESSAGE_KEY);
            throw modelNotFound;
        }
        return model;
    }

    @Override
    public Model getModelByProcessInstanceID(String ProcessInstanceID) {
        Model model = modelRepository.findModelByProcessInstanceID(ProcessInstanceID);
        if (model == null) {
            NotFoundException modelNotFound = new NotFoundException("No model found with the given ProcessInstanceID: " + ProcessInstanceID);
            modelNotFound.setMessageKey(PROCESS_NOT_FOUND_MESSAGE_KEY);
            throw modelNotFound;
        }
        return model;
    }

    @Override
    public List<AbstractModel> getModelsByModelType(Integer modelType) {
        return new ArrayList<AbstractModel>(modelRepository.findModelsByModelType(modelType));
    }

    @Override
    public Boolean validateModelKey(Model model, Integer modelType, String key) {
        Boolean modelKeyResponse = false;
        List<Model> models = modelRepository.findModelsByKeyAndType(key, modelType);
        for (Model modelInfo : models) {
            if (model == null || modelInfo.getId().equals(model.getId()) == false) {
                modelKeyResponse=true;
                break;
            }
        }
        return modelKeyResponse;
    }

    protected BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
    @Override
    public byte[] getBpmnXML(BpmnModel bpmnModel) {
        for (Process process : bpmnModel.getProcesses()) {
            if (StringUtils.isNotEmpty(process.getId())) {
                char firstCharacter = process.getId().charAt(0);
                // no digit is allowed as first character
                if (Character.isDigit(firstCharacter)) {
                    process.setId("a" + process.getId());
                }
            }
        }
        byte[] xmlBytes = bpmnXMLConverter.convertToXML(bpmnModel);
        return xmlBytes;
    }

    @Override
    public ModelHistory getModelHistory(String modelId, String modelHistoryId) {
        Model model = getModel(modelId);
        ModelHistory modelHistory = modelHistoryRepository.findById(modelHistoryId).get();
        if (modelHistory == null || modelHistory.getRemovalDate() != null || !modelHistory.getModelId().equals(model.getId())) {
            throw new NotFoundException("Process model history not found: " + modelHistoryId);
        }
        return modelHistory;
    }

    @Override
    public Model changeModel(Model model) {
        model = modelRepository.save((Model) model);
        return model;
    }

    @Override
    public Model persistModel(Model model) {
        model = modelRepository.save((Model) model);

        if (StringUtils.isNotEmpty(model.getModelEditorJson())) {
            ObjectNode jsonNode = null;
            try {
                jsonNode = (ObjectNode) objectMapper.readTree(model.getModelEditorJson());
            } catch (Exception e) {
                log.error("Could not deserialize json model", e);
                throw new InternalServerErrorException("Could not deserialize json model");
            }

            if ((model.getModelType() == null || model.getModelType().intValue() == Model.MODEL_TYPE_BPMN || model.getModelType().intValue() == Model.MODEL_TYPE_TEMPLATE )) {
                modelImageService.generateThumbnailImage(model, jsonNode);
            }
        }

        return model;
    }


    @Override
    public BpmnModel getBpmnModel(AbstractModel model) {
        return null;
    }


    @Override
    public BpmnModel getBpmnModel(AbstractModel model, Map<String, Model> formMap, Map<String, Model> decisionTableMap) {
        return null;
    }


    @Override
    public Model saveModel(Model modelObject) {
        return persistModel(modelObject);
    }

    @Override
    public Model saveModel(Model modelObject, String editorJson, byte[] imageBytes, boolean newVersion, String newVersionComment, User updatedBy) {
        return internalSave(modelObject.getName(), modelObject.getKey(), modelObject.getDescription(), editorJson, newVersion,
                newVersionComment, imageBytes, updatedBy, modelObject);
    }

    @Override
    public Model saveModel(String modelId, String name, String key, String description, String editorJson,
                           boolean newVersion, String newVersionComment, User updatedBy) {
        Model modelObject = modelRepository.findById(modelId).get();
        return internalSave(name, key, description, editorJson, newVersion, newVersionComment, null, updatedBy, modelObject);
    }

    protected Model internalSave(String name, String key, String description, String editorJson, boolean newVersion,
                                 String newVersionComment, byte[] imageBytes, User updatedBy, Model modelObject) {

        if (newVersion == false) {
            modelObject.setLastUpdated(new Date());
            modelObject.setLastUpdatedBy(updatedBy.getId());
            modelObject.setName(name);
            modelObject.setKey(key);
            modelObject.setDescription(description);
            modelObject.setModelEditorJson(editorJson);

            if (imageBytes != null) {
                modelObject.setThumbnail(imageBytes);
            }
        } else {
            ModelHistory historyModel = createNewModelhistory(modelObject);
            persistModelHistory(historyModel);

            modelObject.setVersion(modelObject.getVersion() + 1);
            modelObject.setLastUpdated(new Date());
            modelObject.setLastUpdatedBy(updatedBy.getId());
            modelObject.setName(name);
            modelObject.setKey(key);
            modelObject.setDescription(description);
            modelObject.setModelEditorJson(editorJson);
            modelObject.setComment(newVersionComment);

            if (imageBytes != null) {
                modelObject.setThumbnail(imageBytes);
            }
        }

        return persistModel(modelObject);
    }

    @Override
    public Model createModel(Model newModel, User createdBy) {
        newModel.setVersion(1);
        newModel.setCreated(Calendar.getInstance().getTime());
        newModel.setCreatedBy(createdBy.getId());
        newModel.setLastUpdated(Calendar.getInstance().getTime());
        newModel.setLastUpdatedBy(createdBy.getId());

        persistModel(newModel);
        return newModel;
    }

    @Override
    public Model createModel(ModelRepresentation model, String editorJson, User createdBy) {
        Model newModel = new Model();
        newModel.setVersion(1);
        newModel.setName(model.getName());
        newModel.setKey(model.getKey());
        newModel.setModelType(model.getModelType());
        newModel.setCreated(Calendar.getInstance().getTime());
        newModel.setCreatedBy(createdBy.getId());
        newModel.setDescription(model.getDescription());
        newModel.setModelEditorJson(editorJson);
        newModel.setLastUpdated(Calendar.getInstance().getTime());
        newModel.setLastUpdatedBy(createdBy.getId());
        newModel.setStatus(model.getStatus());
        persistModel(newModel);
        return newModel;
    }

    @Override
    public Model createModel(String name, String key, String description, String editorJson, User createdBy) {
        Model newModel = new Model();
        newModel.setVersion(1);
        newModel.setName(name);
        newModel.setKey(key);
        newModel.setModelType(AbstractModel.MODEL_TYPE_BPMN);
        newModel.setCreated(Calendar.getInstance().getTime());
        newModel.setCreatedBy(createdBy.getId());
        newModel.setDescription(description);
        newModel.setModelEditorJson(editorJson);
        newModel.setLastUpdated(Calendar.getInstance().getTime());
        newModel.setLastUpdatedBy(createdBy.getId());
        newModel.setStatus(MODEL_STATUS_UNPUBLISH);
        persistModel(newModel);
        return newModel;
    }

    @Override
    public Model createModelTemplate(String name, String key, String description, String editorJson, User createdBy) {
        Model newModel = new Model();
        newModel.setVersion(1);
        newModel.setName(name);
        newModel.setKey(key);
        newModel.setModelType(AbstractModel.MODEL_TYPE_TEMPLATE);
        newModel.setCreated(Calendar.getInstance().getTime());
        newModel.setCreatedBy(createdBy.getId());
        newModel.setDescription(description);
        newModel.setModelEditorJson(editorJson);
        newModel.setLastUpdated(Calendar.getInstance().getTime());
        newModel.setLastUpdatedBy(createdBy.getId());
        persistModel(newModel);
        return newModel;
    }

    @Override
    public Template createTemplate(Template template) {
        template = templateRepository.save(template);
        return template;
    }

    @Override
    public void deleteModel(String modelId, boolean cascadeHistory, boolean deleteRuntimeApp) {
        Model model = modelRepository.findById(modelId).get();
        if (model == null) {
            throw new IllegalArgumentException("No model found with id: " + modelId);
        }

        List<ModelHistory> history = modelHistoryRepository.findByModelIdAndRemovalDateIsNullOrderByVersionDesc(model.getId());

        ModelHistory historyModel = createNewModelhistory(model);
        historyModel.setRemovalDate(Calendar.getInstance().getTime());
        persistModelHistory(historyModel);

        if (cascadeHistory || history.size() == 0) {
            deleteModelAndChildren(model);
        } else {
            ModelHistory toRevive = history.remove(0);
            populateModelBasedOnHistory(model, toRevive);
            persistModel(model);
            modelHistoryRepository.delete(toRevive);
        }
    }
    protected void populateModelBasedOnHistory(Model model, ModelHistory basedOn) {
        model.setName(basedOn.getName());
        model.setKey(basedOn.getKey());
        model.setDescription(basedOn.getDescription());
        model.setCreated(basedOn.getCreated());
        model.setLastUpdated(basedOn.getLastUpdated());
        model.setCreatedBy(basedOn.getCreatedBy());
        model.setLastUpdatedBy(basedOn.getLastUpdatedBy());
        model.setModelEditorJson(basedOn.getModelEditorJson());
        model.setModelType(basedOn.getModelType());
        model.setVersion(basedOn.getVersion());
        model.setComment(basedOn.getComment());
    }
    protected ModelHistory persistModelHistory(ModelHistory modelHistory) {
        return modelHistoryRepository.save(modelHistory);
    }
    protected ModelHistory createNewModelhistory(Model model) {
        ModelHistory historyModel = new ModelHistory();
        historyModel.setName(model.getName());
        historyModel.setKey(model.getKey());
        historyModel.setDescription(model.getDescription());
        historyModel.setCreated(model.getCreated());
        historyModel.setLastUpdated(model.getLastUpdated());
        historyModel.setCreatedBy(model.getCreatedBy());
        historyModel.setLastUpdatedBy(model.getLastUpdatedBy());
        historyModel.setModelEditorJson(model.getModelEditorJson());
        historyModel.setModelType(model.getModelType());
        historyModel.setVersion(model.getVersion());
        historyModel.setModelId(model.getId());
        historyModel.setComment(model.getComment());
        return historyModel;
    }
    protected void deleteModelAndChildren(Model model) {
        List<Model> allModels = new ArrayList<Model>();
        internalDeleteModelAndChildren(model, allModels);

        for (Model modelToDelete : allModels) {
            modelRepository.delete(modelToDelete);
        }
    }
    protected void internalDeleteModelAndChildren(Model model, List<Model> allModels) {
        modelRelationRepository.deleteModelRelationsForParentModel(model.getId());
        allModels.add(model);
    }
}
