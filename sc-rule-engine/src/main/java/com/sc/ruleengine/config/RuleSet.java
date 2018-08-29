package com.sc.ruleengine.config;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * 规则集对象
 */
@XStreamAlias("rule-set")
public class RuleSet<Rule> {

    /**
     * 规则集名称
     */
    @XStreamAsAttribute
    private String name;

    /**
     * 规则列表
     */
    @XStreamAlias("rule")
    @XStreamImplicit
    private List<Rule> rules;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Rule> getRules() {
        if (rules == null) {
            rules = new ArrayList<Rule>();
        }
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }
}
