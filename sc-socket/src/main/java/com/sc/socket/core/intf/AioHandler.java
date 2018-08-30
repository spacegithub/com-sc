package com.sc.socket.core.intf;

import com.sc.socket.core.ChannelContext;
import com.sc.socket.core.exception.AioDecodeException;
import com.sc.socket.core.GroupContext;

import java.nio.ByteBuffer;

/**
 * 
 *
 * 2017年10月19日 上午9:40:15
 */
public interface AioHandler {

	/**
	 * 根据ByteBuffer解码成业务需要的Packet对象.
	 * 如果收到的数据不全，导致解码失败，请返回null，在下次消息来时框架层会自动续上前面的收到的数据
	 * @param buffer 参与本次希望解码的ByteBuffer
	 * @param limit ByteBuffer的limit
	 * @param position ByteBuffer的position，不一定是0哦
	 * @param readableLength ByteBuffer参与本次解码的有效数据（= limit - position）
	 * @param channelContext
	 * @return
	 * @throws AioDecodeException
	 */
	Packet decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException;

	/**
	 * 编码
	 * @param packet
	 * @param groupContext
	 * @param channelContext
	 * @return
	 *
	 */
	ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext);

	/**
	 * 处理消息包
	 * @param packet
	 * @param channelContext
	 * @throws Exception
	 *
	 */
	void handler(Packet packet, ChannelContext channelContext) throws Exception;

}
