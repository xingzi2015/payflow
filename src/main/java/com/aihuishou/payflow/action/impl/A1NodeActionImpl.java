package com.aihuishou.payflow.action.impl;

import com.aihuishou.payflow.action.NodeAction;
import com.aihuishou.payflow.model.context.NodeContext;

public class A1NodeActionImpl implements NodeAction {

    @Override
    public Object execute(final NodeContext nodeContext) {
        System.out.println("经过 a1 节点");
        return nodeContext.getDataMap().get("a1");
    }
}
