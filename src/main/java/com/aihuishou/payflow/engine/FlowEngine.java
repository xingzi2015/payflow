package com.aihuishou.payflow.engine;

import java.util.Map;

/**
 *  流程引擎
 */
public interface FlowEngine {

    void execute(String flowId, Map<String, Object> dataMap);

    void handleMQMessage(String receiveMessage);

}
