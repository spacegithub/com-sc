package com.sc.socket.test;

import com.mchange.v2.ser.SerializableUtils;
import com.sc.socket.core.ChannelContext;
import com.sc.socket.core.GroupContext;
import com.sc.socket.core.Sio;
import com.sc.socket.core.exception.AioDecodeException;
import com.sc.socket.core.intf.Packet;
import com.sc.socket.server.ServerGroupContext;
import com.sc.socket.server.SioServer;
import com.sc.socket.server.intf.ServerAioHandler;
import com.sc.socket.server.intf.ServerAioListener;

import java.nio.ByteBuffer;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:qiss
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class SocketTCPTestServer {
    public static void main(String[] args) throws Exception {
        ServerGroupContext serverGroupContext = new ServerGroupContext("Socket-Server", new ServerAioHandler() {
            @Override
            public Packet decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException {
                if (readableLength < HelloPacket.HEADER_LENGHT) {//消息头未读取完毕,返回NULL待下次重新读取
                    return null;
                }
                //读取消息头信息,消息头中存放的信息即为消息体的长度
                int bodyLength = buffer.getInt();
                //消息头协议错误,即消息头中存放的消息体长度异常
                if (bodyLength < 0) {
                    throw new AioDecodeException("bodyLength [" + bodyLength + "] is not right, remote:" + channelContext.getClientNode());
                }
                //本次消息的数据长度=消息头长度+消息体长度
                int neededLength = HelloPacket.HEADER_LENGHT + bodyLength;
                //收到的数据是否足够组包
                int isDataEnough = readableLength - neededLength;
                // 不够消息体长度(剩下的buffe组不了消息体)
                if (isDataEnough < 0) {//继续等待网络缓冲
                    return null;
                }
                HelloPacket packet = new HelloPacket();
                if (bodyLength > 0) {
                    byte[] bodyByt = new byte[bodyLength];
                    buffer.get(bodyByt);
                    packet.setBody(bodyByt);
                }
                return packet;
            }

            @Override
            public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext) {
                HelloPacket helloPacket = (HelloPacket) packet;
                byte[] body = helloPacket.getBody();
                int bodyLen = (body != null) ? body.length : 0;
                int allLen = HelloPacket.HEADER_LENGHT + bodyLen;
                ByteBuffer buffer = ByteBuffer.allocate(allLen);
                buffer.order(groupContext.getByteOrder());
                buffer.putInt(bodyLen);
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
                    System.out.println("接收到客户端发送的消息:" + person);
                    person.setAge(19);
                    person.setName("服务端木头");
                    helloPacket.setBody(SerializableUtils.toByteArray(person));
                    Sio.send(channelContext, helloPacket);
                } else {
                    System.out.println("接收到心跳包直接返回!");
                }
            }
        }, new ServerAioListener() {
            @Override
            public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) throws Exception {
                System.out.println("服务端:-->已接受客户端连接!");
            }

            @Override
            public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize) throws Exception {
                System.out.println("服务端:-->已接解码成功!");
            }

            @Override
            public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes) throws Exception {
                System.out.println("服务端:-->已接受到网络字节!");
            }

            @Override
            public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) throws Exception {
                System.out.println("服务端:-->已发送成功!");
            }

            @Override
            public void onAfterHandled(ChannelContext channelContext, Packet packet, long cost) throws Exception {
                System.out.println("服务端:-->已成功处理消息!");
            }

            @Override
            public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) throws Exception {
                System.out.println("服务端:-->服务端即将关闭!");
            }
        });



        SioServer tioServer = new SioServer(serverGroupContext);
        tioServer.start(null, 8886);
    }

}
