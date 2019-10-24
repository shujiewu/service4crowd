package cn.edu.buaa.act.fastwash.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Classification{
    private String id;
    private String value;
    private String fillColor;
    private String strokeColor;
    public Classification(){}
    public Classification(String id,String value){
        this.id = id;
        this.value = value;
    }
}