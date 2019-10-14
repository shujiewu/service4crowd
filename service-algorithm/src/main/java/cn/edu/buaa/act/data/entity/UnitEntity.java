package cn.edu.buaa.act.data.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UnitEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String unitId;
    private String state;
    private String id;
    private Object data;
}
