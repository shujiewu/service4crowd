
package cn.edu.buaa.act.workflow.controller;

import cn.edu.buaa.act.common.entity.AtomicService;
import cn.edu.buaa.act.common.entity.MicroService;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.workflow.common.Constant;
import cn.edu.buaa.act.workflow.exception.InternalServerErrorException;
import cn.edu.buaa.act.workflow.exception.NotFoundException;
import cn.edu.buaa.act.workflow.feign.IServiceManage;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wsj
 */
@RequestMapping(value = "/workflow")
@RestController
public class StencilSetController {

    private final Logger log = LoggerFactory.getLogger(StencilSetController.class);
    protected ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    IServiceManage iServiceManage;

    @RequestMapping(value = "/stencilsets/{microServiceId}/atomicService", method = RequestMethod.GET, produces = "application/json")
    public TableResultResponse<AtomicService> getAtomicServiceList(@PathVariable String microServiceId) {
        MicroService microService = Constant.microServiceMap.get(microServiceId);
        if(microService==null){
               throw new NotFoundException();
        }
        if(microService.getAtomicServiceList()!=null){
            return new TableResultResponse<>(microService.getAtomicServiceList().size(),microService.getAtomicServiceList());
        }
        else {
            return new TableResultResponse<>();
        }
    }

    @RequestMapping(value = "/stencilsets/editor", method = RequestMethod.GET, produces = "application/json")
    public JSONObject getStencilSetForEditor() {
        try {
            JSONObject jsonResult = iServiceManage.serviceConfigList();
            JSONArray microSeriveArray = jsonResult.getJSONObject("data").getJSONArray("rows");
            for(int i=0;i<microSeriveArray.size();i++){
                MicroService microService = JSONObject.parseObject(microSeriveArray.get(i).toString(),MicroService.class);
                Constant.microServiceMap.put(microService.getServiceName(),microService);
            }

            JsonNode stencilNode = objectMapper.readTree(this.getClass().getClassLoader().getResourceAsStream("stencilset.json.zh-cn"));
            return getServiceList(stencilNode);
        } catch (Exception e) {
            log.error("Error reading bpmn stencil set json", e);
            throw new InternalServerErrorException("Error reading bpmn stencil set json");
        }
    }

    public JSONObject getServiceList(JsonNode stencilNode){
        JSONObject jsonObject = JSONObject.parseObject(stencilNode.toString());
        if(Constant.microServiceMap.size()==0){
            return JSONObject.parseObject(stencilNode.toString());
        }
        Constant.microServiceMap.forEach(((name, microService) -> {
            String id= "UserTask-"+ microService.getServiceName();
            ObjectNode stencil = objectMapper.createObjectNode();
            stencil.put("type", "node");
            stencil.put("id", id);
            stencil.put("title", microService.getServiceName());
            stencil.put("description", microService.getDescription());
            stencil.put("view", serviceView);
            stencil.put("icon", serviceIcon);
            ArrayNode groups = objectMapper.createArrayNode();
            groups.add("MicroService");
            stencil.put("groups", groups);
            ArrayNode propertyPackages = objectMapper.createArrayNode();
            baseUserPackage.forEach(s -> {
                propertyPackages.add(s);
            });
            propertyPackages.add("serviceparapackage");
            propertyPackages.add("serviceoutputpackage");
            stencil.put("propertyPackages", propertyPackages);
            stencil.put("hiddenPropertyPackages", objectMapper.createArrayNode());

            ArrayNode roles = objectMapper.createArrayNode();
            baseUserRoles.forEach(s -> {
                roles.add(s);
            });
            stencil.put("roles", roles);

            ArrayNode property = objectMapper.createArrayNode();
            property.add("input");
            property.add("output");
            stencil.put("property", property);

            //objectNode.get("stencils").
            jsonObject.getJSONArray("stencils").add(stencil);
        }));
        return jsonObject;
    }

    private static String serviceView = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n\n   width=\"102\"\n   height=\"82\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"20\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"40\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"60\" oryx:anchors=\"left\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"20\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"40\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"60\" oryx:anchors=\"right\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"40\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\" oryx:minimumSize=\"50 40\">\n\t<rect id=\"text_frame\" oryx:anchors=\"bottom top right left\" x=\"1\" y=\"1\" width=\"94\" height=\"79\" rx=\"10\" ry=\"10\" stroke=\"none\" stroke-width=\"0\" fill=\"none\" />\n\t<rect id=\"bg_frame\" oryx:resize=\"vertical horizontal\" x=\"0\" y=\"0\" width=\"100\" height=\"80\" rx=\"10\" ry=\"10\" stroke=\"#bbbbbb\" stroke-width=\"1\" fill=\"#f9f9f9\" />\n\t\t<text \n\t\t\tfont-size=\"12\" \n\t\t\tid=\"text_name\" \n\t\t\tx=\"50\" \n\t\t\ty=\"40\" \n\t\t\toryx:align=\"middle center\"\n\t\t\toryx:fittoelem=\"text_frame\"\n\t\t\tstroke=\"#373e48\">\n\t\t</text>\n\t\n\t<g id=\"serviceTask\" transform=\"translate(3,3)\">\n\t<path oryx:anchors=\"top left\"\n\t\tstyle=\"fill:#72a7d0;stroke:none\"\n     d=\"M 8,1 7.5,2.875 c 0,0 -0.02438,0.250763 -0.40625,0.4375 C 7.05724,3.330353 7.04387,3.358818 7,3.375 6.6676654,3.4929791 6.3336971,3.6092802 6.03125,3.78125 6.02349,3.78566 6.007733,3.77681 6,3.78125 5.8811373,3.761018 5.8125,3.71875 5.8125,3.71875 l -1.6875,-1 -1.40625,1.4375 0.96875,1.65625 c 0,0 0.065705,0.068637 0.09375,0.1875 0.002,0.00849 -0.00169,0.022138 0,0.03125 C 3.6092802,6.3336971 3.4929791,6.6676654 3.375,7 3.3629836,7.0338489 3.3239228,7.0596246 3.3125,7.09375 3.125763,7.4756184 2.875,7.5 2.875,7.5 L 1,8 l 0,2 1.875,0.5 c 0,0 0.250763,0.02438 0.4375,0.40625 0.017853,0.03651 0.046318,0.04988 0.0625,0.09375 0.1129372,0.318132 0.2124732,0.646641 0.375,0.9375 -0.00302,0.215512 -0.09375,0.34375 -0.09375,0.34375 L 2.6875,13.9375 4.09375,15.34375 5.78125,14.375 c 0,0 0.1229911,-0.09744 0.34375,-0.09375 0.2720511,0.147787 0.5795915,0.23888 0.875,0.34375 0.033849,0.01202 0.059625,0.05108 0.09375,0.0625 C 7.4756199,14.874237 7.5,15.125 7.5,15.125 L 8,17 l 2,0 0.5,-1.875 c 0,0 0.02438,-0.250763 0.40625,-0.4375 0.03651,-0.01785 0.04988,-0.04632 0.09375,-0.0625 0.332335,-0.117979 0.666303,-0.23428 0.96875,-0.40625 0.177303,0.0173 0.28125,0.09375 0.28125,0.09375 l 1.65625,0.96875 1.40625,-1.40625 -0.96875,-1.65625 c 0,0 -0.07645,-0.103947 -0.09375,-0.28125 0.162527,-0.290859 0.262063,-0.619368 0.375,-0.9375 0.01618,-0.04387 0.04465,-0.05724 0.0625,-0.09375 C 14.874237,10.52438 15.125,10.5 15.125,10.5 L 17,10 17,8 15.125,7.5 c 0,0 -0.250763,-0.024382 -0.4375,-0.40625 C 14.669647,7.0572406 14.641181,7.0438697 14.625,7 14.55912,6.8144282 14.520616,6.6141566 14.4375,6.4375 c -0.224363,-0.4866 0,-0.71875 0,-0.71875 L 15.40625,4.0625 14,2.625 l -1.65625,1 c 0,0 -0.253337,0.1695664 -0.71875,-0.03125 l -0.03125,0 C 11.405359,3.5035185 11.198648,3.4455201 11,3.375 10.95613,3.3588185 10.942759,3.3303534 10.90625,3.3125 10.524382,3.125763 10.5,2.875 10.5,2.875 L 10,1 8,1 z m 1,5 c 1.656854,0 3,1.3431458 3,3 0,1.656854 -1.343146,3 -3,3 C 7.3431458,12 6,10.656854 6,9 6,7.3431458 7.3431458,6 9,6 z\" />\n\t</g>\n  \n\t<g id=\"parallel\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M46 70 v8 M50 70 v8 M54 70 v8\" stroke-width=\"2\" />\n\t</g>\n\t\n\t<g id=\"sequential\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" stroke-width=\"2\" d=\"M46,76h10M46,72h10 M46,68h10\"/>\n\t</g>\n\t\n\t<g id=\"compensation\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M 62 74 L 66 70 L 66 78 L 62 74 L 62 70 L 58 74 L 62 78 L 62 74\" stroke-width=\"1\" />\n\t</g>\n  </g>\n</svg>";
    private static String serviceIcon = "activity/list/type.service.png";
    private List<String> basePackage = new ArrayList<String>(Arrays.asList("overrideidpackage", "namepackage", "servicetasktypepackage", "documentationpackage", "asynchronousdefinitionpackage", "servicetaskclasspackage", "servicetaskexpressionpackage", "servicetaskdelegateexpressionpackage", "servicetaskresultvariablepackage"));
    private List<String> baseRoles = new ArrayList<>(Arrays.asList("Activity", "sequence_start", "sequence_end", "ActivitiesMorph", "all"));


    private static String userTaskView = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg\n   xmlns=\"http://www.w3.org/2000/svg\"\n   xmlns:svg=\"http://www.w3.org/2000/svg\"\n   xmlns:oryx=\"http://www.b3mn.org/oryx\"\n   xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n\n   width=\"102\"\n   height=\"82\"\n   version=\"1.0\">\n  <defs></defs>\n  <oryx:magnets>\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"20\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"40\" oryx:anchors=\"left\" />\n  \t<oryx:magnet oryx:cx=\"1\" oryx:cy=\"60\" oryx:anchors=\"left\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"79\" oryx:anchors=\"bottom\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"20\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"40\" oryx:anchors=\"right\" />\n  \t<oryx:magnet oryx:cx=\"99\" oryx:cy=\"60\" oryx:anchors=\"right\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"25\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t<oryx:magnet oryx:cx=\"75\" oryx:cy=\"1\" oryx:anchors=\"top\" />\n  \t\n  \t<oryx:magnet oryx:cx=\"50\" oryx:cy=\"40\" oryx:default=\"yes\" />\n  </oryx:magnets>\n  <g pointer-events=\"fill\" oryx:minimumSize=\"50 40\">\n\t<rect id=\"text_frame\" oryx:anchors=\"bottom top right left\" x=\"1\" y=\"1\" width=\"94\" height=\"79\" rx=\"10\" ry=\"10\" stroke=\"none\" stroke-width=\"0\" fill=\"none\" />\n\t<rect id=\"bg_frame\" oryx:resize=\"vertical horizontal\" x=\"0\" y=\"0\" width=\"100\" height=\"80\" rx=\"10\" ry=\"10\" stroke=\"#bbbbbb\" stroke-width=\"1\" fill=\"#f9f9f9\" />\n\t\t<text \n\t\t\tfont-size=\"12\" \n\t\t\tid=\"text_name\" \n\t\t\tx=\"50\" \n\t\t\ty=\"40\" \n\t\t\toryx:align=\"middle center\"\n\t\t\toryx:fittoelem=\"text_frame\"\n\t\t\tstroke=\"#373e48\">\n\t\t</text>\n\t\n\t<g id=\"userTask\" transform=\"translate(3,3)\">\n\t\t<path oryx:anchors=\"top left\"\n       \t\tstyle=\"fill:#d1b575;stroke:none;\"\n       \t\t d=\"m 1,17 16,0 0,-1.7778 -5.333332,-3.5555 0,-1.7778 c 1.244444,0 1.244444,-2.3111 1.244444,-2.3111 l 0,-3.0222 C 12.555557,0.8221 9.0000001,1.0001 9.0000001,1.0001 c 0,0 -3.5555556,-0.178 -3.9111111,3.5555 l 0,3.0222 c 0,0 0,2.3111 1.2444443,2.3111 l 0,1.7778 L 1,15.2222 1,17 17,17\" \n         />\n\t\t\n\t</g>\n  \n\t<g id=\"parallel\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M46 70 v8 M50 70 v8 M54 70 v8\" stroke-width=\"2\" />\n\t</g>\n\t\n\t<g id=\"sequential\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" stroke-width=\"2\" d=\"M46,76h10M46,72h10 M46,68h10\"/>\n\t</g>\n\t\n\n\t<g id=\"compensation\">\n\t\t<path oryx:anchors=\"bottom\" fill=\"none\" stroke=\"#bbbbbb\" d=\"M 62 74 L 66 70 L 66 78 L 62 74 L 62 70 L 58 74 L 62 78 L 62 74\" stroke-width=\"1\" />\n\t</g>\n  </g>\n</svg>";
    private static String userTaskIcon = "activity/list/type.user.png";
    private List<String> baseUserPackage = new ArrayList<String>(Arrays.asList("overrideidpackage", "namepackage", "servicenamepackage","documentationpackage", "asynchronousdefinitionpackage", "exclusivedefinitionpackage", "executionlistenerspackage", "usertaskassignmentpackage", "formkeydefinitionpackage", "formreferencepackage", "duedatedefinitionpackage", "prioritydefinitionpackage", "formpropertiespackage", "tasklistenerspackage"));
    private List<String> baseUserRoles = new ArrayList<>(Arrays.asList("Activity", "sequence_start", "sequence_end", "ActivitiesMorph", "all"));



//"multiinstance_typepackage", "multiinstance_cardinalitypackage", "multiinstance_collectionpackage", "multiinstance_variablepackage", "multiinstance_conditionpackage", "isforcompensationpackage",


//    @Autowired
//    protected IDataService iDataService;
//
//    public JSONObject getServiceList(JsonNode stencilNode) {
//        List<ServiceEntity> serviceEntityList = iDataService.getServiceList();
//
//        JSONObject jsonObject = JSONObject.parseObject(stencilNode.toString());
//
//        serviceEntityList.forEach(serviceEntity -> {
//            if (serviceEntity.getServiceId().indexOf("ServiceTask") != -1) {
//                ObjectNode stencil = objectMapper.createObjectNode();
//                stencil.put("type", "node");
//                stencil.put("id", serviceEntity.getServiceId());
//                stencil.put("title", serviceEntity.getTitle());
//                stencil.put("description", serviceEntity.getDescription());
//                stencil.put("view", serviceView);
//                stencil.put("icon", serviceIcon);
//
//                ArrayNode groups = objectMapper.createArrayNode();
//                groups.add(serviceEntity.getGroups());
//                stencil.put("groups", groups);
//
//                ArrayNode propertyPackages = objectMapper.createArrayNode();
//                basePackage.forEach(s -> {
//                    propertyPackages.add(s);
//                });
//                propertyPackages.add("serviceparapackage");
//                if (serviceEntity.getServiceId().indexOf("AnswerAggregate") != -1) {
//                    propertyPackages.add("mergeservicetaskfieldspackage");
//                }
//
//
//                stencil.put("propertyPackages", propertyPackages);
//                stencil.put("hiddenPropertyPackages", objectMapper.createArrayNode());
//
//                ArrayNode roles = objectMapper.createArrayNode();
//                baseRoles.forEach(s -> {
//                    roles.add(s);
//                });
//                stencil.put("roles", roles);
//
//                ArrayNode property = objectMapper.createArrayNode();
//                property.add("input");
//                property.add("output");
//                stencil.put("property", property);
//
//                //objectNode.get("stencils").
//                jsonObject.getJSONArray("stencils").add(stencil);
//            }
//            if (serviceEntity.getServiceId().indexOf("UserTask") != -1) {
//                ObjectNode stencil = objectMapper.createObjectNode();
//                stencil.put("type", "node");
//                stencil.put("id", serviceEntity.getServiceId());
//                stencil.put("title", serviceEntity.getTitle());
//                stencil.put("description", serviceEntity.getDescription());
//                stencil.put("view", userTaskView);
//                stencil.put("icon", userTaskIcon);
//
//                ArrayNode groups = objectMapper.createArrayNode();
//                groups.add(serviceEntity.getGroups());
//                stencil.put("groups", groups);
//
//                ArrayNode propertyPackages = objectMapper.createArrayNode();
//                baseUserPackage.forEach(s -> {
//                    propertyPackages.add(s);
//                });
//
//                stencil.put("propertyPackages", propertyPackages);
//                stencil.put("hiddenPropertyPackages", objectMapper.createArrayNode());
//
//                ArrayNode roles = objectMapper.createArrayNode();
//                baseUserRoles.forEach(s -> {
//                    roles.add(s);
//                });
//                stencil.put("roles", roles);
//                //objectNode.get("stencils").
//                jsonObject.getJSONArray("stencils").add(stencil);
//            }
//        });
//        return jsonObject;
//    }
}