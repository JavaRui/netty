package com.crwu.tool.netty.server;

import com.crwu.tool.netty.biz.handler.im.IMDecoder;
import com.crwu.tool.netty.biz.handler.im.IMEncoder;
import com.crwu.tool.netty.server.handler.MessageServerHandler;
import com.crwu.tool.netty.server.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author wuchengrui
 * @Description: \\TODO
 * @date 2020/9/18 21:14
 */
@Slf4j
public class ServerMain {

    private int port = 10080;
    public void start(int port){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();
                            /** 解析自定义协议 */
                            pipeline.addLast(new IMDecoder());  //Inbound
                            pipeline.addLast(new IMEncoder());  //Outbound
                            pipeline.addLast(new MessageServerHandler());

                            /** 解析Http请求 */
                            pipeline.addLast(new HttpServerCodec());  //Outbound
                            //主要是将同一个http请求或响应的多个消息对象变成一个 fullHttpRequest完整的消息对象
                            pipeline.addLast("httpAggregator",new HttpObjectAggregator(64 * 1024));//Inbound
                            //主要用于处理大数据流,比如一个1G大小的文件如果你直接传输肯定会撑暴jvm内存的 ,加上这个handler我们就不用考虑这个问题了
//                            pipeline.addLast(new ChunkedWriteHandler());//Inbound、Outbound
//                            pipeline.addLast(new SimpleHttpServerHandler());

//这个是websocket的handler，是netty提供的，也可以自定义，建议就用默认的
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello",null,true,65535));//in
                            //自定义的handler，处理服务端传来的消息
                            pipeline.addLast(new WebSocketHandler());//in
//
//                            pipeline.addLast(new HttpResponseEncoder());





                        }
                    });
            ChannelFuture f = b.bind(this.port).sync();
            log.info("服务已启动,监听端口" + this.port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    public void start() {
        start(this.port);
    }


    public static void main(String[] args) throws IOException {
        if(args.length > 0) {
            new ServerMain().start(Integer.valueOf(args[0]));
        }else{
            new ServerMain().start();
        }
    }

}
