package com.aihuishou.payflow.handler;

import com.aihuishou.payflow.enums.StatusEnum;
import com.aihuishou.payflow.model.context.NodeContext;
import com.aihuishou.payflow.model.param.Flow;

public interface FlowHandler {

    void preHandle(String flowExecuteId, String startNodeExecuteId, Flow flow);

    void postHandle(String flowExecuteId, StatusEnum statusEnum);

    void exceptionHandle(String flowExecuteId, Exception e);

}
