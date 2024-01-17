package com.aihuishou.payflow.action;

import com.aihuishou.payflow.model.context.NodeContext;
import org.w3c.dom.Node;

public interface NodeAction {
    /**
     * Execute node action.
     * @param nodeContext
     * @return
     */
    Object execute(NodeContext nodeContext);
}
