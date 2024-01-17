package com.aihuishou.payflow.action;

import com.aihuishou.payflow.model.context.NodeContext;

public interface NodeConditionAction {
    boolean evaluate(NodeContext nodeContext);

}
