package cn.edu.buaa.act.workflow.model;

import cn.edu.buaa.act.common.util.ServiceProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * ServiceOutputs
 *
 * @author wsj
 * @date 2018/10/22
 */
@Getter
@Setter
public class ServiceOutputs {
    private String microServiceName;
    private String selectAtomicName;
    private Map<String,ServiceOuputsInfo> syncResponseMap;
    private Map<String,ServiceOuputsInfo> asyncResponseMap;
    @Override
    public String toString() {
        return "SelectAtomicName:"+selectAtomicName+" "+"size:"+syncResponseMap.size();
    }
}
