package cn.edu.buaa.act.fastwash.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Improve {

    private String workerId;
    private String lastWorkerId;
    private Quality lastQuality;
    private Quality currentQuality;
    private double difficult;
}
