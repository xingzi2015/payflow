package com.aihuishou.payflow.action.impl;

import com.aihuishou.payflow.action.NodeAction;
import com.aihuishou.payflow.model.context.NodeContext;

public class A2NodeActionImpl implements NodeAction {

    @Override
    public Object execute(final NodeContext nodeContext) {
        System.out.println("经过 a2 节点" + nodeContext.getPreResult());
        if (nodeContext.getPreResult() instanceof Integer) {
            return (Integer) nodeContext.getPreResult() + 1;
        } else {
            return 0;
        }
    }
}
