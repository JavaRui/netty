package com.crwu.tool.netty.server.handler;

import com.crwu.tool.netty.biz.handler.im.IMEncoder;
import com.crwu.tool.netty.biz.protocol.IMMessage;
import com.crwu.tool.netty.biz.protocol.IMP;
import com.crwu.tool.netty.server.notify.NotifyMgr;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wuchengrui
 * @Description: \\TODO
 * @date 2020/9/19 15:01
 */
@Slf4j
public class MessageServerHandler  extends SimpleChannelInboundHandler<IMMessage> implements ServerHandlerFace{
    private IMEncoder encoder = new IMEncoder();
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        log.info("进来了 MessageServerHandler");
//        super.channelRead(ctx,msg);
//        boolean b = acceptInboundMessage(msg);
//        if(!b){
//            log.info("已经跳过了？？？？？？直接下一个handler");
//            ctx.channel().pipeline().remove(this);
//        }
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMMessage msg) throws Exception {

        String content = msg.getContent();
        String response = "收到："+msg.getSender()+"，内容是："+content;
        msg.setContent("收到======="+content);
        log.info(response);
//        ctx.writeAndFlush(response);
//        String encode = encoder.encode(msg);
//        ctx.writeAndFlush(msg);

        NotifyMgr.inst().send2All(ctx.channel() , this,msg);

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

        IMMessage imMessage = new IMMessage();
        imMessage.setCmd(IMP.LOGOUT.getName());
        NotifyMgr.inst().send2All(ctx.channel(),this,imMessage);
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);



    }
}
