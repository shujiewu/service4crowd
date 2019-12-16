package cn.edu.buaa.act.fastwash.estimate;


import cn.edu.buaa.act.fastwash.data.Annotation;
import cn.edu.buaa.act.fastwash.data.TaskItemEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaskAndGT {
    private TaskItemEntity taskItemEntity;
    private List<Annotation> GT;
    private List<List<Annotation>> result;

    private List<Annotation> inferenceResult;

    // private List<Quality> qualities;

    private double acc;
    private double rec;
    private double meanIoU;

    private int[][] multiTask;
    private int multiTaskRight;
    private int goldTaskRight;

    private double[][] great;

    private double difficult;

    private List<Double> difficultList;
}
