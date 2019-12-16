package cn.edu.buaa.act.fastwash.entity;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RepublishRequest {
    private List<String> crowdTaskIdList;
}
