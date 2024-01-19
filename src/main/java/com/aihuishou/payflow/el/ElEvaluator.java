package com.aihuishou.payflow.el;

import org.springframework.expression.Expression;

public interface ElEvaluator {
    <T> T evalWithDefaultContext(String exp, Object root, boolean cache);

    Expression parseExpression(String exp, boolean cache);
}
