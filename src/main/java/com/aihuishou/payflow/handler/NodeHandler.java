package com.aihuishou.payflow.handler;

import com.aihuishou.payflow.model.context.NodeContext;
import com.aihuishou.payflow.model.param.FlowNode;

public interface NodeHandler {
    void preHandle(FlowNode node, NodeContext nodeContext);
    void postHandle(FlowNode node, NodeContext nodeContext);
    void exceptionHandle(FlowNode node, NodeContext nodeContext);
}
