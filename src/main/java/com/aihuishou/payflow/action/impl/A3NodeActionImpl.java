package com.aihuishou.payflow.action.impl;

import com.aihuishou.payflow.action.NodeAction;
import com.aihuishou.payflow.model.context.NodeContext;

public class A3NodeActionImpl implements NodeAction {

    @Override
    public Object execute(final NodeContext nodeContext) {
        System.out.println("经过 a3 节点");
        return "a3";
    }
}
