package cn.edu.buaa.act.workflow.controller;

import cn.edu.buaa.act.workflow.domain.AbstractModel;
import cn.edu.buaa.act.workflow.domain.Model;
import cn.edu.buaa.act.workflow.exception.BadRequestException;
import cn.edu.buaa.act.workflow.exception.ConflictingRequestException;
import cn.edu.buaa.act.workflow.exception.InternalServerErrorException;
import cn.edu.buaa.act.workflow.exception.NonJsonResourceNotFoundException;
import cn.edu.buaa.act.workflow.model.ModelRepresentation;
import cn.edu.buaa.act.workflow.repository.ModelRepository;
import cn.edu.buaa.act.workflow.service.ModelService;
import cn.edu.buaa.act.workflow.util.SecurityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.activiti.engine.identity.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.*;

import cn.edu.buaa.act.workflow.common.Constant;

import static cn.edu.buaa.act.workflow.common.Constant.MODEL_STATUS_UNPUBLISH;

/**
 * ModelController
 *
 * @author wsj
 * @date 2018/10/6
 */

@RequestMapping(value = "/workflow")
@RestController
public class ModelController {
    private static final Logger log = LoggerFactory.getLogger(ModelController.class);
    private static final String RESOLVE_ACTION_OVERWRITE = "overwrite";
    private static final String RESOLVE_ACTION_SAVE_AS = "saveAs";
    private static final String RESOLVE_ACTION_NEW_VERSION = "newVersion";

    @Autowired
    protected ModelService modelService;

    @Autowired
    protected ModelRepository modelRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * @param modelId
     * @return
     */
    @RequestMapping(value = "/models/{modelId}", method = RequestMethod.GET, produces = "application/json")
    public ModelRepresentation getModel(String modelId) {
        Model model = modelService.getModel(modelId);
        if (model == null) {
            throw new NonJsonResourceNotFoundException();
        }
        ModelRepresentation result = new ModelRepresentation(model);
        return result;
    }

    /**
     * @param modelId
     * @param cascade
     * @param deleteRuntimeApp
     * @return
     */
    @RequestMapping(value = "/models/{modelId}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteModel(@PathVariable String modelId, @RequestParam(required = false) Boolean cascade, @RequestParam(required = false) Boolean deleteRuntimeApp) {

        Model model = modelService.getModel(modelId);
        Map map = new HashMap<>();
        try {
            String currentUserId = SecurityUtils.getCurrentUserId();
            boolean currentUserIsOwner = currentUserId.equals(model.getCreatedBy());
            if (currentUserIsOwner) {
                modelService.deleteModel(model.getId(), Boolean.TRUE.equals(cascade), Boolean.TRUE.equals(deleteRuntimeApp));
            }
            map.put("success", true);
            return new ResponseEntity<Object>(map, HttpStatus.OK);
        } catch (Exception e) {
            map.put("success", false);
            map.put("message", "Model cannot be deleted");
            return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);
        }
    }


//    @RequestMapping(value = "/models/deploy", method = RequestMethod.POST, produces = "application/json")
//    public ResponseEntity<Object> deployModelToApp(@RequestBody DeployRepresentation appModel) {
//        Map map=new HashMap<>();
//        Boolean isKeyAlreadyExists = modelService.validateModelKey(null, AbstractModel.MODEL_TYPE_APP,appModel.getAppDefinition().getKey());
//        if (isKeyAlreadyExists) {
//            map.put("success",false);
//            return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);
//            //throw new BadRequestException("Provided model key already exists: " + appModel.getAppDefinition().getKey());
//        }
//        String json = null;
//        try {
//            json = objectMapper.writeValueAsString(new AppDefinition());
//        } catch (Exception e) {
//            log.error("Error creating app definition", e);
//            throw new InternalServerErrorException("Error creating app definition");
//        }
//        ModelRepresentation modelRepresentation=new ModelRepresentation();
//        Model model;
//        modelRepresentation.setName(appModel.getAppDefinition().getName());
//        modelRepresentation.setKey(appModel.getAppDefinition().getKey());
//        modelRepresentation.setModelType(AbstractModel.MODEL_TYPE_APP);
//        modelRepresentation.setDescription(appModel.getAppDefinition().getDescription());
//        User user = SecurityUtils.getCurrentUserObject();
//        model=modelService.createModel(modelRepresentation, json, user);
//        String editorJson = null;
//        try {
//            editorJson = objectMapper.writeValueAsString(appModel.getAppDefinition().getDefinition());
//        } catch (Exception e) {
//            log.error("Error while processing app definition json " + modelId, e);
//            //throw new InternalServerErrorException("App definition could not be saved " + modelId);
//        }
//        model = modelService.saveModel(model, editorJson, null, false, null, user);
//        map.put("success",true);
//        return new ResponseEntity<Object>(map, HttpStatus.OK);
//    }

    /**
     * @param modelRepresentation
     * @return
     */
    @RequestMapping(value = "/models", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Object> createModel(@RequestBody ModelRepresentation modelRepresentation) {
        modelRepresentation.setKey(modelRepresentation.getKey().replaceAll(" ", ""));
        Map map = new HashMap<>();
        Boolean isKeyAlreadyExists = modelService.validateModelKey(null, modelRepresentation.getModelType(), modelRepresentation.getKey());
        if (isKeyAlreadyExists) {
            map.put("success", false);
            map.put("message", "Provided model key already exists");
            return new ResponseEntity<Object>(map, HttpStatus.OK);
        }
        String json = null;
        if (modelRepresentation.getModelType() != null && modelRepresentation.getModelType().equals(AbstractModel.MODEL_TYPE_APP)) {
//            try {
//                json = objectMapper.writeValueAsString(new AppDefinition());
//            } catch (Exception e) {
//                log.error("Error creating app definition", e);
//                throw new InternalServerErrorException("Error creating app definition");
//            }
        } else {
            ObjectNode editorNode = objectMapper.createObjectNode();
            editorNode.put("id", "canvas");
            editorNode.put("resourceId", "canvas");
            ObjectNode stencilSetNode = objectMapper.createObjectNode();
            stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
            editorNode.put("stencilset", stencilSetNode);
            ObjectNode propertiesNode = objectMapper.createObjectNode();
            propertiesNode.put("process_id", modelRepresentation.getKey().replaceAll(" ",""));
            propertiesNode.put("name", modelRepresentation.getName());
            if (StringUtils.isNotEmpty(modelRepresentation.getDescription())) {
                propertiesNode.put("documentation", modelRepresentation.getDescription());
            }
            editorNode.put("properties", propertiesNode);

            ArrayNode childShapeArray = objectMapper.createArrayNode();
            editorNode.put("childShapes", childShapeArray);
            ObjectNode childNode = objectMapper.createObjectNode();
            childShapeArray.add(childNode);
            ObjectNode boundsNode = objectMapper.createObjectNode();
            childNode.put("bounds", boundsNode);
            ObjectNode lowerRightNode = objectMapper.createObjectNode();
            boundsNode.put("lowerRight", lowerRightNode);
            lowerRightNode.put("x", 130);
            lowerRightNode.put("y", 193);
            ObjectNode upperLeftNode = objectMapper.createObjectNode();
            boundsNode.put("upperLeft", upperLeftNode);
            upperLeftNode.put("x", 100);
            upperLeftNode.put("y", 163);
            childNode.put("childShapes", objectMapper.createArrayNode());
            childNode.put("dockers", objectMapper.createArrayNode());
            childNode.put("outgoing", objectMapper.createArrayNode());
            childNode.put("resourceId", "startEvent1");
            ObjectNode stencilNode = objectMapper.createObjectNode();
            childNode.put("stencil", stencilNode);
            stencilNode.put("id", "StartNoneEvent");
            json = editorNode.toString();
        }

        Model newModel = modelService.createModel(modelRepresentation, json, SecurityUtils.getCurrentUserObject());
        if (newModel != null) {
            map.put("success", true);
            map.put("message", "Create Success");
            map.put("id", newModel.getId());
            return new ResponseEntity<Object>(map, HttpStatus.OK);
        } else {
            return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @param modelId
     * @param modelRepresentation
     * @return
     */
    @RequestMapping(value = "/models/{modelId}/clone", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Object> duplicateModel(@PathVariable String modelId, @RequestBody ModelRepresentation modelRepresentation) {

        String json = null;
        Model model = null;
        if (modelId != null) {
            model = modelService.getModel(modelId);
            json = model.getModelEditorJson();
        }
        Map map = new HashMap<>();
        if (model == null) {
            map.put("success", false);
            map.put("message", "Error duplicating model : Unknown original model");
            return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);
        }
        Boolean isKeyAlreadyExists = modelService.validateModelKey(null, modelRepresentation.getModelType(), modelRepresentation.getKey());
        if (isKeyAlreadyExists) {
            map.put("success", false);
            map.put("message", "Provided model key already exists");
            return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);
        }

        if (modelRepresentation.getModelType() != null && modelRepresentation.getModelType().equals(AbstractModel.MODEL_TYPE_FORM)) {
            // nothing to do special for forms (just clone the json)
        } else if (modelRepresentation.getModelType() != null && modelRepresentation.getModelType().equals(AbstractModel.MODEL_TYPE_APP)) {
            // nothing to do special for applications (just clone the json)
        } else if (modelRepresentation.getModelType() != null && modelRepresentation.getModelType().equals(AbstractModel.MODEL_TYPE_DECISION_TABLE)) {
            // Decision Table model
        } else {
            // BPMN model
            ObjectNode editorNode = null;
            try {
                ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(json);
                editorNode = deleteEmbededReferencesFromBPMNModel(editorJsonNode);
                ObjectNode propertiesNode = (ObjectNode) editorNode.get("properties");
                String processId = modelRepresentation.getKey().replaceAll(" ", "");
                propertiesNode.put("process_id", processId);
                propertiesNode.put("name", modelRepresentation.getName());
                if (StringUtils.isNotEmpty(modelRepresentation.getDescription())) {
                    propertiesNode.put("documentation", modelRepresentation.getDescription());
                }
                editorNode.put("properties", propertiesNode);

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (editorNode != null) {
                json = editorNode.toString();
            }
        }
        modelRepresentation.setStatus(MODEL_STATUS_UNPUBLISH);
        // create the new model
        Model newModel = modelService.createModel(modelRepresentation, json, SecurityUtils.getCurrentUserObject());
        // copy also the thumbnail
        byte[] imageBytes = model.getThumbnail();
        newModel = modelService.saveModel(newModel, newModel.getModelEditorJson(), imageBytes, false, newModel.getComment(), SecurityUtils.getCurrentUserObject());

        if (newModel != null) {
            map.put("success", true);
            map.put("message", "Clone Success");
            map.put("id", newModel.getId());
            return new ResponseEntity<Object>(map, HttpStatus.OK);
        } else {
            return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);
        }
    }


    protected ObjectNode deleteEmbededReferencesFromBPMNModel(ObjectNode editorJsonNode) {
        try {
            internalDeleteNodeByNameFromBPMNModel(editorJsonNode, "formreference");
            internalDeleteNodeByNameFromBPMNModel(editorJsonNode, "subprocessreference");
            return editorJsonNode;
        } catch (Exception e) {
            throw new InternalServerErrorException("Cannot delete the external references");
        }
    }

    protected void internalDeleteNodeByNameFromBPMNModel(JsonNode editorJsonNode, String propertyName) {
        JsonNode childShapesNode = editorJsonNode.get("childShapes");
        if (childShapesNode != null && childShapesNode.isArray()) {
            ArrayNode childShapesArrayNode = (ArrayNode) childShapesNode;
            for (JsonNode childShapeNode : childShapesArrayNode) {
                // Properties
                ObjectNode properties = (ObjectNode) childShapeNode.get("properties");
                if (properties != null && properties.has(propertyName)) {
                    JsonNode propertyNode = properties.get(propertyName);
                    if (propertyNode != null) {
                        properties.remove(propertyName);
                    }
                }

                // Potential nested child shapes
                if (childShapeNode.has("childShapes")) {
                    internalDeleteNodeByNameFromBPMNModel(childShapeNode, propertyName);
                }

            }
        }
    }


    /**
     * @param filter
     * @param sort
     * @param modelType
     * @param request
     * @return
     */
    @RequestMapping(value = "/models", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> getModelLists(@RequestParam(required = false) String filter, @RequestParam(required = false) String sort, @RequestParam Integer modelType,
                                            HttpServletRequest request) {
        Map map = new HashMap<>();
        String filterName = null;
        String filterKey = null;
        String status = null;
        int page=1,limit=10;
        List<NameValuePair> params = URLEncodedUtils.parse(request.getQueryString(), Charset.forName("UTF-8"));
        if (params != null) {
            for (NameValuePair nameValuePair : params) {
                if ("name".equalsIgnoreCase(nameValuePair.getName())) {
                    if(!nameValuePair.getValue().isEmpty()){
                        filterName = makeValidFilterText(nameValuePair.getValue());
                    }
                }
                if ("key".equalsIgnoreCase(nameValuePair.getName())) {
                    if(!nameValuePair.getValue().isEmpty()){
                        filterKey = makeValidFilterText(nameValuePair.getValue());
                    }
                }
                if ("status".equalsIgnoreCase(nameValuePair.getName())) {
                    if(!nameValuePair.getValue().isEmpty()){
                        status = nameValuePair.getValue();
                    }
                }
                if("page".equals(nameValuePair.getName())){
                    page=Integer.parseInt(nameValuePair.getValue())-1;
                }
                if("limit".equals(nameValuePair.getName())){
                    limit=Integer.parseInt(nameValuePair.getValue());
                }
            }
        }

        List<ModelRepresentation> resultList = new ArrayList<ModelRepresentation>();
        List<Model> models = null;

        User user = SecurityUtils.getCurrentUserObject();
        Page<Model> modelPage;
        if (filterName==null && filterKey==null) {
            Pageable pageable = new PageRequest(page,limit,getSort(sort, false));
            if(status==null){
                modelPage = modelRepository.findPageModelsCreatedBy(user.getId(), modelType, pageable);
            }
            else {
                modelPage = modelRepository.findPageModelsCreatedByStatus(user.getId(), modelType, status,pageable);
            }

            models=modelPage.getContent();
        } else {
            Pageable pageable = new PageRequest(page,limit,getSort(sort, false));
            if(status==null){
                modelPage = modelRepository.findPageModelsCreatedBy(user.getId(), modelType,filterName,filterKey,pageable);
            }else {
                modelPage = modelRepository.findPageModelsCreatedByStatus(user.getId(), modelType,status,filterName,filterKey,pageable);
            }

            models=modelPage.getContent();
        }
        if (CollectionUtils.isNotEmpty(models)) {
            List<String> addedModelIds = new ArrayList<String>();
            for (Model model : models) {
                if (addedModelIds.contains(model.getId()) == false) {
                    addedModelIds.add(model.getId());
                    ModelRepresentation representation = new ModelRepresentation(model);
                    resultList.add(representation);
                }
            }
        }
        map.put("totalElements", modelPage.getTotalElements());
        map.put("list",resultList);

        map.put("run",modelRepository.countByModelStatusAndUser("Published",user.getId()));
        map.put("model",modelRepository.countByModelStatusAndUser("Unpublished",user.getId()));
        map.put("completed",modelRepository.countByModelStatusAndUser("Complete",user.getId()));
        map.put("totalProcess",modelRepository.countByUser(user.getId(),AbstractModel.MODEL_TYPE_BPMN));
        //TableResultResponse<ModelRepresentation> result = new  TableResultResponse<ModelRepresentation>(modelPage.getTotalElements(),resultList);
        return new ResponseEntity<Object>(map,HttpStatus.OK);
    }
    protected String makeValidFilterText(String filterText) {
        String validFilter = null;

        if (filterText != null) {
            String trimmed = StringUtils.trim(filterText);
            if (trimmed.length() >= Constant.MIN_FILTER_LENGTH) {
                validFilter = "%" + trimmed.toLowerCase() + "%";
            }
        }
        return validFilter;
    }

    protected Sort getSort(String sort, boolean prefixWithProcessModel) {
        String propName;
        Sort.Direction direction;
        if (Constant.SORT_NAME_ASC.equals(sort)) {
            if (prefixWithProcessModel) {
                propName = "model.name";
            } else {
                propName = "name";
            }
            direction = Sort.Direction.ASC;
        } else if (Constant.SORT_NAME_DESC.equals(sort)) {
            if (prefixWithProcessModel) {
                propName = "model.name";
            } else {
                propName = "name";
            }
            direction = Sort.Direction.DESC;
        } else if (Constant.SORT_MODIFIED_ASC.equals(sort)) {
            if (prefixWithProcessModel) {
                propName = "model.lastUpdated";
            } else {
                propName = "lastUpdated";
            }
            direction = Sort.Direction.ASC;
        } else {
            // Default sorting
            if (prefixWithProcessModel) {
                propName = "model.lastUpdated";
            } else {
                propName = "lastUpdated";
            }
            direction = Sort.Direction.DESC;
        }
        return new Sort(direction, propName);
    }
    public byte[] getModelThumbnail(String modelId) {
        Model model = modelService.getModel(modelId);
        if (model == null) {
            throw new NonJsonResourceNotFoundException();
        }
        return model.getThumbnail();
    }


    /**
     * @param name
     * @param key
     * @param description
     * @param modelId
     * @return
     */
    @RequestMapping(value = "/modelToTemplate", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> modelToTemplate(@RequestParam(value = "name", required = true) String name,
                                                  @RequestParam(value = "key", required = true) String key,
                                                  @RequestParam(value = "description", required = false) String description,
                                                  @RequestParam(value = "modelId", required = true) String modelId){
        String json = null;
        Model model = null;
        if (modelId != null) {
            model = modelService.getModel(modelId);
            json = model.getModelEditorJson();
        }
        Map map = new HashMap<>();
        if (model == null) {
            map.put("success", false);
            map.put("message", "Error duplicating model : Unknown original model");
            return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);
        }
        Boolean isKeyAlreadyExists = modelService.validateModelKey(null, AbstractModel.MODEL_TYPE_TEMPLATE, key);
        if (isKeyAlreadyExists) {
            map.put("success", false);
            map.put("message", "Provided model key already exists");
            return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);
        }
        ObjectNode editorNode = null;
        try {
            ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(json);

            editorNode = deleteEmbededReferencesFromBPMNModel(editorJsonNode);

            ObjectNode propertiesNode = (ObjectNode) editorNode.get("properties");
            String processId = key.replaceAll(" ", "");
            propertiesNode.put("process_id", processId);
            propertiesNode.put("name", name);
            if (StringUtils.isNotEmpty(description)) {
                propertiesNode.put("documentation", description);
            }
            editorNode.put("properties", propertiesNode);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (editorNode != null) {
            json = editorNode.toString();
        }

        Model newModel = modelService.createModelTemplate(name,key,description, json, SecurityUtils.getCurrentUserObject());
        byte[] imageBytes = model.getThumbnail();
        newModel = modelService.saveModel(newModel, newModel.getModelEditorJson(), imageBytes, false, newModel.getComment(), SecurityUtils.getCurrentUserObject());
        if (newModel != null) {
            map.put("success", true);
            map.put("message", "Collect Success");
            map.put("id", newModel.getId());
            return new ResponseEntity<Object>(map, HttpStatus.OK);
        } else {
            return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * @param modelId
     * @return
     */
    @RequestMapping(value = "/models/{modelId}/thumbnail", method = RequestMethod.GET)
    public String getModelThumbnail1(@PathVariable String modelId) {
        String result=(new BASE64Encoder()).encodeBuffer(getModelThumbnail(modelId));
        return result;
    }

    @RequestMapping(value = "/models/{modelId}/editor/json", method = RequestMethod.GET, produces = "application/json")
    public ObjectNode getModelJSON(@PathVariable String modelId) {
        Model model = modelService.getModel(modelId);
        ObjectNode modelNode = objectMapper.createObjectNode();
        modelNode.put("modelId", model.getId());
        modelNode.put("name", model.getName());
        modelNode.put("key", model.getKey());
        modelNode.put("description", model.getDescription());
        modelNode.putPOJO("lastUpdated", model.getLastUpdated());
        modelNode.put("lastUpdatedBy", model.getLastUpdatedBy());
        if (StringUtils.isNotEmpty(model.getModelEditorJson())) {
            try {
                ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(model.getModelEditorJson());
                editorJsonNode.put("modelType", "model");
                modelNode.put("model", editorJsonNode);
            } catch (Exception e) {
                log.error("Error reading editor json " + modelId, e);
                throw new InternalServerErrorException("Error reading editor json " + modelId);
            }

        } else {
            ObjectNode editorJsonNode = objectMapper.createObjectNode();
            editorJsonNode.put("id", "canvas");
            editorJsonNode.put("resourceId", "canvas");
            ObjectNode stencilSetNode = objectMapper.createObjectNode();
            stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
            editorJsonNode.put("modelType", "model");
            modelNode.put("model", editorJsonNode);
        }
        return modelNode;
    }

    @RequestMapping(value = "/models/{modelId}/editor/json", method = RequestMethod.POST)
    public ModelRepresentation saveModel(@PathVariable String modelId, @RequestBody MultiValueMap<String, String> values) {
        long lastUpdated = -1L;
        String lastUpdatedString = values.getFirst("lastUpdated");
        if (lastUpdatedString == null) {
            throw new BadRequestException("Missing lastUpdated date");
        }
        try {
            Date readValue = objectMapper.getDeserializationConfig().getDateFormat().parse(lastUpdatedString);
            lastUpdated = readValue.getTime();
        } catch (ParseException e) {
            throw new BadRequestException("Invalid lastUpdated date: '" + lastUpdatedString + "'");
        }

        Model model = modelService.getModel(modelId);
        User currentUser = SecurityUtils.getCurrentUserObject();
        boolean currentUserIsOwner = model.getLastUpdatedBy().equals(currentUser.getId());
        String resolveAction = values.getFirst("conflictResolveAction");
        if (model.getLastUpdated().getTime() != lastUpdated) {

            if (RESOLVE_ACTION_SAVE_AS.equals(resolveAction)) {

                String saveAs = values.getFirst("saveAs");
                String json = values.getFirst("json_xml");
                System.out.println(json);
                return createNewModel(saveAs, model.getDescription(), model.getModelType(), json);

            } else if (RESOLVE_ACTION_OVERWRITE.equals(resolveAction)) {
                return updateModel(model, values, false);
            } else if (RESOLVE_ACTION_NEW_VERSION.equals(resolveAction)) {
                return updateModel(model, values, true);
            } else {
                String isNewVersionString = values.getFirst("newversion");
                if (currentUserIsOwner && "true".equals(isNewVersionString)) {
                    return updateModel(model, values, true);
                } else {
                    ConflictingRequestException exception = new ConflictingRequestException("Process model was updated in the meantime");
                    exception.addCustomData("userFullName", model.getLastUpdatedBy());
                    exception.addCustomData("newVersionAllowed", currentUserIsOwner);
                    throw exception;
                }
            }

        } else {
            return updateModel(model, values, false);

        }
    }
    protected ModelRepresentation updateModel(Model model, MultiValueMap<String, String> values, boolean forceNewVersion) {
        String name = values.getFirst("name");
        String key = values.getFirst("key");
        String description = values.getFirst("description");
        String isNewVersionString = values.getFirst("newversion");
        String newVersionComment = null;

        Boolean isKeyAlreadyExists = modelService.validateModelKey(model, model.getModelType(), key);
        if (isKeyAlreadyExists) {
            throw new BadRequestException("Model with provided key already exists " + key);
        }

        boolean newVersion = false;
        if (forceNewVersion) {
            newVersion = true;
            newVersionComment = values.getFirst("comment");
        } else {
            if (isNewVersionString != null) {
                newVersion = "true".equals(isNewVersionString);
                newVersionComment = values.getFirst("comment");
            }
        }

        String json = values.getFirst("json_xml");

        try {
            model = modelService.saveModel(model.getId(), name, key, description, json, newVersion,
                    newVersionComment, SecurityUtils.getCurrentUserObject());
            return new ModelRepresentation(model);

        } catch (Exception e) {
            log.error("Error saving model " + model.getId(), e);
            throw new BadRequestException("Process model could not be saved " + model.getId());
        }
    }

    protected ModelRepresentation createNewModel(String name, String description, Integer modelType, String editorJson) {
        ModelRepresentation model = new ModelRepresentation();
        model.setName(name);
        model.setDescription(description);
        model.setModelType(modelType);
        Model newModel = modelService.createModel(model, editorJson, SecurityUtils.getCurrentUserObject());
        return new ModelRepresentation(newModel);
    }


//    @RequestMapping(value = "/rest/models/{modelId}", method = RequestMethod.PUT)
//    public ModelRepresentation updateModel(@PathVariable String modelId, @RequestBody ModelRepresentation updatedModel) {
//        Model model = modelService.getModel(modelId);
//        Boolean isKeyAlreadyExists = modelService.validateModelKey(model, model.getModelType(), updatedModel.getKey());
//        if (isKeyAlreadyExists) {
//            throw new BadRequestException("Model with provided key already exists " + updatedModel.getKey());
//        }
//        try {
//            updatedModel.updateModel(model);
//            modelRepository.save(model);
//            ModelRepresentation result = new ModelRepresentation(model);
//            return result;
//
//        } catch (Exception e) {
//            throw new BadRequestException("Model cannot be updated: " + modelId);
//        }
//    }

//    @RequestMapping(value = "/rest/templateToModel", method = RequestMethod.GET, produces = "application/json")
//    public ResponseEntity<Object> templateToModel(@RequestParam(value = "name", required = true) String name,
//                                                  @RequestParam(value = "key", required = true) String key,
//                                                  @RequestParam(value = "description", required = false) String description,
//                                                  @RequestParam(value = "modelId", required = true) String modelId){
//        String json = null;
//        Model model = null;
//        if (modelId != null) {
//            model = modelService.getModel(modelId);
//            json = model.getModelEditorJson();
//        }
//        Map map = new HashMap<>();
//        if (model == null) {
//            map.put("success", false);
//            map.put("message", "Error duplicating model : Unknown original model");
//            return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);
//        }
//        Boolean isKeyAlreadyExists = modelService.validateModelKey(null, AbstractModel.MODEL_TYPE_BPMN, key);
//        if (isKeyAlreadyExists) {
//            map.put("success", false);
//            map.put("message", "Provided model key already exists");
//            return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);
//            // throw new BadRequestException("Provided model key already exists: " + modelRepresentation.getKey());
//        }
//        ObjectNode editorNode = null;
//        try {
//            ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(json);
//            editorNode = deleteEmbededReferencesFromBPMNModel(editorJsonNode);
//            ObjectNode propertiesNode = (ObjectNode) editorNode.get("properties");
//            String processId = key.replaceAll(" ", "");
//            propertiesNode.put("process_id", processId);
//            propertiesNode.put("name", name);
//            if (StringUtils.isNotEmpty(description)) {
//                propertiesNode.put("documentation", description);
//            }
//            editorNode.put("properties", propertiesNode);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (editorNode != null) {
//            json = editorNode.toString();
//        }
//
//        Model newModel = modelService.createModel(name,key,description, json, SecurityUtils.getCurrentUserObject());
//        byte[] imageBytes = model.getThumbnail();
//        newModel = modelService.saveModel(newModel, newModel.getModelEditorJson(), imageBytes, false, newModel.getComment(), SecurityUtils.getCurrentUserObject());
//        if (newModel != null) {
//            map.put("success", true);
//            map.put("message", "Collect Success");
//            map.put("id", newModel.getId());
//            return new ResponseEntity<Object>(map, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);
//        }
//    }


}
