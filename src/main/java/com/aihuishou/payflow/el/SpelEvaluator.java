package com.aihuishou.payflow.el;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SpelEvaluator implements ElEvaluator {

    private static final StandardEvaluationContext context = new StandardEvaluationContext();
    private static final ExpressionParser parser = new SpelExpressionParser();
    private static final Map<String, Expression> cacheMap = new ConcurrentHashMap<>();

    @Override
    public <T> T evalWithDefaultContext(final String exp, final Object root, final boolean cache) {
        Object value = parseExpression(exp, cache).getValue(context, root);
        return (T) value;
    }

    @Override
    public Expression parseExpression(final String exp, final boolean cache) {
        Expression expression;
        if (cache) {
            expression = cacheMap.get(exp);
            if (expression == null) {
                expression = parser.parseExpression(exp);
                cacheMap.put(exp, expression);
            }
        } else {
            expression = parser.parseExpression(exp);
        }
        return expression;
    }

}
