package com.aihuishou.payflow.action.impl;

import com.aihuishou.payflow.action.NodeAction;
import com.aihuishou.payflow.model.context.NodeContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class A5NodeActionImpl implements NodeAction {

    @Override
    public Object execute(final NodeContext nodeContext) {
        log.info("经过 a5 节点，nodeContext="+nodeContext.toSimpleString());
        return "a5";
    }
}
