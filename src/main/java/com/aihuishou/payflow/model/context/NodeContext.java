package com.aihuishou.payflow.model.context;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
public class NodeContext implements Serializable {

    private String executeId;
    private String flowExecuteId;
    private String preExecuteId;

    private NodeContext previousNode;

    private NodeContext[] nextNodes;

    private Object preResult;

    private Object actionResult;

    private Throwable throwable;

    private Map<String, Object> dataMap;

    private Integer tryTimes;

    private Integer delayLevel;

    public String toSimpleString() {
        return "{" +
            "executeId='" + executeId + '\'' +
            ", flowExecuteId='" + flowExecuteId + '\'' +
            ", preExecuteId='" + preExecuteId + '\'' +
            ", preResult=" + preResult +
            '}';
    }

    @Override
    public String toString() {
        return "NodeContext{" +
            "executeId='" + executeId + '\'' +
            ", flowExecuteId='" + flowExecuteId + '\'' +
            ", preExecuteId='" + preExecuteId + '\'' +
            ", preResult=" + preResult +
            ", actionResult=" + actionResult +
            ", throwable=" + throwable +
            ", dataMap=" + dataMap +
            ", tryTimes=" + tryTimes +
            ", delayLevel=" + delayLevel +
            '}';
    }
}
