package com.aihuishou.payflow.action.impl;

import com.aihuishou.payflow.action.ConditionAction;
import com.aihuishou.payflow.model.context.NodeContext;

public class ToA6ConditionAction implements ConditionAction {

    @Override
    public boolean evaluate(final NodeContext nodeContext) {
        if (nodeContext.getActionResult() instanceof Integer) {
            return (Integer) nodeContext.getActionResult() <= 3;
        }
        return false;
    }
}
