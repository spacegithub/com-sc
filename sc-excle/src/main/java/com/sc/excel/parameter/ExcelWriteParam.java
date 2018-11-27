package com.sc.excel.parameter;

import com.sc.excel.support.ExcelTypeEnum;

import java.io.OutputStream;

/**
 * {@link com.sc.excel.ExcelWriter}
 *
 *
 */
@Deprecated
public class ExcelWriteParam {

    /**
     */
    private OutputStream outputStream;

    /**
     */
    private ExcelTypeEnum type;

    public ExcelWriteParam(OutputStream outputStream, ExcelTypeEnum type) {
        this.outputStream = outputStream;
        this.type = type;

    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public ExcelTypeEnum getType() {
        return type;
    }

    public void setType(ExcelTypeEnum type) {
        this.type = type;
    }
}
