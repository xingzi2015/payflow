package com.aihuishou.payflow.engine.impl;

import com.aihuishou.payflow.action.NodeAction;
import com.aihuishou.payflow.engine.FlowContext;
import com.aihuishou.payflow.engine.FlowEngine;
import com.aihuishou.payflow.engine.FlowResult;
import com.aihuishou.payflow.model.context.NodeContext;
import com.aihuishou.payflow.model.mq.NodeMessage;
import com.aihuishou.payflow.model.param.Flow;
import com.aihuishou.payflow.model.param.FlowNode;
import com.aihuishou.payflow.model.param.NodeCondition;
import com.aihuishou.payflow.mq.NormalMessageProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class FlowEngineImpl implements FlowEngine {

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(
        Runtime.getRuntime().availableProcessors() * 2);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final NormalMessageProducer normalMessageProducer;


    @Override
    public FlowResult execute(String flowName, Map<String, Object> dataMap) {

        Flow flow = FlowContext.getFlow(flowName);
        FlowNode flowNode = flow.getStartNode();
        NodeContext nodeContext = NodeContext.builder()
            .executeId(UUID.randomUUID().toString())
            .dataMap(dataMap)
            .tryTimes(0)
            .build();
        //TODO 数据库插入 FLOW
        executeNextNode(List.of(Pair.of(flowNode, nodeContext)));
        return null;
    }

    private void executeNode(FlowNode node, NodeContext nodeContext) {

        NodeAction nodeAction = node.getNodeAction();
        try {
            //TODO 幂等逻辑，前置AOP 操作，数据库插入 NODE
            Object result = nodeAction.execute(nodeContext);
            //TODO 后置 AOP 操作,数据库更新 NODE 状态=成功
            nodeContext.setActionResult(result);
        } catch (Exception e) {
            nodeContext.setThrowable(e);
            if (nodeContext.getTryTimes() < node.getRetryTimes()) {
                nodeContext.setTryTimes(nodeContext.getTryTimes() + 1);
                executeNode(node, nodeContext);
            } else {
                //TODO 数据库更新，NODE 状态=失败， FLOW 状态=失败
                //记数据库日志
            }
        }
        if (node.getEnd()) {
            //TODO 数据库更新 FLOW 状态=成功
            return;
        }
        List<FlowNode> flowNodes = matchNextNode(node.getConditions(), nodeContext);
        if (flowNodes.isEmpty()) {
            log.warn("未满足任何条件，执行错误");
        } else {

            List<Pair<FlowNode, NodeContext>> nodePairs = flowNodes.stream()
                .map(flowNode -> Pair.of(flowNode,
                    NodeContext.builder()
                        .executeId(UUID.randomUUID().toString())
                        .previousNode(nodeContext)
                        .preResult(nodeContext.getActionResult())
                        .dataMap(nodeContext.getDataMap())
                        .tryTimes(0)
                        .build())).collect(Collectors.toList());

            nodeContext.setNextNodes(nodePairs.stream().map(Pair::getRight).toArray(NodeContext[]::new));
            executeNextNode(nodePairs);
        }
    }


    @SneakyThrows
    private void executeNextNode(final List<Pair<FlowNode, NodeContext>> nodePairs) {
        for (Pair<FlowNode, NodeContext> nodePair : nodePairs) {
            switch (nodePair.getLeft().getExecuteType()) {
                case MQ -> {

                    //生成 NodeMessage
                    NodeMessage nodeMessage = new NodeMessage();
                    nodeMessage.setId(nodePair.getLeft().getId());
                    nodeMessage.setFlowName(nodePair.getLeft().getFlowName());
                    BeanUtils.copyProperties(nodePair.getRight(), nodeMessage);
                    //MQ发送
                    normalMessageProducer.sendMessage(objectMapper.writeValueAsString(nodeMessage),
                        nodePair.getLeft().getDelayLevel());
                }
                case THREAD_POOL -> {
                    if (nodePair.getLeft().getDelayLevel() > 0) {
                        //TODO 根据不同的 delayLevel 发送消息
                        //本地延迟执行
                        scheduledExecutorService.schedule(() -> executeNode(nodePair.getLeft(), nodePair.getRight()),
                            nodePair.getLeft().getDelayLevel(),
                            TimeUnit.SECONDS);
                    } else {
                        //本地线程池执行
                        FlowContext.getDefaultExecutor()
                            .execute(() -> executeNode(nodePair.getLeft(), nodePair.getRight()));
                    }

                }
            }
        }
    }

    private List<FlowNode> matchNextNode(NodeCondition[] nodeConditions, NodeContext nodeContext) {
        for (NodeCondition nodeCondition : nodeConditions) {
            if (nodeCondition.getNodeWhens() == null) {
                log.info("条件由于配置为空，通过！");
                return nodeCondition.getToFlowNodes();
            }
            if (Arrays.stream(nodeCondition.getNodeWhens()).allMatch(
                nodeWhen -> {
                    boolean result = nodeWhen.getConditionAction().evaluate(nodeContext);
                    return nodeWhen.getIsNegated() != result;
                }
            )) {
                return nodeCondition.getToFlowNodes();
            }
        }
        return new ArrayList<>();
    }

    @SneakyThrows
    @Override
    public void handleMQMessage(String receiveMessage) {

        NodeMessage nodeMessage = objectMapper.readValue(receiveMessage, NodeMessage.class);
        NodeContext nodeContext = NodeContext.builder().build();
        BeanUtils.copyProperties(nodeMessage, nodeContext);
        executeNode(FlowContext.getFlowNode(nodeMessage.getFlowName() + "_" + nodeMessage.getId()), nodeContext);
    }


}
