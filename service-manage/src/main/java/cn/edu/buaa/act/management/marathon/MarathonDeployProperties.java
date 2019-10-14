package cn.edu.buaa.act.management.marathon;

import cn.edu.buaa.act.management.common.Constraint;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DeployProperties
 *
 * @author wsj
 * @date 2018/10/2
 */

@Getter
@Setter
@ConfigurationProperties(MarathonDeployProperties.PREFIX)
public class MarathonDeployProperties {
    final static String PREFIX = "deploy.mesos.marathon";
    private String apiEndpoint = "http://10.1.1.63:8080";
    private String imagePullSecret;
    private double memory = 512.0D;
    private double cpu = 0.5D;
    private String[] environmentVariables = new String[]{};

    private Set<Constraint> constraints = new HashSet<>();
    private List<String> uris = new ArrayList<>(0);
}
