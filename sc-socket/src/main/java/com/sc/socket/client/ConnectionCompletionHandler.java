package com.sc.socket.client;

import com.sc.socket.client.intf.ClientAioListener;
import com.sc.socket.core.GroupContext;
import com.sc.socket.core.Node;
import com.sc.socket.core.ReadCompletionHandler;
import com.sc.socket.core.ssl.SslFacadeContext;
import com.sc.socket.core.ssl.SslUtils;
import com.sc.socket.core.stat.IpStat;
import com.sc.socket.utils.SystemTimer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 *
 *
 * 2017年4月1日 上午9:32:10
 */
public class ConnectionCompletionHandler implements CompletionHandler<Void, ConnectionCompletionVo> {
	private static Logger log = LoggerFactory.getLogger(ConnectionCompletionHandler.class);

	/**
	 *
	 * @param result
	 * @param attachment
	 *
	 */
	@Override
	public void completed(Void result, ConnectionCompletionVo attachment) {
		handler(result, attachment, null);
	}

	/**
	 *
	 * @param throwable
	 * @param attachment
	 *
	 */
	@Override
	public void failed(Throwable throwable, ConnectionCompletionVo attachment) {
		handler(null, attachment, throwable);
	}

	/**
	 *
	 * @param result
	 * @param attachment
	 * @param throwable
	 *
	 */
	private void handler(Void result, ConnectionCompletionVo attachment, Throwable throwable) {
		ClientChannelContext channelContext = attachment.getChannelContext();
		AsynchronousSocketChannel asynchronousSocketChannel = attachment.getAsynchronousSocketChannel();
		TioClient tioClient = attachment.getTioClient();
		ClientGroupContext clientGroupContext = tioClient.getClientGroupContext();
		Node serverNode = attachment.getServerNode();
		String bindIp = attachment.getBindIp();
		Integer bindPort = attachment.getBindPort();
		ClientAioListener clientAioListener = clientGroupContext.getClientAioListener();
		boolean isReconnect = attachment.isReconnect();
		boolean isConnected = false;

		try {
			if (throwable == null) {
				if (isReconnect) {
					channelContext.setAsynchronousSocketChannel(asynchronousSocketChannel);
					//				channelContext.getDecodeRunnable().setCanceled(false);
					channelContext.handlerRunnable.setCanceled(false);
					//		channelContext.getHandlerRunnableHighPrior().setCanceled(false);
					channelContext.sendRunnable.setCanceled(false);
					//		channelContext.getSendRunnableHighPrior().setCanceled(false);

					clientGroupContext.closeds.remove(channelContext);
				} else {
					channelContext = new ClientChannelContext(clientGroupContext, asynchronousSocketChannel);
					channelContext.setServerNode(serverNode);
				}

				channelContext.setBindIp(bindIp);
				channelContext.setBindPort(bindPort);

				channelContext.setReconnCount(0);
				channelContext.setClosed(false);
				isConnected = true;

				attachment.setChannelContext(channelContext);

				//				clientGroupContext.ips.bind(channelContext);
				clientGroupContext.connecteds.add(channelContext);

				ReadCompletionHandler readCompletionHandler = channelContext.getReadCompletionHandler();
				ByteBuffer readByteBuffer = readCompletionHandler.getReadByteBuffer();//ByteBuffer.allocateDirect(channelContext.groupContext.getReadBufferSize());
				readByteBuffer.position(0);
				readByteBuffer.limit(readByteBuffer.capacity());
				asynchronousSocketChannel.read(readByteBuffer, readByteBuffer, readCompletionHandler);

				log.info("connected to {}", serverNode);
				if (isConnected && !isReconnect) {
					channelContext.stat.setTimeFirstConnected(SystemTimer.currTime);
				}
			} else {
				log.error(throwable.toString(), throwable);
				if (channelContext == null) {
					channelContext = new ClientChannelContext(clientGroupContext, asynchronousSocketChannel);
					channelContext.setServerNode(serverNode);
				}

				if (!isReconnect) //不是重连，则是第一次连接，需要把channelContext加到closeds行列
				{
					clientGroupContext.closeds.add(channelContext);
				}

				attachment.setChannelContext(channelContext);

				ReconnConf.put(channelContext);
			}
		} catch (Throwable e) {
			log.error(e.toString(), e);
		} finally {
			if (attachment.getCountDownLatch() != null) {
				attachment.getCountDownLatch().countDown();
			}

			try {
				channelContext.setReconnect(isReconnect);

				if (SslUtils.isSsl(channelContext.groupContext)) {
					if (isConnected) {
						//						channelContext.sslFacadeContext.beginHandshake();
						SslFacadeContext sslFacadeContext = new SslFacadeContext(channelContext);
						sslFacadeContext.beginHandshake();
					} else {
						if (clientAioListener != null) {
							clientAioListener.onAfterConnected(channelContext, isConnected, isReconnect);
						}
					}
				} else {
					if (clientAioListener != null) {
						clientAioListener.onAfterConnected(channelContext, isConnected, isReconnect);
					}
				}

				GroupContext groupContext = channelContext.groupContext;
				if (groupContext.ipStats.durationList != null && groupContext.ipStats.durationList.size() > 0) {
					try {
						for (Long v : groupContext.ipStats.durationList) {
							IpStat ipStat = groupContext.ipStats.get(v, channelContext.getClientNode().getIp());
							ipStat.getRequestCount().incrementAndGet();
							groupContext.getIpStatListener().onAfterConnected(channelContext, isConnected, isReconnect, ipStat);
						}
					} catch (Exception e) {
						log.error(e.toString(), e);
					}
				}
			} catch (Throwable e1) {
				log.error(e1.toString(), e1);
			}
		}
	}
}
