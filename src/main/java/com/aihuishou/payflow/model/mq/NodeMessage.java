package com.aihuishou.payflow.model.mq;

import com.aihuishou.payflow.model.param.NodeCondition;
import lombok.Data;

import java.util.Map;

@Data
public class NodeMessage {
    private String id;

    private String executeId;
    private Object preResult;
    private Map<String, Object> dataMap;
    private Integer tryTimes;
    private String flowName;
}
