package cn.edu.buaa.act.fastwash.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Quality {
    private double acc;
    private double rec;
    private double fscore;
    private double meanIoU;
    private double quality;
    public Quality(){}
    public Quality(double acc,double rec,double meanIoU){
        this.acc = acc;
        this.rec = rec;
        this.fscore = 2.0/((1.0/acc)+(1.0/(rec)));
        this.meanIoU = meanIoU;
        this.quality = (this.fscore+this.meanIoU)/2.0;
    }
}
