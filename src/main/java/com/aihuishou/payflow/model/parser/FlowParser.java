package com.aihuishou.payflow.model.parser;

import com.aihuishou.payflow.el.ElEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FlowParser {

    private final ElEvaluator elEvaluator;

    public <T> T parse(String exp, Class<T> clz) {
       return elEvaluator.evalWithDefaultContext(exp, clz, true);
    }
}
