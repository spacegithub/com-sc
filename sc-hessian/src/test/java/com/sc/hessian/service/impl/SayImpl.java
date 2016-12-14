package com.sc.hessian.service.impl;

import com.sc.hessian.service.Say;

import org.springframework.stereotype.Service;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 *
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */

@Service
public class SayImpl implements Say {

    @Override
    public String say(String name) {
        return "hessian:"+name;
    }
}
