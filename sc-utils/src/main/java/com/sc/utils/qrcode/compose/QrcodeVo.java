package com.sc.utils.qrcode.compose;

import java.io.File;

/**
 * @author qiss
 */
public class QrcodeVo {
    /**
     * 二维码内容
     */
    private String content;
    /**
     * 二维码宽度
     */
    private Integer width;
    /**
     * 二维码高度
     */
    private Integer height;
    /**
     * 二维码边距 默认为1
     */
    private Integer margin;
    /**
     * 纠错等级 默认为高
     */
    private Enum errorCorrection;

    /**
     * 是否添加logo文件  默认为null不添加
     */
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