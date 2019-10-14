package cn.edu.buaa.act.data.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author wsj
 * 回答统计
 */
@Getter
@Setter
public class AnswerStatRepresentation implements Serializable {
    private static final long serialVersionUID = 2L;
    private int taskTotal;
    private int answerTotal;
    private int workerTotal;
    private int classTotal;
    private List<Double> workerQuality;
    private List<Integer> workerPerTask;
    private List<Integer> taskPerWorker;
    private List<Double> taskConsistency;
    private String dataName;
    private String dataType;
    private String dataId;

    private List<String> workerId;
    private List<String> taskId;
}