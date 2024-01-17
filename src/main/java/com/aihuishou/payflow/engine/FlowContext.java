package com.aihuishou.payflow.engine;

import com.aihuishou.payflow.action.NodeAction;
import com.aihuishou.payflow.action.NodeConditionAction;
import com.aihuishou.payflow.engine.FlowParam.Runner;
import com.aihuishou.payflow.model.param.Flow;
import com.aihuishou.payflow.model.param.FlowNode;
import com.aihuishou.payflow.model.param.NodeCondition;
import com.aihuishou.payflow.model.parser.FlowParser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
public class FlowContext {

    private final FlowParam flowParam;
    private final FlowParser flowParser;

    @Getter
    private static Executor defaultExecutor;
    private static Map<String, Flow> flowMap;
    private static Map<String, FlowNode> flowNodeMap;

    /**
     * 加载 FLow
     */
    @PostConstruct
    public void init() {
        initRunner();
        initFlows();
    }


    private void initRunner() {
        Optional<Runner> runnerOptional = Optional.of(flowParam).map(FlowParam::getRunner);
        //初始化线程池
        runnerOptional.ifPresent(runner -> defaultExecutor = flowParser.parse(runner.getCreateExp(), Executor.class));
    }

    private void initFlows() {
        flowMap = new HashMap<>();
        flowNodeMap =new HashMap<>();
        for (Flow flow : flowParam.getFlows()) {
            //nodeMap 用于存储NodeId 和 node 的映射关系
            Map<String, FlowNode> nodeMap = new HashMap<>();
            Set<NodeCondition> nodeConditions = new HashSet<>();
            for (FlowNode flowNode : flow.getNodes()) {
                if (Boolean.TRUE == flowNode.getStart()) {
                    //赋值流程的开始节点
                    flow.setStartNode(flowNode);
                }
                nodeMap.put(flowNode.getId(), flowNode);
                flowNodeMap.put(flow.getName() + "_" + flowNode.getId(), flowNode);
                //赋值每个流程需要做的事
                flowNode.setNodeAction(flowParser.parse(flowNode.getCreateExp(), NodeAction.class));
                flowNode.setFlowName(flow.getName());
                if (flowNode.getConditions() != null) {
                    for (NodeCondition nodeCondition : flowNode.getConditions()) {
                        Optional.of(nodeCondition).map(NodeCondition::getNodeWhens)
                            .ifPresent(nodeWhens -> Arrays.stream(nodeWhens).forEach(
                                //赋值流程运行的条件
                                nodeWhen -> nodeWhen.setConditionAction(
                                    flowParser.parse(nodeWhen.getCreateExp(), NodeConditionAction.class))
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
            flowMap.put(flow.getName(), flow);
        }
    }


    public static Flow getFlow(String flowName) {
        return flowMap.get(flowName);
    }

    public static FlowNode getFlowNode(String key) {
        return flowNodeMap.get(key);
    }
}
