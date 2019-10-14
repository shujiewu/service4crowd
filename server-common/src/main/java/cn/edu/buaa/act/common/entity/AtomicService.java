package cn.edu.buaa.act.common.entity;

import cn.edu.buaa.act.common.util.ServiceProperty;
import cn.edu.buaa.act.common.util.ServiceResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

/**
 * ServiceEntity
 *
 * @author wsj
 * @date 2018/10/20
 */
@Getter
@Setter
public class AtomicService {
    private String id;
    private String userId;
    private String permission;
    private Date createTime;


    private String serviceName;
    private String description;
    private String method;

    private String url;
    private List<ServiceProperty> queryParameters;
    private List<ServiceProperty> uriParameters;
    private List<ServiceProperty> body;

    private String channel;
    private List<ServiceProperty> messageBody;

    private List<ServiceResponse> serviceResponses;

    private Boolean async;
    private String asyncResultUrl;
    private List<ServiceResponse> asyncServiceResponses;
}
