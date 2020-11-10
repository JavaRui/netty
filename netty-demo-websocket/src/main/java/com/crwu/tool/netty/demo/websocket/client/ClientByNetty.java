package com.crwu.tool.netty.demo.websocket.client;

import cn.hutool.core.util.RandomUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.URI;

/**
 * 基于websocket的netty客户端
 *
 */
public class ClientByNetty {
    public static  URI websocketURI ;

    static{
        try {
            if(websocketURI == null){
                websocketURI = new URI("ws://localhost:10080/hello");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {

        //netty基本操作，线程组
        EventLoopGroup group = new NioEventLoopGroup();
        //netty基本操作，启动类
        Bootstrap boot = new Bootstrap();
        boot.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .group(group)
                .handler(new LoggingHandler(LogLevel.INFO))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("http-codec",new HttpClientCodec());
                        pipeline.addLast("aggregator",new HttpObjectAggregator(1024*1024*10));
                        pipeline.addLast("hookedHandler", new WebSocketClientHandler());
                    }
                });
        //websocke连接的地址，/hello是因为在服务端的websockethandler设置的


                //客户端与服务端连接的通道，final修饰表示只会有一个
        final Channel channel = boot.connect(websocketURI.getHost(), websocketURI.getPort()).sync().channel();//进行握手


        System.out.println("EchoClient.main ServerBootstrap配置启动完成");
        channel.closeFuture().sync();
        System.out.println("EchoClient.end");

//        HttpHeaders httpHeaders = new DefaultHttpHeaders();
//        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, (String) null, true, httpHeaders);
//        WebSocketClientHandler handler = (WebSocketClientHandler) channel.pipeline().get("hookedHandler");
//        handler.setHandshaker(handshaker);
//        handshaker.handshake(channel);
//        //此处阻塞是为了表示等待等待是否握手成功
//        handler.handshakeFuture().sync();
//        System.out.println("握手成功");
//        //给服务端发送的内容，如果客户端与服务端连接成功后，可以多次掉用这个方法发送消息
//        for(int i = 0 ; i<10 ;i++){
//            sengMessage(channel);
//            Thread.sleep(5000);
//        }

    }

    public static void sengMessage(Channel channel){
        //发送的内容，是一个文本格式的内容
        String putMessage="你好，我是客户端:  "+RandomUtil.randomString(3);
        TextWebSocketFrame frame = new TextWebSocketFrame(putMessage);
        channel.writeAndFlush(frame).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    System.out.println("消息发送成功，发送的消息是："+putMessage);
                } else {
                    System.out.println("消息发送失败 " + channelFuture.cause().getMessage());
                }
            }
        });
    }

}
