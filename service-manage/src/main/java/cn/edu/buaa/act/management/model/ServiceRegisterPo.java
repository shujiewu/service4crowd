package cn.edu.buaa.act.management.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

/**
 * ServiceRegisterPo
 *
 * @author wsj
 * @date 2018/10/11
 */
@Getter
@Setter
public class ServiceRegisterPo {
    private String uri;
    private Boolean force;
    private String metadataUri;
    private String version;
    private String description;
    private String propertyId;
}
