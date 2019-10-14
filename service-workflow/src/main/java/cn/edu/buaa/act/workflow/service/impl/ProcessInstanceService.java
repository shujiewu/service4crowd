package cn.edu.buaa.act.workflow.service.impl;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * @author wsj
 */
@Service
public class ProcessInstanceService {

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    protected HistoryService historyService;


    public ProcessDefinition getProcessDefinitionById(String processDefinitionId) {
        return repositoryService.getProcessDefinition(processDefinitionId);
    }
    public ProcessInstance startProcessInstance(String processDefinitionId, Map<String, Object> variables, String processInstanceName) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, variables);
        if (!processInstance.isEnded() && processInstanceName != null) {
            runtimeService.setProcessInstanceName(processInstance.getId(), processInstanceName);
        }
        return processInstance;
    }
}
