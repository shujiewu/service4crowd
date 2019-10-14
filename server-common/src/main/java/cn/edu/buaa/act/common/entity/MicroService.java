package cn.edu.buaa.act.common.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * MicroService
 *
 * @author wsj
 * @date 2018/10/20
 */
@Getter
@Setter
public class MicroService {
    private String id;
    private String userId;
    private String permission;
    private Date createTime;

    private String registerId;
    private String serviceName;
    private String description;
    private List<AtomicService> atomicServiceList;

    private String serviceType;
}
