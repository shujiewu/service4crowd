package cn.edu.buaa.act.figureeight.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class WorkerRepresentation {
    private Integer workerNumber;
    private List<Double> workerTrust;
    private List<String> workerId;
    private List<Integer> completeTaskNumer;

    private Map<String,Map<String,Integer>> country;
    private Map<String,Integer> countryStat;

    private List<Long> avgCompleteTime;

}
