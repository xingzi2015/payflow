package com.aihuishou.payflow.handler.impl;

import com.aihuishou.payflow.handler.NodeHandler;
import com.aihuishou.payflow.model.context.NodeContext;
import com.aihuishou.payflow.model.param.FlowNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultNodeHandler implements NodeHandler {

    @Override
    public void preHandle(final FlowNode node, final NodeContext nodeContext) {
        log.info("node 通用前置方法");
    }

    @Override
    public void postHandle(final FlowNode node, final NodeContext nodeContext) {
        log.info("node 通用后置方法");
    }

    @Override
    public void exceptionHandle(final FlowNode node, final NodeContext nodeContext) {
        log.info("node 通用异常处理");
    }
}
