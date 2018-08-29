package com.sc.ruleengine.config;

import com.sun.istack.internal.NotNull;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("rule")
public class Rule {
    /**
     * 规则Id
     */
    @NotNull
    @XStreamAsAttribute
    private String id;
    /**
     * 规则优先级1~10级
     */
    @XStreamAsAttribute
    private int priority=10;

    /**
     * 规则表述
     */
    @XStreamAsAttribute
    private String description;

    /**
     * 规则是否有效
     */
    @XStreamAsAttribute
    private boolean vaild=true;
    /**
     * 规则类型
     */
    @NotNull
    @XStreamAsAttribute
    private String type;
    /**
     * 是否支持多次执行
     */
    @XStreamAsAttribute
    private boolean multipleTimes=false;

    /**
     * 规则是否互斥的
     */
    @XStreamAsAttribute
    private boolean exclusive=false;

    /**
     * 规则执行条件和执行动作
     */
    @XStreamAlias("rule-eval")
    private RuleEval ruleEval;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isMultipleTimes() {
        return multipleTimes;
    }

    public void setMultipleTimes(boolean multipleTimes) {
        this.multipleTimes = multipleTimes;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVaild() {
        return vaild;
    }

    public void setVaild(boolean vaild) {
        this.vaild = vaild;
    }

    public RuleEval getRuleEval() {
        return ruleEval;
    }

    public void setRuleEval(RuleEval ruleEval) {
        this.ruleEval = ruleEval;
    }
}
