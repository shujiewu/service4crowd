package cn.edu.buaa.act.workflow.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


public class RestVariable {

    public enum RestVariableScope {
        LOCAL, GLOBAL
    };

    private String name;
    private String type;
    private RestVariableScope variableScope;
    private Object value;
    private String valueUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonInclude(Include.NON_NULL)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    public RestVariableScope getVariableScope() {
        return variableScope;
    }

    public void setVariableScope(RestVariableScope variableScope) {
        this.variableScope = variableScope;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getScope() {
        String scope = null;
        if (variableScope != null) {
            scope = variableScope.name().toLowerCase();
        }
        return scope;
    }

    public void setScope(String scope) {
        setVariableScope(getScopeFromString(scope));
    }

    public void setValueUrl(String valueUrl) {
        this.valueUrl = valueUrl;
    }

    @JsonInclude(Include.NON_NULL)
    public String getValueUrl() {
        return valueUrl;
    }

    public static RestVariableScope getScopeFromString(String scope) {
        if (scope != null) {
            for (RestVariableScope s : RestVariableScope.values()) {
                if (s.name().equalsIgnoreCase(scope)) {
                    return s;
                }
            }
            return null;
        } else {
            return null;
        }
    }
}
