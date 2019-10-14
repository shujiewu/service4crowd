package cn.edu.buaa.act.data.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * ServiceResultEntity
 * 服务结果
 * @author wsj
 * @date 2018/6/21
 */

@Getter
@Setter
public class ServiceResultEntity {
    private String id;
    private String userId;
    private String userName;
    private String serviceName;
    private Map<String,Object> result;
}
