package com.crwu.tool.netty.demo.websocket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.net.InetSocketAddress;

/**
 * 基于websocket的服务端代码
 */
public class ServerByNetty {
    public static void main(String[] args) throws Exception{
        ServerByNetty serverByNetty = new ServerByNetty();
        serverByNetty.startServer();
//        SpringApplication.run(ThemApplication.class, args);
    }

    /**
     * 服务端启动类
     * @throws Exception
     */
    public void startServer() throws Exception {
        // netty基本操作，两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup wokerGroup = new NioEventLoopGroup();
        try{
            //netty的启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,wokerGroup).channel(NioServerSocketChannel.class)
                    //记录日志的handler，netty自带的
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .option(ChannelOption.SO_BACKLOG,1024*1024*10)
                    //设置handler
                    .childHandler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //websocket协议本身是基于Http协议的，所以需要Http解码器
                            pipeline.addLast("http-codec",new HttpServerCodec());
                            //以块的方式来写的处理器
                            pipeline.addLast("http-chunked",new ChunkedWriteHandler());
                            //netty是基于分段请求的，HttpObjectAggregator的作用是将请求分段再聚合,参数是聚合字节的最大长度
                            pipeline.addLast("aggregator",new HttpObjectAggregator(1024*1024*1024));
                            //这个是websocket的handler，是netty提供的，也可以自定义，建议就用默认的
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello",null,true,65535));
                            //自定义的handler，处理服务端传来的消息
                            pipeline.addLast(new WebSocketHandle());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(new InetSocketAddress(8899)).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            wokerGroup.shutdownGracefully();
        }

    }
}
