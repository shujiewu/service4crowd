package cn.edu.buaa.act.fastwash.entity;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DecomposeRequest {

    private Boolean simulate;
    private String dataSetName;
    private List<String> imageIdList;

}
