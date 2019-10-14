package cn.edu.buaa.act.management.model;

import cn.edu.buaa.act.management.entity.ServiceDefinition;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

import java.util.Collections;
import java.util.List;
import java.util.Map;


@Setter
@Getter
public class DeploymentRequest {
    private final ServiceDefinition definition;
    private final Resource resource;
    private final Map<String, String> deploymentProperties;
    private final List<String> commandlineArguments;

    public DeploymentRequest(ServiceDefinition definition, Resource resource, Map<String, String> deploymentProperties) {
        this(definition, resource, deploymentProperties, (List)null);
    }

    public DeploymentRequest(ServiceDefinition definition, Resource resource, Map<String, String> deploymentProperties, List<String> commandlineArguments) {
        this.definition = definition;
        this.resource = resource;
        this.deploymentProperties = deploymentProperties == null ? Collections.emptyMap() : Collections.unmodifiableMap(deploymentProperties);
        this.commandlineArguments = commandlineArguments == null ? Collections.emptyList() : Collections.unmodifiableList(commandlineArguments);
    }

    public DeploymentRequest(ServiceDefinition definition, Resource resource) {
        this(definition, resource, (Map)null);
    }
}