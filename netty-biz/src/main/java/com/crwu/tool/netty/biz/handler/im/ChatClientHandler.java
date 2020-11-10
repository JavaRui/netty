package com.crwu.tool.netty.biz.handler.im;

import com.crwu.tool.netty.biz.handler.BaseClientHandler;
import com.crwu.tool.netty.biz.handler.HandlerFace;
import com.crwu.tool.netty.biz.protocol.IMMessage;
import com.crwu.tool.netty.biz.receiver.ReceiverMgr;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 聊天客户端逻辑实现
 * @author Tom
 *
 */
@Slf4j
public class ChatClientHandler extends BaseClientHandler<IMMessage> implements HandlerFace {

	private ChannelHandlerContext ctx;


    /**
     * 收到消息后调用
     * @throws IOException 
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, IMMessage msg) throws IOException {
    	IMMessage m = (IMMessage)msg;
		log.info("收到   {}",m);
        ReceiverMgr.addMsg(m);
    }

    /**
     * 发生异常时调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	log.info("与服务器断开连接:"+cause.getMessage());
        ctx.close();
    }
}
