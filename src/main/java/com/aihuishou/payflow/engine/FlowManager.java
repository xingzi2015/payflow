package com.aihuishou.payflow.engine;

import com.aihuishou.payflow.action.ConditionAction;
import com.aihuishou.payflow.action.NodeAction;
import com.aihuishou.payflow.algorithm.RetryAlgorithm;
import com.aihuishou.payflow.engine.FlowParam.Runner;
import com.aihuishou.payflow.handler.FlowHandler;
import com.aihuishou.payflow.handler.NodeHandler;
import com.aihuishou.payflow.model.param.Flow;
import com.aihuishou.payflow.model.param.FlowNode;
import com.aihuishou.payflow.model.param.NodeCondition;
import com.aihuishou.payflow.model.parser.FlowParser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FlowManager {

    private final FlowParam flowParam;
    private final FlowParser flowParser;
    private final ApplicationContext applicationContext;

    @Getter
    private static Executor defaultExecutor;
    private static Map<String, Flow> flowMap;
    private static Map<String, FlowNode> flowNodeMap;
    @Getter
    private static List<NodeHandler> nodeHandlers = new ArrayList<>();
    @Getter
    private static List<FlowHandler> flowHandlers = new ArrayList<>();

    /**
     * 加载 FLow
     */
    @PostConstruct
    public void init() {
        initRunner();
        initFlows();
        initHandler();
    }

    private void initRunner() {
        Optional<Runner> runnerOptional = Optional.of(flowParam).map(FlowParam::getRunner);
        //初始化线程池
        runnerOptional.ifPresent(runner -> defaultExecutor = flowParser.parse(runner.getCreateExp(), Executor.class));
    }

    private void initFlows() {
        flowMap = new HashMap<>();
        flowNodeMap = new HashMap<>();
        for (Flow flow : flowParam.getFlows()) {
            final String flowId = flow.getId();
            if (flowMap.containsKey(flowId)) {
                throw new IllegalStateException("配置了重复的流程，重复的流程：" + flowId);
            }
            flowMap.put(flowId, flow);
            //nodeMap 用于存储NodeId 和 node 的映射关系
            Map<String, FlowNode> nodeMap = new HashMap<>();
            Set<NodeCondition> nodeConditions = new HashSet<>();
            for (FlowNode flowNode : flow.getNodes()) {
                if (Boolean.TRUE == flowNode.getStart()) {
                    //赋值流程的开始节点
                    flow.setStartNode(flowNode);
                }
                //需要判断 node Ide不能重复
                nodeMap.put(flowNode.getId(), flowNode);
                if (flowNodeMap.containsKey(flowId + "_" + flowNode.getId())) {
                    throw new IllegalStateException(
                        String.format("当前流程存在重复的节点，流程名称：%s,节点名称:%s", flowId, flowNode.getId()));
                }
                flowNodeMap.put(flowId + "_" + flowNode.getId(), flowNode);
                //赋值每个流程需要做的事
                flowNode.setNodeAction(flowParser.parse(flowNode.getCreateExp(), NodeAction.class));
                if (StringUtils.isNotEmpty(flowNode.getRetryAlgorithmExp())) {
                    flowNode.setRetryAlgorithm(flowParser.parse(flowNode.getRetryAlgorithmExp(), RetryAlgorithm.class));
                }
                flowNode.setFlowId(flowId);
                if (flowNode.getConditions() != null) {
                    for (NodeCondition nodeCondition : flowNode.getConditions()) {
                        Optional.of(nodeCondition).map(NodeCondition::getWhenNodes)
                            .ifPresent(nodeWhens -> Arrays.stream(nodeWhens).forEach(
                                //赋值流程运行的条件
                                nodeWhen -> {
                                    if (StringUtils.isNotEmpty(nodeWhen.getCreateExp())) {
                                        nodeWhen.setConditionAction(
                                            flowParser.parse(nodeWhen.getCreateExp(), ConditionAction.class));
                                    }
                                    if (StringUtils.isNotEmpty(nodeWhen.getSimpleExp())) {
                                        nodeWhen.setSimpleExpression(
                                            flowParser.parseExpression(nodeWhen.getSimpleExp()));
                                    }
                                }
                            ));
                        nodeConditions.add(nodeCondition);
                    }
                }
            }
            for (NodeCondition nodeCondition : nodeConditions) {
                List<FlowNode> flowNodes = new ArrayList<>();
                for (String toNode : nodeCondition.getToNodes()) {
                    flowNodes.add(nodeMap.get(toNode));
                }
                nodeCondition.setToFlowNodes(flowNodes);
            }

        }
    }


    private void initHandler() {

        if (Objects.nonNull(flowParam.getNodeHandlers())) {
            nodeHandlers = Arrays.stream(flowParam.getNodeHandlers())
                .map(handler -> flowParser.parse(handler.getCreateExp(), NodeHandler.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        }
        if (Objects.nonNull(flowParam.getNodeHandlers())) {
            flowHandlers = Arrays.stream(flowParam.getFlowHandlers())
                .map(handler -> flowParser.parse(handler.getCreateExp(), FlowHandler.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        }
    }

    public static Flow getFlow(String flowId) {
        return flowMap.get(flowId);
    }

    public static FlowNode getFlowNode(String key) {
        return flowNodeMap.get(key);
    }
}
