package com.aihuishou.payflow.action;

import com.aihuishou.payflow.model.context.NodeContext;
import org.w3c.dom.Node;

public interface NodeAction {

    Object execute(NodeContext nodeContext);
}
