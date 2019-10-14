package cn.edu.buaa.act.workflow.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wsj
 */
@Getter
@Setter
public class CreateProcessInstanceRepresentation {
    private String processDefinitionId;
    private String name;
    public CreateProcessInstanceRepresentation(){
    }
    public CreateProcessInstanceRepresentation(String processDefinitionId,String name) {
        this.processDefinitionId=processDefinitionId;
        this.name=name;
    }
}
