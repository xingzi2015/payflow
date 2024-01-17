package com.aihuishou.payflow.model.param;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Map;

@Data
public class Flow {
    private String name;
    private FlowNode[] nodes;
    // following generate by system
    @JsonIgnore
    private FlowNode startNode;
}
