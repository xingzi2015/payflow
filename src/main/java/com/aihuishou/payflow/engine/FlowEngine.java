package com.aihuishou.payflow.engine;

import java.util.Map;

/**
 *  流程引擎
 */
public interface FlowEngine {

    /**
     * 执行流程
     * @return
     */
    void execute(String flowName, Map<String, Object> dataMap);

    void handleMQMessage(String receiveMessage);

}
