package com.sc.utils.qrcode.compose;

import java.io.File;


public class QrcodeVo {
    
    private String content;
    
    private Integer width;
    
    private Integer height;
    
    private Integer margin;
    
    private Enum errorCorrection;

    
    private File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getWidth() {
        return this.width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return this.height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getMargin() {
        return this.margin;
    }

    public void setMargin(Integer margin) {
        this.margin = margin;
    }

    public Enum getErrorCorrection() {
        return this.errorCorrection;
    }

    public void setErrorCorrection(Enum errorCorrection) {
        this.errorCorrection = errorCorrection;
    }


}
