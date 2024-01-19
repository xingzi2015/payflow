package com.aihuishou.payflow.action.impl;

import com.aihuishou.payflow.action.NodeAction;
import com.aihuishou.payflow.model.context.NodeContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class A1NodeActionImpl implements NodeAction {
    @Override
    public void preHandle(NodeContext nodeContext){
        log.info("节点a1 前置方法");
    }
    @Override
    public Object execute(final NodeContext nodeContext) {
        log.info("经过 a1 节点，nodeContext="+nodeContext.toSimpleString());
        return nodeContext.getDataMap().get("a1");
    }
    @Override
    public void postHandle(NodeContext nodeContext){
        log.info("节点a1 后置方法,执行结果: {}",nodeContext.getActionResult());
    }
}
