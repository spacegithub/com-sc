package com.sc.ruleengine.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:qiss
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@XStreamAlias("rule-eval")
public class RuleEval{

    /**
     * 执行条件
     */
    @XStreamAsAttribute
    private String condition;

    /**
     * 执行动作
     */
    @XStreamAsAttribute
    private String action;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
