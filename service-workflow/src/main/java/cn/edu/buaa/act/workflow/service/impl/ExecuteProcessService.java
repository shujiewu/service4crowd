package cn.edu.buaa.act.workflow.service.impl;

import cn.edu.buaa.act.common.constant.CommonConstants;
import cn.edu.buaa.act.common.constant.NotifyChannelConstants;
import cn.edu.buaa.act.common.entity.AtomicService;
import cn.edu.buaa.act.common.entity.MicroService;
import cn.edu.buaa.act.common.msg.PlayLoadMessage;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.common.util.ServiceProperty;
import cn.edu.buaa.act.workflow.channel.DataProcessorNotifyChannel;
import cn.edu.buaa.act.workflow.channel.ReceiveResultNotifyChannel;
import cn.edu.buaa.act.workflow.common.Constant;
import cn.edu.buaa.act.workflow.config.AsyncConfiguration;
import cn.edu.buaa.act.workflow.controller.StencilSetController;
import cn.edu.buaa.act.workflow.exception.NotFoundException;
import cn.edu.buaa.act.workflow.feign.IDataCoreService;
import cn.edu.buaa.act.workflow.model.ServiceOutputs;
import cn.edu.buaa.act.workflow.model.ServiceParameters;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.activiti.editor.constants.StencilConstants.PROPERTY_PROCESS_ID;

/**
 * ExecuteService
 *
 * @author wsj
 * @date 2018/10/22
 */
@Service
@Slf4j
@EnableBinding({DataProcessorNotifyChannel.class})
public class ExecuteProcessService {

    private final Logger logger = LoggerFactory.getLogger(ExecuteProcessService.class);

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected RuntimeService runtimeService;

    private Map<String, ScheduledFuture> cronTaskScheduledFutureMap = new HashMap<>();
    private PriorityBlockingQueue<Task> blockingDeque = new PriorityBlockingQueue<Task>();
    private ConcurrentHashMap<String, Task> taskMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Map> completeMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, ServiceParameters> serviceParametersMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, ServiceOutputs> serviceOutputsMap = new ConcurrentHashMap<>();

    @Autowired
    private DataProcessorNotifyChannel dataProcessorNotifyChannel;

    @Autowired
    private IDataCoreService dataCoreService;

    public Runnable createRunnable(String processInstanceId) {
        Runnable runnable = new Runnable() {
            private String getVarExpression(String test) {
                final Pattern pattern = Pattern.compile(Constant.VAR_EXPRESSION);
                Matcher matcher = pattern.matcher(test);
                while (matcher.find()) {
                    return matcher.group();
                }
                return null;
            }

            private Boolean createTask(String type, String activityId, String processInstanceId, String userId, String serviceName, String taskId) {
                Map<String, String> createTask = new HashMap<>();
                createTask.put("activityId", activityId);
                createTask.put("processInstanceId", processInstanceId);
                createTask.put("userId", userId);
                createTask.put("serviceName", serviceName);
                createTask.put("taskId", taskId);
                Map result = new HashMap();
                if (CommonConstants.HUMAN_TASK.equals(type)) {
                    result = dataCoreService.createHumanTask(createTask).getBody();
                } else {
                    result = dataCoreService.createMachineTask(createTask).getBody();
                }
                return (Boolean) result.get("success");
            }

            private Boolean completeTask(String type, String status, String serviceResultId, String taskId) {
                Map<String, String> completeTask = new HashMap<>();
                completeTask.put("status", status);
                completeTask.put("serviceResultId", serviceResultId);
                completeTask.put("taskId", taskId);
                Map result = new HashMap();
                if (CommonConstants.HUMAN_TASK.equals(type)) {
                    result = dataCoreService.completeCrowdTask(completeTask).getBody();
                } else {
                    result = dataCoreService.completeMachineTask(completeTask).getBody();
                }
                return (Boolean) result.get("success");
            }

            @Override
            public void run() {
                //logger.info(processInstanceId);
                //看队列中是否完成
                //执行任务
                List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
                tasks.stream().forEach(task -> {
                    String process_id = (String) taskService.getVariable(task.getId(), PROPERTY_PROCESS_ID);
                    //logger.info(process_id + task.getTaskDefinitionKey());
                    if (taskMap.containsKey(task.getId())) {
                        if (completeMap.containsKey(task.getId())) {
                            Map result = completeMap.get(task.getId());
                            ServiceOutputs serviceOutputs = serviceOutputsMap.get(process_id + task.getTaskDefinitionKey());
                            MicroService microService = Constant.microServiceMap.get(serviceOutputs.getMicroServiceName());
                            AtomicService atomicService = microService.getAtomicServiceList().parallelStream().filter(u -> u.getServiceName().equals(serviceOutputs.getSelectAtomicName())).findFirst().orElseGet(null);
                            if (atomicService == null) {
                                throw new NotFoundException();
                            }
                            // System.out.println(result.keySet());
                            String serviceResultId = result.get("serviceResultId").toString();
                            try {
                                List<ServiceProperty> serviceProperties = serviceOutputs.getAsyncResponseMap().get(result.get("status").toString()).getData();
                                if (serviceResultId != null) {
                                    Map<String, Object> serviceResult = dataCoreService.getServiceResult(serviceResultId).getBody();
                                    serviceProperties.forEach(serviceProperty -> {
                                        if (Constant.PARA_SCOPE_PROCESS.equals(serviceProperty.getScope())) {
                                            System.out.println("设置流程变量"+serviceProperty.getName());
                                            if (serviceProperty.getType().equals("object") || serviceProperty.getType().equals("object[]") || serviceProperty.getType().equals("string[]")) {
                                                runtimeService.setVariable(task.getExecutionId(), StringUtils.hasText(serviceProperty.getRename()) ? serviceProperty.getRename() : serviceProperty.getName(), JSON.toJSON(serviceResult.get(serviceProperty.getName())));
                                            } else {
                                                runtimeService.setVariable(task.getExecutionId(), StringUtils.hasText(serviceProperty.getRename()) ? serviceProperty.getRename() : serviceProperty.getName(), serviceResult.get(serviceProperty.getName()));
                                            }
                                        } else if (Constant.PARA_SCOPE_EXECUTION.equals(serviceProperty.getScope())) {
                                            System.out.println("设置分支变量"+serviceProperty.getName());
                                            if (serviceProperty.getType().equals("object") || serviceProperty.getType().equals("object[]") || serviceProperty.getType().equals("string[]")) {
                                                runtimeService.setVariableLocal(task.getExecutionId(), StringUtils.hasText(serviceProperty.getRename()) ? serviceProperty.getRename() : serviceProperty.getName(), JSON.toJSON(serviceResult.get(serviceProperty.getName())));
                                            } else {
                                                runtimeService.setVariableLocal(task.getExecutionId(), StringUtils.hasText(serviceProperty.getRename()) ? serviceProperty.getRename() : serviceProperty.getName(), serviceResult.get(serviceProperty.getName()));
                                            }
                                            //runtimeService.setVariableLocal(task.getExecutionId(), StringUtils.hasText(serviceProperty.getRename())?serviceProperty.getRename():serviceProperty.getName(),serviceResult.get(serviceProperty.getName()).toString());
                                        }
                                    });
                                }
                                taskService.complete(task.getId());
                                taskMap.remove(task.getId());
                                completeTask(microService.getServiceType(), "complete", serviceResultId, task.getId());
                            } catch (Exception e) {
                                taskMap.remove(task.getId());
                                completeTask(microService.getServiceType(), "error", serviceResultId, task.getId());
                                e.printStackTrace();
                            }
                        }
                    } else {
                        taskMap.put(task.getId(), task);

                        ServiceParameters serviceParameters = serviceParametersMap.get(process_id + task.getTaskDefinitionKey());
//                        System.out.println(serviceParametersMap.values());
//                        log.info(serviceParameters.getMicroServiceName());
//                        log.info(serviceParameters.getSelectAtomicName());
                        MicroService microService = Constant.microServiceMap.get(serviceParameters.getMicroServiceName());
                        AtomicService atomicService = microService.getAtomicServiceList().parallelStream().filter(u -> u.getServiceName().equals(serviceParameters.getSelectAtomicName())).findFirst().orElseGet(null);
                        if (atomicService == null) {
                            throw new NotFoundException();
                        }
                        String activityId = task.getTaskDefinitionKey();
                        String processInstanceId = task.getProcessInstanceId();
                        String userId = runtimeService.getVariable(task.getExecutionId(), "userId").toString();
                        if (!"MQ".equals(atomicService.getMethod())) {
                            String url = "http://" + microService.getServiceName() + atomicService.getUrl();
                            url = url + "?userId=" + runtimeService.getVariable(task.getExecutionId(), "userId");
                            if (atomicService.getQueryParameters() != null && atomicService.getQueryParameters().size() > 0) {
                                for (ServiceProperty serviceProperty : atomicService.getQueryParameters()) {
                                    url = url + "&&" + serviceProperty.getName() + "=" + "{" + Constant.PARA_TYPE_QUERY + "." + serviceProperty.getName() + "}";
                                }
                                //url = url.substring(0, url.length() - 2);
                            }
                            Map<String, Object> uriVariables = new HashMap<String, Object>();
                            serviceParameters.getInputPara().stream().filter(para -> para.getParaType().equals(Constant.PARA_TYPE_QUERY))
                                    .forEach(para -> {
                                        if (para.getValue() != null && !para.getValue().toString().isEmpty()) {
                                            String expression = getVarExpression(para.getValue().toString());
                                            if (expression != null) {
                                                uriVariables.put(Constant.PARA_TYPE_QUERY + "." + para.getName(), runtimeService.getVariable(task.getExecutionId(), expression));
                                            } else {
                                                uriVariables.put(Constant.PARA_TYPE_QUERY + "." + para.getName(), para.getValue());
                                            }
                                        } else {
                                            Object var = runtimeService.getVariable(task.getExecutionId(), para.getName());
                                            if (var == null) {
                                                uriVariables.put(Constant.PARA_TYPE_QUERY + "." + para.getName(), para.getDefaultValue());
                                            } else {
                                                uriVariables.put(Constant.PARA_TYPE_QUERY + "." + para.getName(), var);
                                            }
                                        }
                                        // uriVariables.put(Constant.PARA_TYPE_QUERY + "." + para.getName(), para.getValue() == null ? para.getDefaultValue() : para.getValue());
                                    });
                            serviceParameters.getInputPara().stream().filter(para -> para.getParaType().equals(Constant.PARA_TYPE_URI))
                                    .forEach(para -> {
                                        if (para.getValue() != null && !para.getValue().toString().isEmpty()) {
                                            String expression = getVarExpression(para.getValue().toString());
                                            if (expression != null) {
                                                uriVariables.put(para.getName(), runtimeService.getVariable(task.getExecutionId(), expression));
                                            } else {
                                                uriVariables.put(para.getName(), para.getValue());
                                            }
                                        } else {
                                            Object var = runtimeService.getVariable(task.getExecutionId(), para.getName());
                                            if (var == null) {
                                                uriVariables.put(para.getName(), para.getDefaultValue());
                                            } else {
                                                uriVariables.put(para.getName(), var);
                                            }
                                        }
                                    });

                            if ("GET".equals(atomicService.getMethod())) {
                                try {
                                    //System.out.println(url);
                                    //System.out.println(uriVariables);
                                    createTask(microService.getServiceType(), task.getTaskDefinitionKey(), processInstanceId, userId, task.getName(), task.getId());
                                    ResponseEntity<JSONObject> responseEntity = restTemplate.getForEntity(url, JSONObject.class, uriVariables);

                                    ServiceOutputs serviceOutputs = serviceOutputsMap.get(process_id + task.getTaskDefinitionKey());
                                    if (!atomicService.getAsync()) {
                                        int status = responseEntity.getStatusCodeValue();
                                        List<ServiceProperty> serviceProperties = serviceOutputs.getSyncResponseMap().get(String.valueOf(status)).getData();
                                        serviceProperties.forEach(serviceProperty -> {
                                            if (Constant.PARA_SCOPE_PROCESS.equals(serviceProperty.getScope())) {
                                                runtimeService.setVariable(task.getExecutionId(), StringUtils.hasText(serviceProperty.getRename()) ? serviceProperty.getRename() : serviceProperty.getName(), responseEntity.getBody().get(serviceProperty.getName()));
                                            } else if (Constant.PARA_SCOPE_EXECUTION.equals(serviceProperty.getScope())) {
                                                runtimeService.setVariableLocal(task.getExecutionId(), StringUtils.hasText(serviceProperty.getRename()) ? serviceProperty.getRename() : serviceProperty.getName(), responseEntity.getBody().get(serviceProperty.getName()));
                                            }
                                        });
                                        taskService.complete(task.getId());
                                        taskMap.remove(task.getId());
                                        completeTask(microService.getServiceType(), "complete", (String) responseEntity.getBody().get("serviceResultId"), task.getId());
                                    }
                                } catch (Exception e) {
                                    completeTask(microService.getServiceType(), "error", null, task.getId());
                                    cronTaskScheduledFutureMap.get(processInstanceId).cancel(true);
                                    e.printStackTrace();
                                }
                            } else if ("POST".equals(atomicService.getMethod())) {
                                try {
                                    createTask(microService.getServiceType(), task.getTaskDefinitionKey(), processInstanceId, userId, task.getName(), task.getId());
                                    MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<String, Object>();
                                    serviceParameters.getInputPara().stream().filter(para -> para.getParaType().equals(Constant.PARA_TYPE_BODY))
                                            .forEach(para -> {
                                                if (para.getValue() != null && !para.getValue().toString().isEmpty()) {
                                                    String expression = getVarExpression(para.getValue().toString());
                                                    if (expression != null) {
                                                        Object var = runtimeService.getVariable(task.getExecutionId(), expression);
                                                        paramMap.add(para.getName(), para.getType().equals("object") || para.getType().equals("object[]") || para.getType().equals("string[]") ? JSONObject.toJSONString(var) : var);
                                                        //paramMap.add(para.getName(), runtimeService.getVariable(task.getExecutionId(), expression));
// para.getType().equals("object")||para.getType().equals("object[]")?JSONObject.toJSONString(runtimeService.getVariable(task.getExecutionId(),expression)):
                                                    } else {
                                                        paramMap.add(para.getName(), para.getValue());
                                                    }
                                                } else {
                                                    Object var = runtimeService.getVariable(task.getExecutionId(), para.getName());
                                                    if (var == null) {
                                                        paramMap.add(para.getName(), para.getDefaultValue());
                                                    } else {
                                                        //paramMap.add(para.getName(), var);
                                                        //list 需要转换为json
                                                        paramMap.add(para.getName(), para.getType().equals("object") || para.getType().equals("object[]") || para.getType().equals("string[]") ? JSONObject.toJSONString(var) : var);
                                                    }
                                                }
                                            });


                                    // paramMap.remove("UnitDataList");
                                    // paramMap.remove("UnitDataListId");
                                    paramMap.add("taskId", task.getId());
                                    paramMap.add("serviceName", task.getName());
                                    paramMap.add("processInstanceId", processInstanceId);



                                    // System.out.println(url);

                                    if (paramMap.containsKey("state")) {

                                        File rootPath = new File("D://servicedata/temp/" + task.getId() + "/");
                                        if (!rootPath.exists()) {
                                            rootPath.mkdirs();
                                        }
                                        Object truthData = paramMap.get("state").get(0);
                                        try {
                                            File file = new File(rootPath.getAbsolutePath() + "/state.txt");
                                            // if file doesnt exists, then create it
                                            if (!file.exists()) {
                                                file.createNewFile();
                                            }
                                            FileWriter fw = new FileWriter(file.getAbsoluteFile());
                                            BufferedWriter bw = new BufferedWriter(fw);
                                            // System.out.println((JSONObject.parseObject(truthData)).getJSONArray("Specificity_r"));
                                            JSONObject state = JSON.parseObject(truthData.toString());
                                            JSONArray spr = state.getJSONArray("Specificity_r");
                                            for(int i = 0;i<spr.size();i++){
                                                spr.set(i,Double.parseDouble(spr.getString(i)));
                                            }
                                            state.put("Specificity_r",spr);

                                            List<JSONArray> confidence = state.getJSONArray("confidence").toJavaList(JSONArray.class);
                                            List<List<Double>> reconfidence = new ArrayList<>();
                                            for(int i = 0;i<confidence.size();i++){
                                                List<Double>  item = confidence.get(i).toJavaList(Double.class);
                                                reconfidence.add(item);
                                            }
                                            state.put("confidence",reconfidence);

                                            //spcefic r为什么成了字符串
                                            bw.write(state.toString());
                                            bw.close();
                                            paramMap.remove("state");
                                            paramMap.add("state", file.getAbsolutePath());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    else {
                                        System.out.println(paramMap);
                                    }

                                    HttpHeaders headers = new HttpHeaders();
                                    headers.add("Content-Type", "application/json; charset=utf-8");
                                    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(paramMap, new HttpHeaders());
                                    //ResponseEntity<TableResultResponse> responseEntity2 = restTemplate.getForEntity("http://service-figureEight/figure-eight/jobs?APIKey={queryParameters.APIKey}", TableResultResponse.class,uriVariables);
                                    ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, request, Map.class, uriVariables);
                                    // System.out.println(responseEntity.getStatusCodeValue());
                                    // System.out.println(responseEntity.getBody());

                                    ServiceOutputs serviceOutputs = serviceOutputsMap.get(process_id + task.getTaskDefinitionKey());
                                    if (!atomicService.getAsync()) {
                                        int status = responseEntity.getStatusCodeValue();
                                        List<ServiceProperty> serviceProperties = serviceOutputs.getSyncResponseMap().get(String.valueOf(status)).getData();
                                        serviceProperties.forEach(serviceProperty -> {
                                            if (Constant.PARA_SCOPE_PROCESS.equals(serviceProperty.getScope())) {
                                                runtimeService.setVariable(task.getExecutionId(), StringUtils.hasText(serviceProperty.getRename()) ? serviceProperty.getRename() : serviceProperty.getName(), responseEntity.getBody().get(serviceProperty.getName()));
                                            } else if (Constant.PARA_SCOPE_EXECUTION.equals(serviceProperty.getScope())) {
                                                runtimeService.setVariableLocal(task.getExecutionId(), StringUtils.hasText(serviceProperty.getRename()) ? serviceProperty.getRename() : serviceProperty.getName(), responseEntity.getBody().get(serviceProperty.getName()));
                                            }
                                        });
                                        taskService.complete(task.getId());
                                        taskMap.remove(task.getId());
                                        completeTask(microService.getServiceType(), "complete", (String) responseEntity.getBody().get("serviceResultId"), task.getId());
                                    }

                                } catch (Exception e) {
                                    completeTask(microService.getServiceType(), "error", null, task.getId());
                                    cronTaskScheduledFutureMap.get(processInstanceId).cancel(true);
                                    e.printStackTrace();
                                }
                            }
                        }
                        if ("MQ".equals(atomicService.getMethod())) {
                            //分支覆盖流程
                            PlayLoadMessage<JSONObject> playLoadMessage = new PlayLoadMessage<>();
                            playLoadMessage.setServiceName(task.getName());
                            playLoadMessage.setTaskId(task.getId());
                            playLoadMessage.setProcessInstanceId(processInstanceId);
                            playLoadMessage.setUserId(userId);
                            playLoadMessage.setActivityId(activityId);
                            playLoadMessage.setComplete(false);
                            JSONObject message = new JSONObject();
                            serviceParameters.getInputPara().stream().filter(para -> para.getParaType().equals(Constant.PARA_TYPE_MQ_BODY))
                                    .forEach(para -> {
                                        if (para.getValue() != null && !para.getValue().toString().isEmpty()) {
                                            String expression = getVarExpression(para.getValue().toString());
                                            if (expression != null) {
                                                message.put(para.getName(), runtimeService.getVariable(task.getExecutionId(), expression));
                                            } else {
                                                message.put(para.getName(), para.getValue());
                                            }
                                        } else {
                                            Object var = runtimeService.getVariable(task.getExecutionId(), para.getName());
                                            if (var == null) {
                                                message.put(para.getName(), para.getDefaultValue());
                                            } else {
                                                message.put(para.getName(), var);
                                            }
                                        }
                                    });
                            playLoadMessage.setMessage(message);
                            // System.out.println(JSONObject.toJSON(playLoadMessage));
                            // JSONObject jsonObject = (JSONObject) JSONObject.toJSON(playLoadMessage);
                            //log.info(playLoadMessage.getMessage().toJSONString());
                            createTask(microService.getServiceType(), task.getTaskDefinitionKey(), processInstanceId, userId, task.getName(), task.getId());
                            dataProcessorNotifyChannel.output().send(MessageBuilder.withPayload(JSONObject.toJSON(playLoadMessage)).build());
                        }
                    }
                });
            }
        };
        return runnable;
    }

    public ScheduledFuture<?> threadScheduler(String processInstanceId, Runnable runnable, String cron) {
        /*动态创建定时任务*/
        ScheduledFuture future = threadPoolTaskScheduler.schedule(runnable, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                return new CronTrigger(cron).nextExecutionTime(triggerContext);
            }
        });
        cronTaskScheduledFutureMap.put(processInstanceId, future);
        return future;
    }

    @Scheduled(cron = "0/30 * * * * *")
    //注解定义调度任务的方法，这个方法会在spring的ioc中被封装成一个Runnable对象，如果定义了cron表达式则会进入到CronTask的Runnable中，后面会对调度流程进一步分析
    public void destroy() {
        cronTaskScheduledFutureMap.forEach((processInstancId, future) -> {
            if (runtimeService.createProcessInstanceQuery().processInstanceId(processInstancId).list().isEmpty()) {
                future.cancel(true);
                logger.info("destroy execute");
                cronTaskScheduledFutureMap.remove(processInstancId);
            }
        });
    }

    //@Async("threadPoolTaskScheduler")
//    @Scheduled(cron = "0/3 * * * * *") //注解定义调度任务的方法，这个方法会在spring的ioc中被封装成一个Runnable对象，如果定义了cron表达式则会进入到CronTask的Runnable中，后面会对调度流程进一步分析
//    public void one() {
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        logger.info("one execute");
//    }
}
