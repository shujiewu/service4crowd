package cn.edu.buaa.act.workflow.model;

import cn.edu.buaa.act.common.util.ServiceProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * ServiceParameters
 *
 * @author wsj
 * @date 2018/10/22
 */
@Getter
@Setter
public class ServiceParameters {
    private String microServiceName;
    private String selectAtomicName;
    private List<ServiceProperty> inputPara;
    @Override
    public String toString() {
        return "SelectAtomicName:"+selectAtomicName+" "+"size:"+inputPara.size();
    }
}
