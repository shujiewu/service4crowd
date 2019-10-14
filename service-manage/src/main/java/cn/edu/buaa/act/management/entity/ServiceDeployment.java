package cn.edu.buaa.act.management.entity;

import cn.edu.buaa.act.management.common.DeploymentState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ServiceDeployment
 *
 * @author wsj
 * @date 2018/10/5
 */
@Getter
@Setter
@Entity
@Table(name = "service_deployment")
public class ServiceDeployment {
    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //这个是hibernate的注解/生成32位UUID
    @GeneratedValue(generator="idGenerator")
    private String id;

    private String name;

    private String definitionId;
    private String definitionName;
    private String taskId;
    private String appId;
    private Date deployTime;
    private Date unDeployTime;
    @Lob
    private Map<String, String> properties;
    private DeploymentState status;

    public ServiceDeployment(){

    }
    public ServiceDeployment(String deploymentId,String definitionName, Map<String, String> properties) {
        this.definitionId = deploymentId;
        this.definitionName = definitionName;
        this.properties = properties == null ? Collections.emptyMap() : Collections.unmodifiableMap(new HashMap(properties));
    }

}
