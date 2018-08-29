package com.sc.ruleengine;

import com.sc.ruleengine.config.RuleEval;
import com.sc.ruleengine.core.RuleContext;
import com.sc.ruleengine.core.RuleExecutor;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * <一句话功能简述>
 * <功能详细描述>
 * example 个人所得税计算规则执行器
 */
public class FeeRuleExecutor implements RuleExecutor<RuleEval> {

    private final String FEE_RULE_EXECUTOR = "FEE_RULE";

    /**
     * 返回引擎执行器类型
     */
    @Override
    public String getType() {
        return FEE_RULE_EXECUTOR;
    }

    /**
     * 执行器具体的执行,并把执行结果放到上下文
     *
     * @param context 规则引擎上下文
     * @param rule    规则
     * @return 返回条件是否成立
     */
    @Override
    public boolean execute(RuleContext context, RuleEval rule) {
        if (context.get("salary") == null) {
            return false;
        }
        try {
            ScriptEngine se = new ScriptEngineManager().getEngineByName("js");
            Compilable ce = (Compilable) se;
            CompiledScript cs = ce.compile(rule.getCondition());
            Bindings bindings = se.createBindings();
            bindings.put("salary", context.get("salary"));
            boolean condition = (boolean) cs.eval(bindings);
            if (condition) {
                cs = ce.compile(rule.getAction());
                bindings.put("fee", context.get("fee"));
                Double totalSalary = (Double) cs.eval(bindings);
                context.put("totalSalary",totalSalary);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
