package cn.edu.buaa.act.figureeight.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResultRepresentation {
    private int unitTotal;
    private int judgementTotal;

    private List<String> unitId = new ArrayList<>();
    private List<Integer> judgementCount = new ArrayList<>();

    private List<Double> agreement =new ArrayList<>();

    private List<Double> interval = new ArrayList<>();

    private List<List<Double>> workerTrust = new ArrayList<>();
}
