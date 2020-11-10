package com.crwu.tool.netty.demo.websocket.client;

import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wuchengrui
 * @Description: \\TODO
 * @date 2020/11/1 02:09
 */
public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    public static ExecutorService executor =     Executors.newFixedThreadPool(10);


    //握手的状态信息
    WebSocketClientHandshaker handshaker;
    //netty自带的异步处理
    ChannelPromise handshakeFuture;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean handshakeComplete = this.handshaker.isHandshakeComplete();
        if(!handshakeComplete){
            System.out.println("虽然已经接受到服务器的返回，但当前握手的状态:  "+ handshakeComplete+" ，所以需要去设置，握手为成功");
        }else{
            System.out.println("当前握手的状态:  "+ handshakeComplete+" ");
        }
        Channel ch = ctx.channel();
        FullHttpResponse response;
        //进行握手操作
        if (!handshakeComplete) {
            try {
                response = (FullHttpResponse)msg;
                //既然已经收到了服务器的read的返回，那么就可以看做，客户端和服务器已经握手完成
                //握手协议返回，设置结束握手
                this.handshaker.finishHandshake(ch, response);
                //设置成功,此处是为了做通知，ClientByNetty的那个阻塞代码，但设置为true之后，就可以释放
                //handler.handshakeFuture().sync();
                //的锁了
                this.handshakeFuture.setSuccess();
                System.out.println("服务端的消息：  "+response.headers());
            } catch (WebSocketHandshakeException var7) {
                var7.printStackTrace();
                FullHttpResponse res = (FullHttpResponse)msg;
                String errorMsg = String.format("握手失败,status:%s,reason:%s", res.status(), res.content().toString(CharsetUtil.UTF_8));
                this.handshakeFuture.setFailure(new Exception(errorMsg));
            }
        }
        //握手已经完成，升级成为webSocket，所以，不再接受http报文
        else if (msg instanceof FullHttpResponse) {
            response = (FullHttpResponse)msg;
            throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        } else {
            //接收服务端的消息
            WebSocketFrame frame = (WebSocketFrame)msg;
            System.out.println("frame---->    "+frame);
            //文本信息
            if (frame instanceof TextWebSocketFrame) {
                TextWebSocketFrame textFrame = (TextWebSocketFrame)frame;
                System.out.println("客户端接收的消息是:    "+textFrame.text());
            }
            //二进制信息
            if (frame instanceof BinaryWebSocketFrame) {
                BinaryWebSocketFrame binFrame = (BinaryWebSocketFrame)frame;
                System.out.println("BinaryWebSocketFrame");
            }
            //ping信息
            if (frame instanceof PongWebSocketFrame) {
                System.out.println("WebSocket Client received pong");
            }
            //关闭消息
            if (frame instanceof CloseWebSocketFrame) {
                System.out.println("receive close frame");
                ch.close();
            }

        }
    }

    /**
     * Handler活跃状态，表示连接成功
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("与服务端连接成功");
        executor.execute(()->{
            Channel channel = ctx.channel();
            HttpHeaders httpHeaders = new DefaultHttpHeaders();

            handshaker = WebSocketClientHandshakerFactory.newHandshaker(ClientByNetty.websocketURI, WebSocketVersion.V13, (String) null, true, httpHeaders);
            handshaker.handshake(channel);
            //阻塞等待是否握手成功
            try {
                handshakeFuture.sync();


                System.out.println("握手成功");
                //给服务端发送的内容，如果客户端与服务端连接成功后，可以多次掉用这个方法发送消息
                for(int i = 0 ; i<10 ;i++){
                    ClientByNetty.sengMessage(channel);
                    Thread.sleep(5000);
                }
            }catch ( Exception e){
                e.printStackTrace();
            }
        });
    }

    /**
     * 非活跃状态，没有连接远程主机的时候。
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("主机关闭");
    }

    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("连接异常："+cause.getMessage());
//        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.handshakeFuture = ctx.newPromise();
    }

    public WebSocketClientHandshaker getHandshaker() {
        return handshaker;
    }

    public void setHandshaker(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelPromise getHandshakeFuture() {
        return handshakeFuture;
    }

    public void setHandshakeFuture(ChannelPromise handshakeFuture) {
        this.handshakeFuture = handshakeFuture;
    }

    public ChannelFuture handshakeFuture() {
        return this.handshakeFuture;
    }
}
