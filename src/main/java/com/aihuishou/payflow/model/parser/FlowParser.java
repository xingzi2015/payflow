package com.aihuishou.payflow.model.parser;

import com.aihuishou.payflow.el.ElEvaluator;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlowParser {

    private final ElEvaluator elEvaluator;

    public <T> T parse(String exp, Class<T> clz) {
        if(Strings.isEmpty(exp)){
            return null;
        }
        return elEvaluator.evalWithDefaultContext(exp, clz, true);
    }

    public Expression parseExpression(String exp) {
        return elEvaluator.parseExpression(exp, true);
    }

    public <T> T parse(Expression exp, Object data, Class<T> clz) {

        StandardEvaluationContext context = new StandardEvaluationContext(data);

        return exp.getValue(context, clz);
    }
}
