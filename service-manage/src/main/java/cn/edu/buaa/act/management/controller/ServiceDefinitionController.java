package cn.edu.buaa.act.management.controller;

import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.management.entity.ServiceDefinition;
import cn.edu.buaa.act.management.exception.NoServiceDefinitionException;
import cn.edu.buaa.act.management.service.ServiceDefine;
import cn.edu.buaa.act.management.service.ServiceDeploy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * ServiceDefinationController
 * 定义服务的相关操作
 * @author wsj
 * @date 2018/9/21
 */
@RestController
@RequestMapping("/service/definition")
public class ServiceDefinitionController {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDefinitionController.class);


    @Autowired
    ServiceDeployController serviceDeployController;

    @Autowired
    ServiceDefine serviceDefine;


    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public TableResultResponse<ServiceDefinition> list(Pageable pageable, @RequestParam(required = false) String searchName) {
        Page<ServiceDefinition> serviceDefinitions = this.serviceDefine.findDefinitionByNameLike(pageable, searchName);
        return new TableResultResponse<ServiceDefinition>(serviceDefinitions.getTotalElements(),serviceDefinitions.getContent());
    }


    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceDefinition save(@RequestParam("definitionName") String definitionName, @RequestParam("registeredName") String registeredName,@RequestParam("properties") Map<String,String> properties,
                                  @RequestParam(value = "deploy", defaultValue = "false") boolean deploy) {
        ServiceDefinition serviceDefinition = this.serviceDefine.createServiceApp(definitionName,registeredName,deploy);
        // 还没有传递属性
        return serviceDefinition;
    }



    @RequestMapping(value = "/{name}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("name") String name) {
        this.serviceDefine.deleteDefinition(name);
    }

    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ServiceDefinition display(@PathVariable("name") String name) {
        ServiceDefinition definition = this.serviceDefine.findDefinitionByName(name);
        if (definition == null) {
            throw new NoServiceDefinitionException(name);
        }
        return definition;
    }

}
