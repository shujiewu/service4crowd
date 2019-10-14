package cn.edu.buaa.act.workflow.model;

import cn.edu.buaa.act.workflow.common.RestVariable;
import lombok.Getter;
import lombok.Setter;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProcessInstanceRepresentation {

    protected String id;
    protected String name;
    protected String businessKey;
    protected String processDefinitionId;
    protected String tenantId;
    protected Date started;
    protected Date ended;
    protected UserRepresentation startedBy;
    protected String processDefinitionName;
    protected String processDefinitionDescription;
    protected String processDefinitionKey;
    protected String processDefinitionCategory;
    protected int processDefinitionVersion;
    protected String processDefinitionDeploymentId;
    protected boolean graphicalNotationDefined;
    protected boolean startFormDefined;
    
    protected List<RestVariable> variables = new ArrayList<RestVariable>();

    public ProcessInstanceRepresentation(ProcessInstance processInstance, boolean graphicalNotation) {
        this.id = processInstance.getId();
        this.name = processInstance.getName();
        this.businessKey = processInstance.getBusinessKey();
        this.processDefinitionId = processInstance.getProcessDefinitionId();
        this.tenantId = processInstance.getTenantId();
        this.graphicalNotationDefined = graphicalNotation;
        this.startedBy = new UserRepresentation(processInstance.getStartUserId());
    }
    public ProcessInstanceRepresentation(HistoricProcessInstance processInstance, boolean graphicalNotation) {
        this.id = processInstance.getId();
        this.name = processInstance.getName();
        this.businessKey = processInstance.getBusinessKey();
        this.processDefinitionId = processInstance.getProcessDefinitionId();
        this.tenantId = processInstance.getTenantId();
        this.graphicalNotationDefined = graphicalNotation;
        this.started = processInstance.getStartTime();
        this.ended = processInstance.getEndTime();
        this.startedBy = new UserRepresentation(processInstance.getStartUserId());
    }
    public ProcessInstanceRepresentation(ProcessInstance processInstance, ProcessDefinition processDefinition, boolean graphicalNotation) {
        this(processInstance, graphicalNotation);
        mapProcessDefinition(processDefinition);
    }
    public ProcessInstanceRepresentation(HistoricProcessInstance processInstance, ProcessDefinition processDefinition, boolean graphicalNotation) {
        this(processInstance, graphicalNotation);
        mapProcessDefinition(processDefinition);
    }
    protected void mapProcessDefinition(ProcessDefinition processDefinition) {
        if (processDefinition != null) {
            this.processDefinitionName = processDefinition.getName();
            this.processDefinitionDescription = processDefinition.getDescription();
            this.processDefinitionKey = processDefinition.getKey();
            this.processDefinitionCategory = processDefinition.getCategory();
            this.processDefinitionVersion = processDefinition.getVersion();
            this.processDefinitionDeploymentId = processDefinition.getDeploymentId();
        }
    }
    public ProcessInstanceRepresentation() {}
}
