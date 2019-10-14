package cn.edu.buaa.act.workflow.model;

import cn.edu.buaa.act.common.util.ServiceProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * ServiceOuputsInfo
 *
 * @author wsj
 * @date 2018/10/22
 */
@Getter
@Setter
public class ServiceOuputsInfo {
    private String description;
    private List<ServiceProperty> data;
}
