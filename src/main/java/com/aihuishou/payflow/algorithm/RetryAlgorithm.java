package com.aihuishou.payflow.algorithm;

import com.aihuishou.payflow.model.context.NodeContext;

public interface RetryAlgorithm {
    void calculateSendLevel(NodeContext nodeContext);
}
