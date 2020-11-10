package com.crwu.tool.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;

import static io.netty.buffer.Unpooled.copiedBuffer;

@Slf4j
public class SimpleHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    AtomicLong atomicLong = new AtomicLong(0);

	//获取class路径
    private URL baseURL = SimpleHttpServerHandler.class.getResource("");
    private final String webroot = "webroot";


    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception {
        log.info("进来了=============");
        super.channelRead(ctx,msg);
        boolean b = acceptInboundMessage(msg);
        if(!b){
            log.info("已经跳过了？？？？？？直接下一个handler");
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
    		String uri = request.getUri();

        log.info(uri);

        ByteBuf content = copiedBuffer("hello", CharsetUtil.UTF_8);

        HttpResponse response = responseOK( HttpResponseStatus.OK,content);


        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

//        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel client = ctx.channel();
        log.info("Client:"+client.remoteAddress()+"异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }




    private FullHttpResponse responseOK(HttpResponseStatus status, ByteBuf content) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
        if (content != null) {
            response.headers().set("Content-Type", "text/plain;charset=UTF-8");
            response.headers().set("Content_Length", response.content().readableBytes());
        }
        return response;
    }

}

