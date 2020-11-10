package com.crwu.tool.netty.biz.receiver;

import com.crwu.tool.netty.biz.protocol.IMMessage;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author wuchengrui
 * @Description: 消息接收者
 * @date 2020/10/21 20:05
 */
public interface ReceiverFace {

    void receiverMsg(IMMessage msg);

    void setChannel(NioSocketChannel nioSocketChannel);

}
