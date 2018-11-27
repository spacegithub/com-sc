package com.sc.excel.test.listen;

import com.sc.excel.context.AnalysisContext;
import com.sc.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExcelListener extends AnalysisEventListener {


    private List<Object>  data = new ArrayList<Object>();

    @Override
    public void invoke(Object object, AnalysisContext context) {
        System.out.println(context.getCurrentSheet());
        if(data.size()<=100){
            data.add(object);
        }else {
            doSomething();
            data = new ArrayList<Object>();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        doSomething();
    }
    public void doSomething(){
        for (Object o:data) {
            System.out.println(o);
        }
    }
}
