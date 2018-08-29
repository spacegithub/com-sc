package com.sc.ruleengine.core;


import com.sc.ruleengine.config.Rule;
import com.sc.ruleengine.config.RuleSet;

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
        List<Rule> ruleSet = ruleSetMap.get(ruleSetName);
        if (ruleSet != null) {
            Vector<Rule> newSet = new Vector<Rule>(ruleSet);
            processRuleSet(context, newSet);
        }
    }

    private void processRuleSet(RuleContext context, Vector<Rule> newSet) {
        //如果没有后续规则，则退出
        if (newSet.size() == 0) {
            return;
        }
        Rule rule = newSet.get(0);
        RuleExecutor ruleExecutor = ruleExecutorMap.get(rule.getType());
        if (ruleExecutor != null) {
            boolean executed = ruleExecutor.execute(context, rule.getRuleEval());
            if (executed) {
                //如果
                if (rule.isExclusive()) {
                    //如果条件成立，则是独占条件，则直接返回
                    return;
                } else if (!rule.isMultipleTimes()) {
                    //如果不是可重复执行的规则，则删除之
                    newSet.remove(0);
                }
            } else {
                //如果不匹配，则删除之
                newSet.remove(0);
            }
        } else {
            throw new RuntimeException("找不到对应" + rule.getType() + "的执行器");
        }
        processRuleSet(context, newSet);
    }

    @Override
    public void addRules(RuleSet ruleSet) {
        List<Rule> rules = ruleSetMap.get(ruleSet.getName());
        if (rules == null) {
            ruleSetMap.put(ruleSet.getName(), ruleSet.getRules());
        }

//        Collections.sort(rules);
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
