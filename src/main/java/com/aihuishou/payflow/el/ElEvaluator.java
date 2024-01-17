package com.aihuishou.payflow.el;

public interface ElEvaluator {
    <T> T evalWithDefaultContext(String exp, Object root, boolean cache);
}
