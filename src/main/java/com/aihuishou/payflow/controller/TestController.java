package com.aihuishou.payflow.controller;

import com.aihuishou.payflow.engine.FlowEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {
    @Autowired
    private FlowEngine flowEngine;

    @GetMapping("/taskA")
    public String runTaskA(){
        Map<String, Object> dataMap =new HashMap<>();
        dataMap.put("a1","a1 to a3");
        flowEngine.execute("flow1",dataMap);
        return "ok";
    }

    @GetMapping("/taskB")
    public String runTaskB(){
        Map<String, Object> dataMap =new HashMap<>();
        dataMap.put("a1","a1 to a2");
        flowEngine.execute("flow1",dataMap);
        return "ok";
    }

}
