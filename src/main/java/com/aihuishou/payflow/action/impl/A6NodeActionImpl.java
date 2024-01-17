package com.aihuishou.payflow.action.impl;

import com.aihuishou.payflow.action.NodeAction;
import com.aihuishou.payflow.model.context.NodeContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
public class A6NodeActionImpl implements NodeAction {

    @Override
    public Object execute(final NodeContext nodeContext) {
        final Map<String, Object> dataMap = nodeContext.getDataMap();
        log.info("经过 a6 节点，nodeContext="+nodeContext.toSimpleString()+" 计数器结果："+  dataMap.get("a6"));
        if(!dataMap.containsKey("a6")){
            dataMap.put("a6","1");
            throw new IllegalStateException();
        }
        if (Integer.parseInt((String) dataMap.get("a6"))<=3) {
            dataMap.put("a6",String.valueOf(Integer.parseInt(
                (String) dataMap.get("a6"))+1));
            throw new IllegalStateException();
        }
        return "a6";
    }
}
