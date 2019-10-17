package cn.edu.buaa.act.model.detection.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Box {
    private double x;
    private double y;
    private double w;
    private double h;
}
