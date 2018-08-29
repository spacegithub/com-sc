package com.sc.ruleengine.core;


import com.sc.ruleengine.config.RuleEval;

/**
 * 规则引擎执行器接口
 */

public interface RuleExecutor<T extends RuleEval> {
    /**
     * 返回引擎执行器类型
     */
    String getType();

    /**
     * 执行器具体的执行,并把执行结果放到上下文
     * @param context 规则引擎上下文
     * @param rule    规则
     * @return 返回条件是否成立
     */
    boolean execute(RuleContext context, T rule);
}
