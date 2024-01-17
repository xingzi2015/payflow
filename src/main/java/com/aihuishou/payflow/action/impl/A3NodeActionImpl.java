package com.aihuishou.payflow.action.impl;

import com.aihuishou.payflow.action.NodeAction;
import com.aihuishou.payflow.model.context.NodeContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class A3NodeActionImpl implements NodeAction {

    @Override
    public Object execute(final NodeContext nodeContext) {
        log.info("经过 a3 节点");
        return "a3";
    }
}
