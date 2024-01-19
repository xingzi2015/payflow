package com.aihuishou.payflow.action;

import com.aihuishou.payflow.model.context.NodeContext;

public interface NodeAction {

    default void preHandle(NodeContext nodeContext) {}

    Object execute(NodeContext nodeContext);

    default void postHandle(NodeContext nodeContext) {}

    default void exceptionHandle(NodeContext nodeContext) {}
}
