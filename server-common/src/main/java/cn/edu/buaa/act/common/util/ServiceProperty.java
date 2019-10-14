package cn.edu.buaa.act.common.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceProperty {
    private String name;
    private Object value;
    private Object defaultValue;
    private String type;
    private String description;
    private String scope;
    private String paraType;
    private String rename;

    public ServiceProperty() {
    }

    public ServiceProperty(String name, String value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public ServiceProperty(String name, String value, String type, String description) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.description = description;
    }

    @Override
    public String toString() {
        return name+":"+value;
    }
}
