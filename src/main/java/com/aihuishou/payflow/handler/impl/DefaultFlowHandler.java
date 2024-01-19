package com.aihuishou.payflow.handler.impl;

import com.aihuishou.payflow.enums.StatusEnum;
import com.aihuishou.payflow.handler.FlowHandler;
import com.aihuishou.payflow.model.param.Flow;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultFlowHandler implements FlowHandler {

    @Override
    public void preHandle(final String flowExecuteId, final String startNodeExecuteId, final Flow flow) {
        log.info("flow 通用前置方法");
    }

    @Override
    public void postHandle(final String flowExecuteId, final StatusEnum statusEnum) {
        log.info("flow 通用后置方法");
    }

    @Override
    public void exceptionHandle(final String flowExecuteId, final Exception e) {

    }
}
