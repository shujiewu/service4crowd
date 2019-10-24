package cn.edu.buaa.act.fastwash.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Box {
    private double x;
    private double y;
    private double w;
    private double h;
    private double score;
    public Box(){}
    public Box(double x,double y,double w,double h,double score){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.score = score;
    }
}
