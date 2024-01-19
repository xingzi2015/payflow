package com.aihuishou.payflow.model.param;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Data
public class NodeCondition implements Serializable {
    //条件
    private NodeWhen[] whenNodes;
    //下个节点
    private String[] toNodes;

    // following generate by system
    @JsonIgnore
    private List<FlowNode> toFlowNodes;

    @Override
    public String toString() {
        return "NodeCondition{" +
            "nodeWhens=" + Arrays.toString(whenNodes) +
            ", toNodes=" + Arrays.toString(toNodes) +
            '}';
    }
}
