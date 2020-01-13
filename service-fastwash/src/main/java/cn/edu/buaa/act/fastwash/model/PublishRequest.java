package cn.edu.buaa.act.fastwash.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PublishRequest {
    private String strategy;
    private int total;
    private List<String> imageIdList;
    //是否模型推断
    private boolean inference;
    private int maxWorkerPerTask;
    private int maxIterationsPerTask;
}
