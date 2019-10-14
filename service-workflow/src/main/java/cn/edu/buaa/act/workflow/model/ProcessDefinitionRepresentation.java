package cn.edu.buaa.act.workflow.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import org.activiti.engine.repository.ProcessDefinition;

@Getter
@Setter
public class ProcessDefinitionRepresentation{

    protected String id;
    protected String name;
    protected String description;
    protected String key;
    protected String category;
    protected int version;
    protected String deploymentId;
    protected String tenantId;
    protected boolean hasStartForm;
    
    public ProcessDefinitionRepresentation(ProcessDefinition processDefinition) {
        this.id = processDefinition.getId();
        this.name = processDefinition.getName();
        this.description = processDefinition.getDescription();
        this.key = processDefinition.getKey();
        this.category = processDefinition.getCategory();
        this.version = processDefinition.getVersion();
        this.deploymentId = processDefinition.getDeploymentId();
        this.tenantId = processDefinition.getTenantId();
        this.hasStartForm = processDefinition.hasStartFormKey();
    }
    public ProcessDefinitionRepresentation() {}
}
