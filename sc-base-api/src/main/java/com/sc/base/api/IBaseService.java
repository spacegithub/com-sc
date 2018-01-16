package com.sc.base.api;

import com.sc.base.api.header.HeaderInfo;

public interface IBaseService<IN extends BaseRequest, OUT extends BaseResponse> {
    <T extends HeaderInfo> OUT call(T var1, IN var2);
}
