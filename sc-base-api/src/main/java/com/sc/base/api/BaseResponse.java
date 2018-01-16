package com.sc.base.api;

import java.io.Serializable;
/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class BaseResponse implements Serializable {
    public static final int RESULT_CODE_SUCCESS = 0;
    public static final int RESULT_CODE_VALIDATE_FAILED = -1;
    public static final int RESULT_CODE_SERVICE_UNAVAILABLE = -98;
    public static final int RESULT_CODE_UNKNOWN = -99;
    private int resultCode = 0;
    private String resultMessage;
    private String debugResultMessage;

    public BaseResponse() {
    }

    public int getResultCode() {
        return this.resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return this.resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public String getDebugResultMessage() {
        return this.debugResultMessage;
    }

    public void setDebugResultMessage(String debugResultMessage) {
        this.debugResultMessage = debugResultMessage;
    }

    public void setResultCodeAndMessage(int resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }
}
