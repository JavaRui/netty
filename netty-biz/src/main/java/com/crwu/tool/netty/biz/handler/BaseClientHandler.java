package com.crwu.tool.netty.biz.handler;

import com.crwu.tool.netty.biz.protocol.IMMessage;
import com.crwu.tool.netty.biz.protocol.IMP;
import com.crwu.tool.netty.biz.setting.GlobalSetting;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public abstract class BaseClientHandler<T> extends SimpleChannelInboundHandler<T> implements HandlerFace{

    private ChannelHandlerContext ctx;
    public BaseClientHandler(){
        HandlerMgr.setHandlerFace(this);
    }

    protected ChannelHandlerContext getCtx(){
        return ctx;
    }


    /**
     * tcp链路建立成功后调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        IMMessage message = new IMMessage(IMP.LOGIN.getName(),"Console",System.currentTimeMillis(),GlobalSetting.getCreateClientVo().getNickName());
        sendMsg(message);
        log.info("成功连接服务器,已执行登录动作-->"+message);
        HandlerMgr.setHandlerFace(this);
        HandlerMgr.init();
        GlobalSetting.setNioSocketChannel((NioSocketChannel) ctx.channel());
    }

    /**
     * 收到消息后调用
     * @throws IOException
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, T msg) throws IOException {
        log.info("收到消息：  {}",msg);
    }


    /**
     * 发送消息
     * @param msg
     * @return
     * @throws IOException
     */
    @Override
    public boolean sendMsg(Object msg){

        log.info("发送:   {} "+msg);
        ctx.channel().writeAndFlush(msg);
        log.info("继续输入开始对话...");
        return true;
    }

    /**
     * 发生异常时调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("与服务器断开连接:"+cause.getMessage());
        ctx.close();
    }

    @Override
    public Object packMsg(Object msg) {
        return msg;
    }
}
