package com.sc.ruleengine.test;

import com.sc.ruleengine.FeeRuleExecutor;
import com.sc.ruleengine.config.Rule;
import com.sc.ruleengine.config.RuleSet;
import com.sc.ruleengine.core.RuleContext;
import com.sc.ruleengine.core.RuleContextDefault;
import com.sc.ruleengine.core.RuleEngine;
import com.sc.ruleengine.core.RuleEngineDefault;
import com.sc.ruleengine.core.RuleExecutor;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;


public class RuleEngineDefaultTest {
    @Test
    public void execute() throws Exception {
        String feerulexml = Class.class.getClass().getResource("/").getPath() + "feerule.xml";

       /* //创建解析XML对象
        XStream xStream = new XStream();
        //设置别名, 默认会输出全路径
        xStream.alias("rule-set", RuleSet.class);
        xStream.alias("rule", Rule.class);
        xStream.alias("rule-eval", RuleEval.class);

        RuleSet ruleSet=new RuleSet();
        ruleSet.setName("test");

        List<Rule> list=new ArrayList<>();
        Rule rule=new Rule();
        rule.setId("1");
        rule.setPriority(10);
        RuleEval ruleEval=new RuleEval();
        ruleEval.setAction("asdfsdf");
        ruleEval.setCondition("eeeee");
        rule.setRuleEval(ruleEval);
        list.add(rule);

        rule=new Rule();
        rule.setId("1");
        rule.setPriority(10);
        RuleEval ruleEval2=new RuleEval();
        ruleEval2.setAction("asdfsdf");
        ruleEval2.setCondition("eeeee");
        rule.setRuleEval(ruleEval2);
        list.add(rule);
        ruleSet.setRules(list);
        //转为xml
        String xml = xStream.toXML(ruleSet);
        System.out.println(xml);*/


        XStream xstream = new XStream(new DomDriver());
        xstream.autodetectAnnotations(true);
        xstream.processAnnotations(new Class[]{RuleSet.class, Rule.class});
        RuleEngine ruleEngine = new RuleEngineDefault();

        ruleEngine.addRules((RuleSet) xstream.fromXML(FileUtils.readFileToString(new File(feerulexml), "utf-8")));
        ruleEngine.addRuleExecutors(Arrays.<RuleExecutor>asList(new FeeRuleExecutor()));
        RuleContext ruleContext = new RuleContextDefault();
        ruleContext.put("salary", 5100);
        ruleEngine.execute(ruleContext, "feerule");
        System.out.println("-->" + ruleContext.get("totalSalary"));
    }

}