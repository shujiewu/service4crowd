package cn.edu.buaa.act.common.entity;

import cn.edu.buaa.act.common.util.ServiceProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Program
 *
 * @author wsj
 * @date 2018/10/29
 */
@Getter
@Setter
public class Algorithm {
    private String id;
    private String userId;
    private String permission;
    private Date createTime;
    private Boolean async;
    private String name;
    private String version;
    private String description;
    private List<ServiceProperty> inputParameters;
    private List<ServiceProperty> outputParameters;
    private Map<String,String> aggregationProperty;
    private String implementation;
    private List<String> lib;
}
