package com.sc.excel.parameter;

import com.sc.excel.support.ExcelTypeEnum;

import java.io.OutputStream;


public class GenerateParam {

    private OutputStream outputStream;

    private String sheetName;

    private Class clazz;

    private ExcelTypeEnum type;

    public GenerateParam(String sheetName, Class clazz, OutputStream outputStream) {
        this.outputStream = outputStream;
        this.sheetName = sheetName;
        this.clazz = clazz;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }


    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public ExcelTypeEnum getType() {
        return type;
    }

    public void setType(ExcelTypeEnum type) {
        this.type = type;
    }
}
