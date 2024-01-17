package com.aihuishou.payflow.action.impl;

import com.aihuishou.payflow.action.NodeConditionAction;
import com.aihuishou.payflow.model.context.NodeContext;

public class ToA3ConditionAction implements NodeConditionAction {

    @Override
    public boolean evaluate(final NodeContext nodeContext) {
        return nodeContext.getActionResult().equals("a1 to a3");
    }
}
