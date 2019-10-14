package cn.edu.buaa.act.common.util;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * ServiceResponse
 *
 * @author wsj
 * @date 2018/10/20
 */
@Getter
@Setter
public class ServiceResponse {
    private int status;
    private String description;
    private List<ServiceProperty> body;
}
