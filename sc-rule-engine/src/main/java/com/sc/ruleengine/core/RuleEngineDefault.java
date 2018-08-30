package com.sc.ruleengine.core;


import com.sc.ruleengine.config.Rule;
import com.sc.ruleengine.config.RuleSet;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;


public class RuleEngineDefault implements RuleEngine {
    private Map<String, List<Rule>> ruleSetMap = new ConcurrentHashMap<String, List<Rule>>();
    private List<RuleExecutor> ruleExecutorList = new LinkedList<>();
    private Map<String, RuleExecutor> ruleExecutorMap = new ConcurrentHashMap<String, RuleExecutor>();

    @Override
    public void execute(RuleContext context, String ruleSetName) {
        List<Rule> rules = ruleSetMap.get(ruleSetName);
        if (rules != null) {
            Vector<Rule> newSet = new Vector<Rule>(rules);
            processRuleSet(context, newSet);
        }
    }

    private void processRuleSet(RuleContext context, Vector<Rule> rules) {
        //如果没有后续规则，则退出
        if (rules.size() == 0) {
            return;
        }
        Rule rule = rules.get(0);
        RuleExecutor ruleExecutor = ruleExecutorMap.get(rule.getType());
        if (ruleExecutor != null) {
            boolean executed = ruleExecutor.execute(context, rule.getRuleEval());
            if (executed) {
                //条件成立,且是独占条件直接返回
                if (rule.isExclusive()) {
                    return;
                }
                if (!rule.isMultipleTimes()) {
                    //如果不是可重复执行的规则,则删除之注意:如果是非独占的且是可重复执行的需要在执行器里面有返回false的终止,不然会一直执行下去的
                    rules.remove(0);
                }
            } else {
                //如果不匹配,则删除之
                rules.remove(0);
            }
        } else {
            throw new RuntimeException("找不到对应" + rule.getType() + "的执行器");
        }
        processRuleSet(context, rules);
    }

    @Override
    public void addRules(RuleSet ruleSet) {
        List<Rule> rules = ruleSetMap.get(ruleSet.getName());
        if (rules == null) {
            ruleSetMap.put(ruleSet.getName(), ruleSet.getRules());
        }

        Collections.sort(rules, new Comparator<Rule>() {
            @Override
            public int compare(Rule o1, Rule o2) {
                return o1.getPriority() > o2.getPriority() ? 1 : o1.getPriority() == o2.getPriority() ? 0 : -1;
            }
        });
    }

    @Override
    public void removeRules(RuleSet ruleSet) {
        List<Rule> rules = ruleSetMap.get(ruleSet.getName());
        if (rules != null) {
            rules.removeAll(ruleSet.getRules());
        }
    }

    @Override
    public void addRuleExecutors(List<RuleExecutor> ruleExecutors) {
        if (ruleExecutors != null) {
            Iterator<RuleExecutor> it = ruleExecutors.iterator();
            while (it.hasNext()) {
                RuleExecutor temp = it.next();
                ruleExecutorList.add(temp);
                ruleExecutorMap.put(temp.getType(), temp);
            }
        }
    }


    @Override
    public void setRuleExecutors(List<RuleExecutor> ruleExecutors) {
        this.ruleExecutorList = ruleExecutors;
        for (RuleExecutor ruleExecutor : ruleExecutors) {
            ruleExecutorMap.put(ruleExecutor.getType(), ruleExecutor);
        }
    }

    @Override
    public void removeRuleExecutors(List<RuleExecutor> ruleExecutors) {
        if (ruleExecutors != null) {
            Iterator<RuleExecutor> it = ruleExecutors.iterator();
            while (it.hasNext()) {
                RuleExecutor temp = it.next();
                ruleExecutorList.remove(temp);
                ruleExecutorMap.remove(temp.getType());
            }
        }
    }
}
