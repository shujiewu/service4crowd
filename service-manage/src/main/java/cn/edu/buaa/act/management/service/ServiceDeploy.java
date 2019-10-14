package cn.edu.buaa.act.management.service;

import cn.edu.buaa.act.management.common.ServiceStatus;
import cn.edu.buaa.act.management.model.DeploymentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author wsj
 */
public interface ServiceDeploy {


    String PREFIX = "service.deployer.";
    String COUNT_PROPERTY_KEY = "service.deployer.count";
    String GROUP_PROPERTY_KEY = "service.deployer.group";

    String MEMORY_PROPERTY_KEY = "service.deployer.memory";
    String DISK_PROPERTY_KEY = "service.deployer.disk";



    /**
     * @param request
     * @return
     */
    public String deploy(DeploymentRequest request);

    /**
     * @param id
     * @return
     */
    public Boolean unDeploy(String id);

    /**
     * @param id
     * @return
     */
    public ServiceStatus getStatus(String id);

    /**
     * @param pageable
     * @return
     */
    public Page<ServiceStatus> getStatus(Pageable pageable);
}
