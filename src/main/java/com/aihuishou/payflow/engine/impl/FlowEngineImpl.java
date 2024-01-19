package com.aihuishou.payflow.engine.impl;

import com.aihuishou.payflow.action.NodeAction;
import com.aihuishou.payflow.engine.FlowEngine;
import com.aihuishou.payflow.engine.FlowManager;
import com.aihuishou.payflow.enums.StatusEnum;
import com.aihuishou.payflow.model.context.NodeContext;
import com.aihuishou.payflow.model.mq.NodeMessage;
import com.aihuishou.payflow.model.param.Flow;
import com.aihuishou.payflow.model.param.FlowNode;
import com.aihuishou.payflow.model.param.NodeCondition;
import com.aihuishou.payflow.model.parser.FlowParser;
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
import java.util.Objects;
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
    private final FlowParser flowParser;


    @Override
    public void execute(String flowId, Map<String, Object> dataMap) {

        Flow flow = FlowManager.getFlow(flowId);
        String flowExecuteId = UUID.randomUUID().toString().substring(0, 8);
        FlowNode flowNode = flow.getStartNode();
        NodeContext nodeContext = NodeContext.builder()
            .executeId(UUID.randomUUID().toString().substring(0, 8))
            .flowExecuteId(flowExecuteId)
            .dataMap(dataMap)
            .tryTimes(0)
            .delayLevel(flowNode.getDelayLevel())
            .build();

        log.info("开始执行,id=" + nodeContext.getFlowExecuteId());
        //执行FLOW前置操作
        FlowManager.getFlowHandlers()
            .forEach(flowHandler -> flowHandler.preHandle(flowExecuteId, nodeContext.getExecuteId(), flow));
        executeNextNode(List.of(Pair.of(flowNode, nodeContext)));
    }

    private void executeNode(FlowNode node, NodeContext nodeContext) {

        NodeAction nodeAction = node.getNodeAction();
        try {
            //通用节点前置处理
            FlowManager.getNodeHandlers().forEach(actionPreHandler -> actionPreHandler.preHandle(node, nodeContext));
            //单节点前置处理
            nodeAction.preHandle(nodeContext);
            //执行流程编排
            Object result = nodeAction.execute(nodeContext);
            nodeContext.setActionResult(result);

            //单节点后置处理
            nodeAction.postHandle(nodeContext);
            //通用节点后置处理
            FlowManager.getNodeHandlers()
                .forEach(actionPreHandler -> actionPreHandler.postHandle(node, nodeContext));

            //执行成功，再次初始化延迟
            nodeContext.setDelayLevel(node.getDelayLevel());

        } catch (Exception e) {
            nodeContext.setThrowable(e);
            //单节点异常处理
            nodeAction.exceptionHandle(nodeContext);
            //通用节点异常处理
            FlowManager.getNodeHandlers().forEach(actionPreHandler -> actionPreHandler.exceptionHandle(node, nodeContext));

            //进行重试
            if (nodeContext.getTryTimes() < node.getRetryTimes()) {
                //重试策略
                if (node.getRetryAlgorithm() != null) {
                    node.getRetryAlgorithm().calculateSendLevel(nodeContext);
                }
                //重试次数+1
                nodeContext.setTryTimes(nodeContext.getTryTimes() + 1);
                log.warn("重试：重试次数：" + nodeContext.getTryTimes() + " 重试延迟：" + nodeContext.getDelayLevel());
                executeNextNode(List.of(Pair.of(node, nodeContext)));
            } else {
                //TODO 数据库更新，NODE 状态=失败， FLOW 状态=失败
                FlowManager.getFlowHandlers()
                    .forEach(flowHandler -> flowHandler.exceptionHandle(nodeContext.getFlowExecuteId(), e));

                log.error("节点执行失败");
            }
            return;
        }
        if (node.getEnd()) {
            //TODO 数据库更新 FLOW 状态=成功
            FlowManager.getFlowHandlers()
                .forEach(flowHandler -> flowHandler.postHandle(nodeContext.getFlowExecuteId(), StatusEnum.SUCCESS));
            log.info("执行成功,id=" + nodeContext.getFlowExecuteId());
            return;
        }
        List<FlowNode> flowNodes = matchNextNode(node.getConditions(), nodeContext);
        if (flowNodes.isEmpty()) {
            log.warn("未满足任何条件，执行错误");
        } else {
            List<Pair<FlowNode, NodeContext>> nodePairs = flowNodes.stream()
                .map(flowNode -> Pair.of(flowNode,
                    NodeContext.builder()
                        .executeId(UUID.randomUUID().toString().substring(0, 8))
                        .preExecuteId(nodeContext.getExecuteId())
                        .flowExecuteId(nodeContext.getFlowExecuteId())
                        .previousNode(nodeContext)
                        .preResult(nodeContext.getActionResult())
                        .dataMap(nodeContext.getDataMap())
                        .tryTimes(0)
                        .delayLevel(nodeContext.getDelayLevel())
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
                    nodeMessage.setFlowId(nodePair.getLeft().getFlowId());
                    BeanUtils.copyProperties(nodePair.getRight(), nodeMessage);
                    //MQ发送
                    normalMessageProducer.sendMessage(objectMapper.writeValueAsString(nodeMessage),
                        nodePair.getRight().getDelayLevel());
                }
                case THREAD_POOL -> {
                    if (nodePair.getRight().getDelayLevel() > 0) {
                        //TODO 根据不同的 delayLevel 发送消息
                        //本地延迟执行
                        scheduledExecutorService.schedule(() -> executeNode(nodePair.getLeft(), nodePair.getRight()),
                            nodePair.getLeft().getDelayLevel(),
                            TimeUnit.SECONDS);
                    } else {
                        //本地线程池执行
                        FlowManager.getDefaultExecutor()
                            .execute(() -> executeNode(nodePair.getLeft(), nodePair.getRight()));
                    }

                }
            }
        }
    }

    private List<FlowNode> matchNextNode(NodeCondition[] nodeConditions, NodeContext nodeContext) {
        for (NodeCondition nodeCondition : nodeConditions) {
            if (nodeCondition.getWhenNodes() == null) {
                log.info("条件由于配置为空，通过！");
                return nodeCondition.getToFlowNodes();
            }
            if (matchCondition(nodeContext, nodeCondition)) {
                return nodeCondition.getToFlowNodes();
            }
        }
        return new ArrayList<>();
    }

    private boolean matchCondition(final NodeContext nodeContext, final NodeCondition nodeCondition) {
        return Arrays.stream(nodeCondition.getWhenNodes()).allMatch(
            nodeWhen -> {
                boolean conditionActionResult = Objects.isNull(nodeWhen.getConditionAction()) ||
                    nodeWhen.getConditionAction().evaluate(nodeContext);
                boolean simpleExpResult =
                    Objects.isNull(nodeWhen.getSimpleExpression()) ||
                        flowParser.parse(nodeWhen.getSimpleExpression(), nodeContext, Boolean.class);
                return nodeWhen.getIsNegated() != (conditionActionResult && simpleExpResult);
            }
        );
    }

    @SneakyThrows
    @Override
    public void handleMQMessage(String receiveMessage) {

        NodeMessage nodeMessage = objectMapper.readValue(receiveMessage, NodeMessage.class);
        NodeContext nodeContext = NodeContext.builder().build();
        BeanUtils.copyProperties(nodeMessage, nodeContext);
        executeNode(FlowManager.getFlowNode(nodeMessage.getFlowId() + "_" + nodeMessage.getId()), nodeContext);
    }


}
