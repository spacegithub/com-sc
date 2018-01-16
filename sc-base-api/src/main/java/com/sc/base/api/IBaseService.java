package com.sc.base.api;

public interface IBaseService<IN extends BaseRequest, OUT extends BaseResponse> {
    <T extends HeaderInfo> OUT call(T var1, IN var2);
}
