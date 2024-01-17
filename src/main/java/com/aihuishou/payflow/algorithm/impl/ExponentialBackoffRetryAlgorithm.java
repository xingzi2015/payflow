package com.aihuishou.payflow.algorithm.impl;

import com.aihuishou.payflow.algorithm.RetryAlgorithm;
import com.aihuishou.payflow.model.context.NodeContext;
import lombok.RequiredArgsConstructor;

/**
 * 指数退避算法
 */
@RequiredArgsConstructor
public class ExponentialBackoffRetryAlgorithm implements RetryAlgorithm {

    private final int maxDelay;

    @Override
    public void calculateSendLevel(final NodeContext nodeContext) {
        nodeContext.setDelayLevel(Math.min(nodeContext.getTryTimes(), maxDelay));
    }
}
