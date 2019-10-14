package cn.edu.buaa.act.mlflow.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Label implements Serializable {
    private static final long serialVersionUID = 3L;
    private String worker;
    private String item;
    private String answer;
    private String type;

    private Map<String,String> response = new HashMap<>();
    public Label(String worker, String item, String answer) {
        this.worker = worker;
        this.item = item;
        this.answer = answer;
    }
    public Label(String worker, String item, String answer, String type) {
        this.worker = worker;
        this.item = item;
        this.answer = answer;
        this.type = type;
    }

    public Label(){

    }
}