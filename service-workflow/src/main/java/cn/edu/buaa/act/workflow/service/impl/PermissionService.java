package cn.edu.buaa.act.workflow.service.impl;

import java.util.ArrayList;
import java.util.List;

import cn.edu.buaa.act.workflow.exception.NotFoundException;
import cn.edu.buaa.act.workflow.exception.NotPermittedException;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author wsj
 */
@Service
public class PermissionService {

  @Autowired
  protected TaskService taskService;

  @Autowired
  protected RuntimeService runtimeService;

  @Autowired
  protected RepositoryService repositoryService;

  @Autowired
  protected HistoryService historyService;

  @Autowired
  protected IdentityService identityService;


  /**
   * @param userId
   * @param processInstanceId
   * @return
   */
  public boolean hasPermissionOnProcessInstance(String userId, String processInstanceId) {
    HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    if (historicProcessInstance == null) {
      throw new NotFoundException("Process instance not found for id " + processInstanceId);
    }

    if (historicProcessInstance.getStartUserId() != null && historicProcessInstance.getStartUserId().equals(userId)) {
      return true;
    }

    HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
    historicProcessInstanceQuery.processInstanceId(processInstanceId);
    historicProcessInstanceQuery.involvedUser(userId);
    if (historicProcessInstanceQuery.count() > 0) {
      return true;
    }
    HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery();
    historicTaskInstanceQuery.processInstanceId(processInstanceId);
    historicTaskInstanceQuery.taskInvolvedUser(userId);
    if (historicTaskInstanceQuery.count() > 0) {
      return true;
    }
    return false;
  }

  /**
   * @param userId
   * @param taskId
   * @return
   */
  public HistoricTaskInstance validatePermissionOnTask(String userId, String taskId) {
    List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery().taskId(taskId).taskInvolvedUser(userId).list();
    if (CollectionUtils.isNotEmpty(tasks)) {
      return tasks.get(0);
    }
    tasks = historyService.createHistoricTaskInstanceQuery().taskId(taskId).list();
    if (CollectionUtils.isNotEmpty(tasks)) {
      HistoricTaskInstance task = tasks.get(0);
      if (task != null && task.getProcessInstanceId() != null) {
        boolean hasReadPermissionOnProcessInstance = hasPermissionOnProcessInstance(userId, task.getProcessInstanceId());
        if (hasReadPermissionOnProcessInstance) {
          return task;
        }
      }
    }
    throw new NotPermittedException("User is not allowed to work with task " + taskId);
  }




  private List<String> getGroupIdsForUser(User user) {
    List<String> groupIds = new ArrayList<String>();
    for (Group group : identityService.createGroupQuery().groupMember(user.getId()).list()) {
      groupIds.add(String.valueOf(group.getId()));
    }
    return groupIds;
  }

  public boolean isTaskOwnerOrAssignee(User user, String taskId) {
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    String currentUser = String.valueOf(user.getId());
    return currentUser.equals(task.getAssignee()) || currentUser.equals(task.getOwner());
  }

  public boolean validateIfUserIsInitiatorAndCanCompleteTask(User user, Task task) {
    boolean canCompleteTask = false;
    if (task.getProcessInstanceId() != null) {
      HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
      if (historicProcessInstance != null && StringUtils.isNotEmpty(historicProcessInstance.getStartUserId())) {
        String processInstanceStartUserId = historicProcessInstance.getStartUserId();
        if (String.valueOf(user.getId()).equals(processInstanceStartUserId)) {
          BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
          FlowElement flowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());
          if (flowElement != null && flowElement instanceof UserTask) {
            UserTask userTask = (UserTask) flowElement;
            List<ExtensionElement> extensionElements = userTask.getExtensionElements().get("initiator-can-complete");
            if (CollectionUtils.isNotEmpty(extensionElements)) {
              String value = extensionElements.get(0).getElementText();
              if (StringUtils.isNotEmpty(value) && Boolean.valueOf(value)) {
                canCompleteTask = true;
              }
            }
          }
        }
      }
    }
    return canCompleteTask;
  }

  public boolean isInvolved(User user, String taskId) {
    return historyService.createHistoricTaskInstanceQuery().taskId(taskId).taskInvolvedUser(String.valueOf(user.getId())).count() == 1;
  }
  public ProcessDefinition getProcessDefinitionById(String processDefinitionId) {
    return repositoryService.getProcessDefinition(processDefinitionId);
  }
}
