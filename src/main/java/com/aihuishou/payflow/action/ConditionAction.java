package com.aihuishou.payflow.action;

import com.aihuishou.payflow.model.context.NodeContext;

public interface ConditionAction {
    boolean evaluate(NodeContext nodeContext);

}
