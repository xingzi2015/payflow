package com.aihuishou.payflow.model.context;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
public class NodeContext implements Serializable {
    private String executeId;

    private NodeContext previousNode;

    private NodeContext[] nextNodes;

    private Object preResult;

    private Object actionResult;

    private Throwable throwable;

    private Map<String, Object> dataMap;

    private Integer tryTimes;

    private String flowName;
}
