package com.sc.socket.test;

import com.mchange.v2.ser.SerializableUtils;
import com.sc.socket.client.ClientChannelContext;
import com.sc.socket.client.ClientGroupContext;
import com.sc.socket.client.ReconnConf;
import com.sc.socket.client.SioClient;
import com.sc.socket.client.intf.ClientAioHandler;
import com.sc.socket.client.intf.ClientAioListener;
import com.sc.socket.core.ChannelContext;
import com.sc.socket.core.GroupContext;
import com.sc.socket.core.Node;
import com.sc.socket.core.Sio;
import com.sc.socket.core.exception.AioDecodeException;
import com.sc.socket.core.intf.Packet;

import java.nio.ByteBuffer;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:qiss
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class SocketTCPTestClient {
    public static void main(String[] args) throws Exception {
        ClientGroupContext clientGroupContext = new ClientGroupContext(new ClientAioHandler() {
            @Override
            public Packet heartbeatPacket() {
                return new HelloPacket();
            }

            @Override
            public Packet decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException {
                if (readableLength < HelloPacket.HEADER_LENGHT) {
                    return null;
                }
                //读取消息体的长度
                int bodyLen = buffer.getInt();
                //数据不正确，则抛出AioDecodeException异常
                if (bodyLen < 0) {
                    throw new AioDecodeException("bodyLength [" + bodyLen + "] is not right, remote:" + channelContext.getClientNode());
                }
                //计算本次需要的数据长度
                int allLen = HelloPacket.HEADER_LENGHT + bodyLen;
                if (readableLength < allLen) {
                    return null;
                }
                HelloPacket packet = new HelloPacket();
                if (bodyLen > 0) {
                    byte[] bodyByt = new byte[bodyLen];
                    buffer.get(bodyByt);
                    packet.setBody(bodyByt);
                }
                return packet;

            }

            @Override
            public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext) {
                HelloPacket helloPacket = (HelloPacket) packet;
                byte[] body = helloPacket.getBody();
                int bodyLen = body != null ? body.length : 0;//消息体长度
                //总长度= 消息头的长度 + 消息体的长度
                int allLen = HelloPacket.HEADER_LENGHT + bodyLen;
                //创建一个新的bytebuffer
                ByteBuffer buffer = ByteBuffer.allocate(allLen);
                //设置字节序,默认大端序
                buffer.order(groupContext.getByteOrder());
                //写入消息头----消息头的内容就是消息体的长度,头长度为Int类型即4个字节
                buffer.putInt(bodyLen);
                //写入消息体
                if (body != null) {
                    buffer.put(body);
                }
                return buffer;
            }

            @Override
            public void handler(Packet packet, ChannelContext channelContext) throws Exception {

                HelloPacket helloPacket = (HelloPacket) packet;
                if (helloPacket.getBody() != null) {
                    Person person = (Person) SerializableUtils.fromByteArray(helloPacket.getBody());
                    System.out.println("接收到服务端发送过来的消息:" + person);
                }

            }
        }, new ClientAioListener() {
            @Override
            public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) throws Exception {
                System.out.println("客户端:-->已连接到服务端!");
            }

            @Override
            public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize) throws Exception {
                System.out.println("客户端:-->已连解码成功!");
            }

            @Override
            public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes) throws Exception {
                System.out.println("客户端:-->已连已获取到字节!");
            }

            @Override
            public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) throws Exception {
                System.out.println("客户端:-->已成功发送字节!");
            }

            @Override
            public void onAfterHandled(ChannelContext channelContext, Packet packet, long cost) throws Exception {
                System.out.println("客户端:-->已成功处理消息!");
            }

            @Override
            public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) throws Exception {
                System.out.println("客户端:-->即将关闭客户端!");
            }
        }, new ReconnConf(3000));
        clientGroupContext.setHeartbeatTimeout(2000);
        SioClient tioClient = new SioClient(clientGroupContext);
        ClientChannelContext clientChannelContext = tioClient.connect(new Node("127.0.0.1", 8886));
        Person person = new Person();
        person.setAge(18);
        person.setName("客户端木头");
        HelloPacket packet = new HelloPacket();
        packet.setBody(SerializableUtils.toByteArray(person));
        Sio.send(clientChannelContext, packet);
    }
}
