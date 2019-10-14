package cn.edu.buaa.act.workflow.controller;
import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.workflow.exception.BadRequestException;
import cn.edu.buaa.act.workflow.exception.NotFoundException;
import cn.edu.buaa.act.workflow.model.CreateProcessInstanceRepresentation;
import cn.edu.buaa.act.workflow.model.ProcessInstanceRepresentation;
import cn.edu.buaa.act.workflow.service.impl.PermissionService;
import cn.edu.buaa.act.workflow.service.impl.ProcessInstanceService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.form.model.FormDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static cn.edu.buaa.act.workflow.common.Constant.MODEL_STATUS_COMPLETE;

/**
 * @author wsj
 */
@RequestMapping(value = "/workflow")
@RestController
public class ProcessInstancesController {

	@Autowired
	protected ProcessInstanceService processInstanceService;
	@Autowired
	protected HistoryService historyService;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private RepositoryService repositoryService;

	@RequestMapping(value = "/processInstance", method = RequestMethod.POST)
    public ProcessInstanceRepresentation startNewProcessInstance(@RequestBody CreateProcessInstanceRepresentation startRequest) {
		if (StringUtils.isEmpty(startRequest.getProcessDefinitionId())) {
			throw new BadRequestException("Process definition id is required");
		}
		ProcessDefinition processDefinition =processInstanceService.getProcessDefinitionById(startRequest.getProcessDefinitionId());
		Map<String, Object> variables = null;
		ProcessInstance processInstance = processInstanceService.startProcessInstance(startRequest.getProcessDefinitionId(), variables, startRequest.getName());
		HistoricProcessInstance historicProcess = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
		return new ProcessInstanceRepresentation(historicProcess, processDefinition, ((ProcessDefinitionEntity) processDefinition).isGraphicalNotationDefined());
	}

	@RequestMapping(value = "/processInstance/{processInstanceId}", method = RequestMethod.POST)
	public ProcessInstanceRepresentation getProcessInstance(@PathVariable String processInstanceId) {
		HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		if (!permissionService.hasPermissionOnProcessInstance(BaseContextHandler.getUserID(),processInstanceId)) {
			throw new NotFoundException("Process with id: " + processInstanceId + " does not exist or is not available for this user");
		}

		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());


		ProcessInstanceRepresentation processInstanceResult = new ProcessInstanceRepresentation(processInstance, processDefinition, processDefinition.isGraphicalNotationDefined());

//		FormDefinition formDefinition = getStartFormDefinition(processInstance.getProcessDefinitionId(), processDefinition, processInstance.getId());
//		if (formDefinition != null) {
//			processInstanceResult.setStartFormDefined(true);
//		}
		return processInstanceResult;
	}






}
