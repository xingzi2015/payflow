package com.aihuishou.payflow.action;

import com.aihuishou.payflow.model.context.NodeContext;

public interface NodeAction {

    Object execute(NodeContext nodeContext);
}
