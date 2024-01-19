package com.aihuishou.payflow.model.param;

import com.aihuishou.payflow.action.ConditionAction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.expression.Expression;

import java.io.Serializable;

@Data
public class NodeWhen implements Serializable {

    private String createExp;
    private String simpleExp;
    private Boolean isNegated = Boolean.FALSE;

    // following generate by system
    @JsonIgnore
    private ConditionAction conditionAction;

    @JsonIgnore
    private Expression simpleExpression;

    @Override
    public String toString() {
        return "NodeWhen{" +
            "createExp='" + createExp + '\'' +
            ", isNegated=" + isNegated +
            '}';
    }
}
