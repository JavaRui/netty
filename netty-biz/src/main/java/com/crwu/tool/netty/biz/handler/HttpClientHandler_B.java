package com.crwu.tool.netty.biz.handler;

import com.crwu.tool.netty.biz.protocol.IMMessage;
import com.crwu.tool.netty.biz.protocol.IMP;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class HttpClientHandler_B extends ChannelInboundHandlerAdapter {


    private ChannelHandlerContext ctx;
    private String nickName = "cr";
    public HttpClientHandler_B(String nickName){
        this.nickName = nickName;
    }

    public void setNickName(String nickName){
        this.nickName = nickName;
    }

    private static LinkedBlockingQueue<String> queue = new LinkedBlockingQueue();

    public static void addLinked(String content){
        queue.add(content);
    }


    /**启动客户端控制台*/
    private void session() throws IOException {
        new Thread(){
            public void run(){
                System.out.println(nickName + ",你好，请在控制台输入对话内容");
                IMMessage message = null;

                while(true){
                    try {
                        if(queue.isEmpty()){
                            Thread.sleep(10);
                            continue;
                        }

                        String input = queue.poll();

                        message = new IMMessage(IMP.CHAT.getName(),System.currentTimeMillis(),nickName,input);

                        sendMsg(message);
                        if(queue.isEmpty()){
                            Thread.sleep(10);
                            continue;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

            }

        }.start();
    }

    private void sendMsg(IMMessage message)  throws Exception{
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,HttpMethod.GET,"/hello/"+message.getContent());
//        HttpRequest request = HttpCreateor.createReqGet("localhost", new URI("/json"));
        ctx.writeAndFlush(request);
        // 发送
//        ctx.channel().writeAndFlush(request).addListener(new ChannelFutureListener() {
//
//            @Override
//            public void operationComplete(ChannelFuture future) throws Exception {
//
//            }
//        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, uri.toASCIIString());
//        request.headers().add(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
//        request.headers().add(HttpHeaderNames.CONTENT_LENGTH,request.content().readableBytes());
       this.ctx = ctx;

        session();

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        System.out.println("msg -> "+msg);
        if(msg instanceof FullHttpResponse){
            FullHttpResponse response = (FullHttpResponse)msg;
            ByteBuf buf = response.content();
            String result = buf.toString(CharsetUtil.UTF_8);
            System.out.println("response -> "+result);
        }
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