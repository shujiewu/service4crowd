package cn.edu.buaa.act.management.controller;

import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.management.common.DeploymentState;
import cn.edu.buaa.act.management.common.ServiceInstanceStatus;
import cn.edu.buaa.act.management.common.ServiceStatus;
import cn.edu.buaa.act.management.exception.NoServiceDeploymentException;
import cn.edu.buaa.act.management.exception.NoServiceInstanceException;
import cn.edu.buaa.act.management.service.ServiceDeploy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * RuntimeAppsController
 * 获取部署服务的状态
 * @author wsj
 * @date 2018/10/3
 */
@RestController
@RequestMapping("/service/runtime")
public class RuntimeInstanceController {
    private static final Logger logger = LoggerFactory.getLogger(RuntimeInstanceController.class);

    @Autowired
    private ServiceDeploy serviceDeploy;

    @RequestMapping("")
    @ResponseStatus(HttpStatus.OK)
    public TableResultResponse<ServiceStatus> list(Pageable pageable)
            throws ExecutionException, InterruptedException {
        //该接口还没有实现
        Page<ServiceStatus> statuses = serviceDeploy.getStatus(pageable);
        return new TableResultResponse<ServiceStatus>(statuses.getTotalElements(),statuses.getContent());
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ServiceStatus display(@PathVariable String id) {
        ServiceStatus status = serviceDeploy.getStatus(id);
        if (status.getState().equals(DeploymentState.unknown)) {
            logger.info("NoServiceDeploymentException");
            throw new NoServiceDeploymentException(id);
        }
        return status;
    }

    @RequestMapping("/{serviceId}/instance/{instanceId}")
    @ResponseStatus(HttpStatus.OK)
    public ServiceInstanceStatus display(@PathVariable String serviceId, @PathVariable String instanceId) {
        ServiceStatus status = serviceDeploy.getStatus(serviceId);
        if (status.getState().equals(DeploymentState.unknown)) {
            logger.info("NoServiceDeploymentException");
            throw new NoServiceDeploymentException(serviceId);
        }
        ServiceInstanceStatus appInstanceStatus = status.getInstances().get(instanceId);
        if (appInstanceStatus == null) {
            logger.info("NoServiceInstanceException");
            throw new NoServiceInstanceException(instanceId);
        }
        return appInstanceStatus;
    }

    @RequestMapping("/{serviceId}/instance")
    @ResponseStatus(HttpStatus.OK)
    public TableResultResponse<ServiceInstanceStatus> list(Pageable pageable,@PathVariable String serviceId)
            throws ExecutionException, InterruptedException {
        ServiceStatus status = serviceDeploy.getStatus(serviceId);
        if (status.getState().equals(DeploymentState.unknown)) {
            logger.info("NoServiceDeploymentException");
            throw new NoServiceDeploymentException(serviceId);
        }
        List<ServiceInstanceStatus> appInstanceStatuses = new ArrayList<>(status.getInstances().values());
        Collections.sort(appInstanceStatuses, RuntimeInstanceController.INSTANCE_SORTER);
        return new TableResultResponse<ServiceInstanceStatus>(appInstanceStatuses.size(),appInstanceStatuses);
    }

    private static final Comparator<? super ServiceInstanceStatus> INSTANCE_SORTER = new Comparator<ServiceInstanceStatus>() {
        @Override
        public int compare(ServiceInstanceStatus i1, ServiceInstanceStatus i2) {
            return i1.getId().compareTo(i2.getId());
        }
    };
}
