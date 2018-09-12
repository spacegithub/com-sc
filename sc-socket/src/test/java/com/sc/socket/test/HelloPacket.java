package com.sc.socket.test;

import com.sc.socket.core.intf.Packet;


/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 *
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class HelloPacket extends Packet {
    //消息头的长度,以后可以扩展消息头
    public static final int HEADER_LENGHT = 4;
    public static final String CHARSET = "utf-8";
    private byte[] body;

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
