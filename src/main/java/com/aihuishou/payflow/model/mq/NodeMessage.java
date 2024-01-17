package com.aihuishou.payflow.model.mq;

import lombok.Data;

import java.util.Map;

@Data
public class NodeMessage {
    private String id;

    private String executeId;
    private String flowExecuteId;
    private String preExecuteId;

    private Object preResult;
    private Map<String, Object> dataMap;
    private Integer tryTimes;
    private String flowName;
}
