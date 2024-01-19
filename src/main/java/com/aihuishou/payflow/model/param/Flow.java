package com.aihuishou.payflow.model.param;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Flow {
    private String id;
    private FlowNode[] nodes;
    // following generate by system
    @JsonIgnore
    private FlowNode startNode;
}
