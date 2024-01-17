package com.aihuishou.payflow.engine;

import com.aihuishou.payflow.model.param.Flow;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@ConfigurationProperties(prefix = "payflow")
@Data
public class FlowParam implements Serializable {

    private Runner runner;

    private Flow[] flows;

    @Data
    public static class Runner{
        String id;
        String createExp;
    }
}
