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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class RuleEngineDefaultTest {
    @Test
    public void execute() throws Exception {
        String feerulexml = Class.class.getClass().getResource("/").getPath() + "feerule.xml";
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

    @Test
    public void testCompoter() {
        List list=new ArrayList();
        list.add(1);
        list.add(3);
        list.add(10);
        list.add(6);
        Collections.sort(list, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 > o2 ? 1 : o1 == o2 ? 0 : -1;
            }
        });

        list.remove(0);
        list.remove(0);




    }

}