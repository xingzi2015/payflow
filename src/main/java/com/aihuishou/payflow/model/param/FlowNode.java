package com.aihuishou.payflow.model.param;

import com.aihuishou.payflow.action.NodeAction;
import com.aihuishou.payflow.enums.ExecuteTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class FlowNode implements Serializable {

    private String id;
    private Boolean start = Boolean.FALSE;
    private Boolean end = Boolean.FALSE;

    private String createExp;
    private NodeCondition[] conditions;
    private String runnerId;
    //默认两次重试
    private Integer retryTimes=2;
    //发送延迟，默认无延迟
    private Integer delayLevel = 0;
    //默认为指数退避
    private String retryAlgorithmExp;
    //节点间的执行方式
    private ExecuteTypeEnum executeType = ExecuteTypeEnum.MQ;
    // following generate by system
    @JsonIgnore
    private NodeAction nodeAction;
}
