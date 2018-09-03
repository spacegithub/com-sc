package com.sc.socket.core;

import com.sc.socket.client.ClientChannelContext;
import com.sc.socket.client.ClientGroupContext;
import com.sc.socket.client.ReconnConf;
import com.sc.socket.core.intf.Packet;
import com.sc.socket.utils.convert.Converter;
import com.sc.socket.utils.hutool.StrUtil;
import com.sc.socket.utils.lock.MapWithLock;
import com.sc.socket.utils.lock.SetWithLock;
import com.sc.socket.utils.page.Page;
import com.sc.socket.utils.page.PageUtils;
import com.sc.socket.utils.thread.ThreadUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * 用户常用操作的封装
 * 用户关心的API几乎全在这
 */
public class Sio {
    /**
     * The log.
     */
    private static Logger log = LoggerFactory.getLogger(Sio.class);

    private Sio() {
    }

    /**
     * 绑定群组
     */
    public static void bindGroup(ChannelContext channelContext, String group) {
        channelContext.groupContext.groups.bind(group, channelContext);
    }

    /**
     * 某通道是否在某群组中
     *
     * @return true：在该群组
     */
    public static boolean isInGroup(String group, ChannelContext channelContext) {
        SetWithLock<String> set = channelContext.getGroups();
        if (set == null) {
            return false;
        }
        return set.getObj().contains(group);
        //
        //		MapWithLock<ChannelContext, SetWithLock<String>> mapWithLock =
        //				channelContext.groupContext.groups.getChannelmap();
        //		ReadLock lock = mapWithLock.readLock();
        //		lock.lock();
        //		try {
        //			Map<ChannelContext, SetWithLock<String>> m = mapWithLock.getObj();
        //			if (m == null || m.size() == 0) {
        //				return false;
        //			}
        //			SetWithLock<String> set = m.get(channelContext);
        //			if (set == null) {
        //				return false;
        //			}
        //			return set.getObj().contains(group);
        //		} catch (Throwable e) {
        //			log.error(e.toString(), e);
        //			return false;
        //		} finally {
        //			lock.unlock();
        //		}
    }

    /**
     * 群组有多少个连接
     */
    public static int groupCount(GroupContext groupContext, String group) {
        SetWithLock<ChannelContext> setWithLock = groupContext.groups.clients(groupContext, group);
        if (setWithLock == null) {
            return 0;
        }

        return setWithLock.getObj().size();
    }

    /**
     * 绑定用户
     */
    public static void bindUser(ChannelContext channelContext, String userid) {
        channelContext.groupContext.users.bind(userid, channelContext);
    }

    /**
     * 绑定token
     */
    public static void bindToken(ChannelContext channelContext, String token) {
        channelContext.groupContext.tokens.bind(token, channelContext);
    }

    /**
     * 绑定业务id
     */
    public static void bindBsId(ChannelContext channelContext, String bsId) {
        channelContext.groupContext.bsIds.bind(channelContext, bsId);
    }

    /**
     * 解绑业务id
     */
    public static void unbindBsId(ChannelContext channelContext) {
        channelContext.groupContext.bsIds.unbind(channelContext);
    }

    /**
     * 根据业务id找ChannelContext
     */
    public static ChannelContext getChannelContextByBsId(GroupContext groupContext, String bsId) {
        return groupContext.bsIds.find(groupContext, bsId);
    }

    /**
     * 发消息给指定业务ID
     */
    public static Boolean sendToBsId(GroupContext groupContext, String bsId, Packet packet) {
        return sendToBsId(groupContext, bsId, packet, false);
    }

    /**
     * 阻塞发消息给指定业务ID
     */
    public static void bSendToBsId(GroupContext groupContext, String bsId, Packet packet) {
        sendToBsId(groupContext, bsId, packet, true);
    }

    /**
     * 发消息给指定业务ID
     */
    private static Boolean sendToBsId(GroupContext groupContext, String bsId, Packet packet, boolean isBlock) {
        ChannelContext channelContext = Sio.getChannelContextByBsId(groupContext, bsId);
        if (isBlock) {
            return bSend(channelContext, packet);
        } else {
            return send(channelContext, packet);
        }
    }

    /**
     * 阻塞发送消息到指定ChannelContext
     */
    public static Boolean bSend(ChannelContext channelContext, Packet packet) {
        if (channelContext == null) {
            return false;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        return send(channelContext, packet, countDownLatch, PacketSendMode.SINGLE_BLOCK);
    }

    /**
     * 发送到指定的ip和port
     */
    public static Boolean bSend(GroupContext groupContext, String ip, int port, Packet packet) {
        return send(groupContext, ip, port, packet, true);
    }

    /**
     * 发消息到所有连接
     */
    public static Boolean bSendToAll(GroupContext groupContext, Packet packet, ChannelContextFilter channelContextFilter) {
        return sendToAll(groupContext, packet, channelContextFilter, true);
    }

    /**
     * 发消息到组
     */
    public static void bSendToGroup(GroupContext groupContext, String group, Packet packet) {
        bSendToGroup(groupContext, group, packet, null);
    }

    /**
     * 发消息到组
     */
    public static Boolean bSendToGroup(GroupContext groupContext, String group, Packet packet, ChannelContextFilter channelContextFilter) {
        return sendToGroup(groupContext, group, packet, channelContextFilter, true);
    }

    /**
     * 发消息给指定ChannelContext id
     */
    public static void bSendToId(GroupContext groupContext, String channelId, Packet packet) {
        sendToId(groupContext, channelId, packet, true);
    }

    /**
     * 发消息到指定集合
     */
    public static Boolean bSendToSet(GroupContext groupContext, SetWithLock<ChannelContext> setWithLock, Packet packet, ChannelContextFilter channelContextFilter) {
        return sendToSet(groupContext, setWithLock, packet, channelContextFilter, true);
    }

    /**
     * 阻塞发消息给指定用户
     */
    public static Boolean bSendToUser(GroupContext groupContext, String userid, Packet packet) {
        return sendToUser(groupContext, userid, packet, true);
    }

    /**
     * 阻塞发消息到指定token
     */
    public static Boolean bSendToToken(GroupContext groupContext, String token, Packet packet) {
        return sendToToken(groupContext, token, packet, true);
    }

    /**
     * 关闭连接
     */
    public static void close(ChannelContext channelContext, String remark) {
        close(channelContext, null, remark);
    }

    /**
     * 关闭连接
     */
    public static void close(ChannelContext channelContext, Throwable throwable, String remark) {
        close(channelContext, throwable, remark, false);
    }

    /**
     *
     * @param channelContext
     * @param throwable
     * @param remark
     * @param isNeedRemove
     *
     */
    private static void close(ChannelContext channelContext, Throwable throwable, String remark, boolean isNeedRemove) {
        if (channelContext == null) {
            return;
        }
        if (channelContext.isWaitingClose) {
            log.debug("{} 正在等待被关闭", channelContext);
            return;
        }

        WriteLock writeLock = channelContext.closeLock.writeLock();
        boolean tryLock = writeLock.tryLock();
        if (!tryLock) {
            return;
        }
        channelContext.isWaitingClose = true;
        writeLock.unlock();

        if (channelContext.asynchronousSocketChannel != null) {
            try {
                channelContext.asynchronousSocketChannel.shutdownInput();
            } catch (Throwable e) {
                //log.error(e.toString(), e);
            }
            try {
                channelContext.asynchronousSocketChannel.shutdownOutput();
            } catch (Throwable e) {
                //log.error(e.toString(), e);
            }
            try {
                channelContext.asynchronousSocketChannel.close();
            } catch (Throwable e) {
                //log.error(e.toString(), e);
            }
        }


        channelContext.closeMeta.setRemark(remark);
        channelContext.closeMeta.setThrowable(throwable);
        if (!isNeedRemove) {
            if (channelContext.isServer()) {
                isNeedRemove = true;
            } else {
                ClientChannelContext clientChannelContext = (ClientChannelContext) channelContext;
                if (!ReconnConf.isNeedReconn(clientChannelContext, false)) {
                    isNeedRemove = true;
                }
            }
        }
        channelContext.closeMeta.setNeedRemove(isNeedRemove);

        channelContext.groupContext.closeRunnable.addMsg(channelContext);
        channelContext.groupContext.closeRunnable.execute();
    }

    /**
     * 关闭连接
     */
    public static void close(GroupContext groupContext, String clientIp, Integer clientPort, Throwable throwable, String remark) {
        ChannelContext channelContext = groupContext.clientNodes.find(clientIp, clientPort);
        close(channelContext, throwable, remark);
    }

    /**
     * 获取所有连接，包括当前处于断开状态的
     */
    public static SetWithLock<ChannelContext> getAllChannelContexts(GroupContext groupContext) {
        return groupContext.connections;
    }

    /**
     * 获取所有处于正常连接状态的连接
     */
    public static SetWithLock<ChannelContext> getAllConnectedsChannelContexts(ClientGroupContext clientGroupContext) {
        return clientGroupContext.connecteds;
    }

    /**
     * 根据clientip和clientport获取ChannelContext
     */
    public static ChannelContext getChannelContextByClientNode(GroupContext groupContext, String clientIp, Integer clientPort) {
        return groupContext.clientNodes.find(clientIp, clientPort);
    }

    /**
     * 根据id获取ChannelContext
     */
    public static ChannelContext getChannelContextById(GroupContext groupContext, String channelId) {
        return groupContext.ids.find(groupContext, channelId);
    }

    /**
     * 根据userid获取SetWithLock<ChannelContext>
     */
    public static SetWithLock<ChannelContext> getChannelContextsByUserid(GroupContext groupContext, String userid) {
        return groupContext.users.find(groupContext, userid);
    }

    /**
     * 根据token获取SetWithLock<ChannelContext>
     */
    public static SetWithLock<ChannelContext> getChannelContextsByToken(GroupContext groupContext, String token) {
        return groupContext.tokens.find(groupContext, token);
    }

    /**
     * 获取一个组的所有客户端
     */
    public static SetWithLock<ChannelContext> getChannelContextsByGroup(GroupContext groupContext, String group) {
        return groupContext.groups.clients(groupContext, group);
    }

    /**
     *
     * @param groupContext
     * @param pageIndex
     * @param pageSize
     * @return
     *
     */
    public static Page<ChannelContext> getPageOfAll(GroupContext groupContext, Integer pageIndex, Integer pageSize) {
        SetWithLock<ChannelContext> setWithLock = Sio.getAllChannelContexts(groupContext);
        return PageUtils.fromSetWithLock(setWithLock, pageIndex, pageSize);
    }

    /**
     *
     * @param groupContext
     * @param pageIndex
     * @param pageSize
     * @param converter
     * @return
     */
    public static <T> Page<T> getPageOfAll(GroupContext groupContext, Integer pageIndex, Integer pageSize, Converter<T> converter) {
        SetWithLock<ChannelContext> setWithLock = Sio.getAllChannelContexts(groupContext);
        return PageUtils.fromSetWithLock(setWithLock, pageIndex, pageSize, converter);
    }

    /**
     * 这个方法是给服务器端用的
     */
    public static Page<ChannelContext> getPageOfConnecteds(ClientGroupContext clientGroupContext, Integer pageIndex, Integer pageSize) {
        SetWithLock<ChannelContext> setWithLock = Sio.getAllConnectedsChannelContexts(clientGroupContext);
        return PageUtils.fromSetWithLock(setWithLock, pageIndex, pageSize);
    }

    /**
     *
     * @param clientGroupContext
     * @param pageIndex
     * @param pageSize
     * @param converter
     * @return
     */
    public static <T> Page<T> getPageOfConnecteds(ClientGroupContext clientGroupContext, Integer pageIndex, Integer pageSize, Converter<T> converter) {
        SetWithLock<ChannelContext> setWithLock = Sio.getAllConnectedsChannelContexts(clientGroupContext);
        return PageUtils.fromSetWithLock(setWithLock, pageIndex, pageSize, converter);
    }

    /**
     *
     * @param groupContext
     * @param group
     * @param pageIndex
     * @param pageSize
     * @return
     *
     */
    public static Page<ChannelContext> getPageOfGroup(GroupContext groupContext, String group, Integer pageIndex, Integer pageSize) {
        SetWithLock<ChannelContext> setWithLock = Sio.getChannelContextsByGroup(groupContext, group);
        return PageUtils.fromSetWithLock(setWithLock, pageIndex, pageSize);
    }

    /**
     *
     * @param groupContext
     * @param group
     * @param pageIndex
     * @param pageSize
     * @param converter
     * @return
     */
    public static <T> Page<T> getPageOfGroup(GroupContext groupContext, String group, Integer pageIndex, Integer pageSize, Converter<T> converter) {
        SetWithLock<ChannelContext> setWithLock = Sio.getChannelContextsByGroup(groupContext, group);
        return PageUtils.fromSetWithLock(setWithLock, pageIndex, pageSize, converter);
    }

    /**
     * 和close方法一样，只不过不再进行重连等维护性的操作
     */
    public static void remove(ChannelContext channelContext, String remark) {
        remove(channelContext, null, remark);
    }

    /**
     * 删除client ip为指定值的所有连接
     */
    public static void remove(GroupContext groupContext, String ip, String remark) {
        SetWithLock<ChannelContext> setWithLock = Sio.getAllChannelContexts(groupContext);
        Lock lock2 = setWithLock.readLock();
        lock2.lock();
        try {
            Set<ChannelContext> set = setWithLock.getObj();
            for (ChannelContext channelContext : set) {
                String clientIp = channelContext.getClientNode().getIp();
                if (StrUtil.equals(clientIp, ip)) {
                    Sio.remove(channelContext, remark);
                }
            }
        } finally {
            lock2.unlock();
        }
    }

    /**
     * 和close方法一样，只不过不再进行重连等维护性的操作
     */
    public static void remove(ChannelContext channelContext, Throwable throwable, String remark) {
        close(channelContext, throwable, remark, true);
    }

    /**
     * 和close方法一样，只不过不再进行重连等维护性的操作
     */
    public static void remove(GroupContext groupContext, String clientIp, Integer clientPort, Throwable throwable, String remark) {
        ChannelContext channelContext = groupContext.clientNodes.find(clientIp, clientPort);
        remove(channelContext, throwable, remark);
    }

    /**
     * 发送消息到指定ChannelContext
     */
    public static Boolean send(ChannelContext channelContext, Packet packet) {
        return send(channelContext, packet, null, null);
    }

    /**
     *
     * @param channelContext
     * @param packet
     * @param countDownLatch
     * @param packetSendMode
     * @return
     *
     */
    private static Boolean send(final ChannelContext channelContext, final Packet packet, CountDownLatch countDownLatch, PacketSendMode packetSendMode) {
        try {
            if (packet == null) {
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
                return false;
            }

            if (channelContext.isVirtual) {
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
                return true;
            }

            if (channelContext == null || channelContext.isClosed || channelContext.isRemoved) {
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
                if (channelContext != null) {
                    log.error("can't send data, {}, isClosed:{}, isRemoved:{}, stack:{} ", channelContext, channelContext.isClosed, channelContext.isRemoved,
                            ThreadUtils.stackTrace());
                }
                return false;
            }

            boolean isSingleBlock = countDownLatch != null && packetSendMode == PacketSendMode.SINGLE_BLOCK;

            boolean isAdded = false;
            if (countDownLatch != null) {
                Packet.Meta meta = new Packet.Meta();
                meta.setCountDownLatch(countDownLatch);
                packet.setMeta(meta);
            }

            if (channelContext.groupContext.useQueueSend) {//发送模式,队列异步线程发送,直接发送
                isAdded = channelContext.sendRunnable.addMsg(packet);
            } else {
                isAdded = channelContext.sendRunnable.sendPacket(packet);
            }

            if (!isAdded) {
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
                return false;
            }
            if (channelContext.groupContext.useQueueSend) {
                channelContext.sendRunnable.execute();
            }

            if (isSingleBlock) {
                long timeout = 10;
                try {
//					channelContext.traceBlockPacket(SynPacketAction.BEFORE_WAIT, packet, countDownLatch, null);
                    Boolean awaitFlag = countDownLatch.await(timeout, TimeUnit.SECONDS);
//					channelContext.traceBlockPacket(SynPacketAction.AFTER__WAIT, packet, countDownLatch, null);

                    if (!awaitFlag) {
                        log.error("{}, 阻塞发送超时, timeout:{}s, packet:{}", channelContext, timeout, packet.logstr());
                    }
                } catch (InterruptedException e) {
                    log.error(e.toString(), e);
                }

                Boolean isSentSuccess = packet.getMeta().getIsSentSuccess();
                return isSentSuccess;
            } else {
                return true;
            }
        } catch (Throwable e) {
            log.error(channelContext + ", " + e.toString(), e);
            return false;
        }

    }

    /**
     * 发送到指定的ip和port
     */
    public static Boolean send(GroupContext groupContext, String ip, int port, Packet packet) {
        return send(groupContext, ip, port, packet, false);
    }

    /**
     * 发送到指定的ip和port
     */
    private static Boolean send(GroupContext groupContext, String ip, int port, Packet packet, boolean isBlock) {
        ChannelContext channelContext = groupContext.clientNodes.find(ip, port);
        if (channelContext != null) {
            if (isBlock) {
                return bSend(channelContext, packet);
            } else {
                return send(channelContext, packet);
            }
        } else {
            log.info("{}, can find channelContext by {}:{}", groupContext.getName(), ip, port);
            return false;
        }
    }

    public static void sendToAll(GroupContext groupContext, Packet packet) {
        sendToAll(groupContext, packet, null);
    }

    /**
     * 发消息到所有连接
     */
    public static void sendToAll(GroupContext groupContext, Packet packet, ChannelContextFilter channelContextFilter) {
        sendToAll(groupContext, packet, channelContextFilter, false);
    }

    /**
     *
     * @param groupContext
     * @param packet
     * @param channelContextFilter
     * @param isBlock
     *
     */
    private static Boolean sendToAll(GroupContext groupContext, Packet packet, ChannelContextFilter channelContextFilter, boolean isBlock) {

        SetWithLock<ChannelContext> setWithLock = groupContext.connections;
        if (setWithLock == null) {
            log.debug("{}, 没有任何连接", groupContext.getName());
            return false;
        }
        Boolean ret = sendToSet(groupContext, setWithLock, packet, channelContextFilter, isBlock);
        return ret;

    }

    /**
     * 发消息到组
     */
    public static void sendToGroup(GroupContext groupContext, String group, Packet packet) {
        sendToGroup(groupContext, group, packet, null);
    }

    /**
     * 发消息到组
     */
    public static void sendToGroup(GroupContext groupContext, String group, Packet packet, ChannelContextFilter channelContextFilter) {
        sendToGroup(groupContext, group, packet, channelContextFilter, false);
    }

    /**
     * 发消息到组
     */
    private static Boolean sendToGroup(GroupContext groupContext, String group, Packet packet, ChannelContextFilter channelContextFilter, boolean isBlock) {

        SetWithLock<ChannelContext> setWithLock = groupContext.groups.clients(groupContext, group);
        if (setWithLock == null) {
            log.debug("{}, 组[{}]不存在", groupContext.getName(), group);
            return false;
        }
        Boolean ret = sendToSet(groupContext, setWithLock, packet, channelContextFilter, isBlock);
        return ret;

    }

    /**
     * 阻塞发送到指定ip对应的集合
     */
    public static void bSendToIp(GroupContext groupContext, String ip, Packet packet) {
        bSendToIp(groupContext, ip, packet, null);
    }

    /**
     * 阻塞发送到指定ip对应的集合
     */
    public static Boolean bSendToIp(GroupContext groupContext, String ip, Packet packet, ChannelContextFilter channelContextFilter) {
        return sendToIp(groupContext, ip, packet, channelContextFilter, true);
    }

    /**
     * 发送到指定ip对应的集合
     */
    public static void sendToIp(GroupContext groupContext, String ip, Packet packet) {
        sendToIp(groupContext, ip, packet, null);
    }

    /**
     * 发送到指定ip对应的集合
     */
    public static void sendToIp(GroupContext groupContext, String ip, Packet packet, ChannelContextFilter channelContextFilter) {
        sendToIp(groupContext, ip, packet, channelContextFilter, false);
    }

    /**
     * 发送到指定ip对应的集合
     */
    private static Boolean sendToIp(GroupContext groupContext, String ip, Packet packet, ChannelContextFilter channelContextFilter, boolean isBlock) {

        SetWithLock<ChannelContext> setWithLock = groupContext.ips.clients(groupContext, ip);
        if (setWithLock == null) {
            log.info("{}, 没有ip为[{}]的对端", groupContext.getName(), ip);
            return false;
        }
        Boolean ret = sendToSet(groupContext, setWithLock, packet, channelContextFilter, isBlock);
        return ret;

    }

    /**
     * 发消息给指定ChannelContext id
     */
    public static Boolean sendToId(GroupContext groupContext, String channelId, Packet packet) {
        return sendToId(groupContext, channelId, packet, false);
    }

    /**
     * 发消息给指定ChannelContext id
     */
    private static Boolean sendToId(GroupContext groupContext, String channelId, Packet packet, boolean isBlock) {
        ChannelContext channelContext = Sio.getChannelContextById(groupContext, channelId);
        if (isBlock) {
            return bSend(channelContext, packet);
        } else {
            return send(channelContext, packet);
        }
    }

    /**
     * 发消息到指定集合
     */
    public static void sendToSet(GroupContext groupContext, SetWithLock<ChannelContext> setWithLock, Packet packet, ChannelContextFilter channelContextFilter) {
        sendToSet(groupContext, setWithLock, packet, channelContextFilter, false);
    }

    /**
     * 发消息到指定集合
     */
    private static Boolean sendToSet(GroupContext groupContext, SetWithLock<ChannelContext> setWithLock, Packet packet, ChannelContextFilter channelContextFilter,
                                     boolean isBlock) {
        //		if (isBlock)
        //		{
        //			try
        //			{
        //				org.GroupContext.SYN_SEND_SEMAPHORE.acquire();
        //			} catch (InterruptedException e)
        //			{
        //				log.error(e.toString(), e);
        //			}
        //		}

        boolean releasedLock = false;
        Lock lock = setWithLock.readLock();
        lock.lock();
        try {
            Set<ChannelContext> set = setWithLock.getObj();
            if (set.size() == 0) {
                log.debug("{}, 集合为空", groupContext.getName());
                return false;
            }
            //			if (!groupContext.isEncodeCareWithChannelContext()) {
            //				ByteBuffer byteBuffer = groupContext.getAioHandler().encode(packet, groupContext, null);
            //				packet.setPreEncodedByteBuffer(byteBuffer);
            //			}

            CountDownLatch countDownLatch = null;
            if (isBlock) {
                countDownLatch = new CountDownLatch(set.size());
            }
            int sendCount = 0;
            for (ChannelContext channelContext : set) {
                if (channelContextFilter != null) {
                    boolean isfilter = channelContextFilter.filter(channelContext);
                    if (!isfilter) {
                        if (isBlock) {
                            countDownLatch.countDown();
                        }
                        continue;
                    }
                }

                sendCount++;
                if (isBlock) {
//					channelContext.traceBlockPacket(SynPacketAction.BEFORE_WAIT, packet, countDownLatch, null);
                    send(channelContext, packet, countDownLatch, PacketSendMode.GROUP_BLOCK);
                } else {
                    send(channelContext, packet, null, null);
                }
            }
            lock.unlock();
            releasedLock = true;

            if (sendCount == 0) {
                return false;
            }

            if (isBlock) {
                try {
                    long timeout = sendCount / 5;
                    timeout = timeout < 10 ? 10 : timeout;
                    boolean awaitFlag = countDownLatch.await(timeout, TimeUnit.SECONDS);
                    if (!awaitFlag) {
                        log.error("{}, 同步群发超时, size:{}, timeout:{}, packet:{}", groupContext.getName(), setWithLock.getObj().size(), timeout, packet.logstr());
                        return false;
                    } else {
                        return true;
                    }
                } catch (InterruptedException e) {
                    log.error(e.toString(), e);
                    return false;
                } finally {

                }
            } else {
                return true;
            }
        } catch (Throwable e) {
            log.error(e.toString(), e);
            return false;
        } finally {
            //			if (isBlock)
            //			{
            //				org.GroupContext.SYN_SEND_SEMAPHORE.release();
            //			}
            if (!releasedLock) {
                lock.unlock();
            }
        }
    }

    /**
     * 发消息给指定用户
     */
    public static Boolean sendToUser(GroupContext groupContext, String userid, Packet packet) {
        return sendToUser(groupContext, userid, packet, false);
    }

    /**
     * 发消息到指定token
     */
    public static Boolean sendToToken(GroupContext groupContext, String token, Packet packet) {
        return sendToToken(groupContext, token, packet, false);
    }

    /**
     * 发消息给指定用户
     */
    private static Boolean sendToUser(GroupContext groupContext, String userid, Packet packet, boolean isBlock) {
        SetWithLock<ChannelContext> setWithLock = groupContext.users.find(groupContext, userid);

        if (setWithLock == null) {
            return false;
        }

        ReadLock readLock = setWithLock.readLock();
        readLock.lock();
        try {
            Set<ChannelContext> set = setWithLock.getObj();
            boolean ret = false;
            for (ChannelContext channelContext : set) {
                boolean singleRet = false;
                // 不要用 a = a || b()，容易漏执行后面的函数
                if (isBlock) {
                    singleRet = bSend(channelContext, packet);
                } else {
                    singleRet = send(channelContext, packet);
                }
                if (singleRet) {
                    ret = true;
                }
            }
            return ret;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            readLock.unlock();
        }
        return false;
    }

    /**
     * 发消息给指定token
     */
    private static Boolean sendToToken(GroupContext groupContext, String token, Packet packet, boolean isBlock) {
        SetWithLock<ChannelContext> setWithLock = groupContext.tokens.find(groupContext, token);
        if (setWithLock == null) {
            return false;
        }

        ReadLock readLock = setWithLock.readLock();
        readLock.lock();
        try {
            Set<ChannelContext> set = setWithLock.getObj();
            boolean ret = false;
            for (ChannelContext channelContext : set) {
                boolean singleRet = false;
                // 不要用 a = a || b()，容易漏执行后面的函数
                if (isBlock) {
                    singleRet = bSend(channelContext, packet);
                } else {
                    singleRet = send(channelContext, packet);
                }
                if (singleRet) {
                    ret = true;
                }
            }
            return ret;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            readLock.unlock();
        }
        return false;

    }

    /**
     * 发送并等待响应.<br> 注意：<br> 1、参数packet的synSeq不为空且大于0（null、等于小于0都不行）<br>
     * 2、对端收到此消息后，需要回一条synSeq一样的消息。业务需要在decode()方法中为packet的synSeq赋值<br>
     * 3、对于同步发送，框架层面并不会帮应用去调用handler.handler(packet, channelContext)方法，应用需要自己去处理响应的消息包，参考：groupContext.getAioHandler().handler(packet,
     * channelContext);<br>
     *
     * @param packet 业务层必须设置好synSeq字段的值，而且要保证唯一（不能重复）。可以在groupContext范围内用AtomicInteger
     */
    @SuppressWarnings("finally")
    public static Packet synSend(ChannelContext channelContext, Packet packet, long timeout) {
        Integer synSeq = packet.getSynSeq();
        if (synSeq == null || synSeq <= 0) {
            throw new RuntimeException("synSeq必须大于0");
        }

        MapWithLock<Integer, Packet> waitingResps = channelContext.groupContext.getWaitingResps();
        try {
            waitingResps.put(synSeq, packet);

            synchronized (packet) {
                send(channelContext, packet);
                try {
                    packet.wait(timeout);
                } catch (InterruptedException e) {
                    log.error(e.toString(), e);
                }
            }
        } catch (Throwable e) {
            log.error(e.toString(), e);
        } finally {
            Packet respPacket = waitingResps.remove(synSeq);
            if (respPacket == null) {
                log.error("respPacket == null,{}", channelContext);
                return null;
            }
            if (respPacket == packet) {
                log.error("{}, 同步发送超时, {}", channelContext.groupContext.getName(), channelContext);
                return null;
            }
            return respPacket;
        }
    }

    /**
     * 与所有组解除解绑关系
     */
    public static void unbindGroup(ChannelContext channelContext) {
        channelContext.groupContext.groups.unbind(channelContext);
    }

    /**
     * 与指定组解除绑定关系
     */
    public static void unbindGroup(String group, ChannelContext channelContext) {
        channelContext.groupContext.groups.unbind(group, channelContext);
    }

    /**
     * 解除channelContext绑定的userid
     */
    public static void unbindUser(ChannelContext channelContext) {
        channelContext.groupContext.users.unbind(channelContext);
    }

    //	org.GroupContext.ipBlacklist

    /**
     * 解除channelContext绑定的token
     */
    public static void unbindToken(ChannelContext channelContext) {
        channelContext.groupContext.tokens.unbind(channelContext);
    }

    /**
     * 解除userid的绑定。一般用于多地登录，踢掉前面登录的场景
     */
    public static void unbindUser(GroupContext groupContext, String userid) {
        groupContext.users.unbind(groupContext, userid);
    }

    public static class IpBlacklist {
        /**
         * 把ip添加到黑名单
         */
        public static boolean add(GroupContext groupContext, String ip) {
            return groupContext.ipBlacklist.add(ip);
        }

        /**
         * 清空黑名单
         */
        public static void clear(GroupContext groupContext) {
            groupContext.ipBlacklist.clear();
        }

        /**
         * 获取ip黑名单列表
         */
        public static Collection<String> getAll(GroupContext groupContext) {
            return groupContext.ipBlacklist.getAll();
        }

        /**
         * 是否在黑名单中
         */
        public static boolean isInBlacklist(GroupContext groupContext, String ip) {
            return groupContext.ipBlacklist.isInBlacklist(ip);
        }

        /**
         * 把ip从黑名单中删除
         */
        public static void remove(GroupContext groupContext, String ip) {
            groupContext.ipBlacklist.remove(ip);
        }
    }

}
