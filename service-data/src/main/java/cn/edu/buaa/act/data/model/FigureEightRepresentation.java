package cn.edu.buaa.act.data.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wsj
 * Figure-Eight平台结果展示
 */
@Getter
@Setter
public class FigureEightRepresentation {
    private int unitTotal;
    private int judgementTotal;

    private List<String> unitId = new ArrayList<>();
    private List<Integer> judgementCount = new ArrayList<>();

    private List<Double> agreement =new ArrayList<>();

    private List<Double> interval = new ArrayList<>();

    private List<List<Double>> workerTrust = new ArrayList<>();
}
