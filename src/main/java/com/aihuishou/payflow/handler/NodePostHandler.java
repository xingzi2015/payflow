package com.aihuishou.payflow.handler;

import com.aihuishou.payflow.model.context.NodeContext;
import com.aihuishou.payflow.model.param.FlowNode;

public interface NodePostHandler {
    void postHandle(FlowNode node, NodeContext nodeContext);
}
