package com.sc.ruleengine.core;

import com.sc.ruleengine.config.RuleSet;

import java.util.List;

/**
 * 规则引擎,管理一个或一组规则和执行器
 */
public interface RuleEngine {
    /**
     * 对指定上下文执行指定类型的规则
     *
     * @param context     规则引擎上下文
     * @param ruleSetName 指定类型规则
     */
    void execute(RuleContext context, String ruleSetName);

    /**
     * 添加一组规则
     */
    void addRules(RuleSet ruleSet);

    /**
     * 删除一组规则
     */
    void removeRules(RuleSet ruleSet);

    /**
     * 添加规则执行器列表
     */
    void addRuleExecutors(List<RuleExecutor> ruleExecutorList);

    /**
     * 设置一组执行器,如果存在则替换执行器实例
     */
    void setRuleExecutors(List<RuleExecutor> ruleExecutors);


    /**
     * 删除一组执行器
     * @param ruleExecutors
     */
    void removeRuleExecutors(List<RuleExecutor> ruleExecutors);


}
