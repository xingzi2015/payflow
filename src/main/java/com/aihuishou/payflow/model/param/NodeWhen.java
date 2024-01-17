package com.aihuishou.payflow.model.param;

import com.aihuishou.payflow.action.NodeConditionAction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class NodeWhen implements Serializable {

    private String createExp;
    private Boolean isNegated = Boolean.FALSE;

    // following generate by system
    @JsonIgnore
    private NodeConditionAction conditionAction;
}
