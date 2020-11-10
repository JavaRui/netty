package com.crwu.tool.netty.biz.client;

import com.crwu.tool.netty.biz.handler.im.ChatClientHandler;
import com.crwu.tool.netty.biz.handler.im.IMDecoder;
import com.crwu.tool.netty.biz.handler.im.IMEncoder;
import com.crwu.tool.netty.biz.handler.websocket.WebSocketClientHandler;
import com.crwu.tool.netty.biz.setting.GlobalSetting;
import com.crwu.tool.netty.biz.vo.CreateClientVo;
import com.crwu.tool.netty.biz.vo.Protocal;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 客户端
 * @author Tom
 *
 */
@Slf4j
public class ChatClient {

    private String host;
    private int port;


    public ChatClient(CreateClientVo createClientVo){
        GlobalSetting.setCreateClientVo(createClientVo);
    }

    public void connect(){
        connect(GlobalSetting.getCreateClientVo().getIp(),GlobalSetting.getCreateClientVo().getPort());
    }

    public void connect(String host,int port){
    		this.host = host;
    		this.port = port;

        EventLoopGroup workerGroup = new NioEventLoopGroup(50);
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.option(ChannelOption.TCP_NODELAY, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();



                    if(GlobalSetting.getCreateClientVo().getProtocal().equals(Protocal.chat)){
                        log.info("====   初始化的是chat  =======");

                        ch.pipeline().addLast(new IMDecoder());//in
                        ch.pipeline().addLast(new IMEncoder());//out
                        ch.pipeline().addLast(new ChatClientHandler());//in
                    }

                    if(GlobalSetting.getCreateClientVo().getProtocal().equals(Protocal.websocket)){
                        log.info("====   初始化的是websocket  =======");
                        pipeline.addLast(new HttpClientCodec());
                        pipeline.addLast(new HttpObjectAggregator(65536));
//                    ch.pipeline().addLast(new HttpContentDecompressor());
//                    ch.pipeline().addLast(new HttpClientHandler("cr"));
                        pipeline.addLast("hookedHandler", new WebSocketClientHandler(GlobalSetting.getCreateClientVo().getWebSocketPath()));


                    }

                }
            });
            ChannelFuture connect = b.connect(this.host, this.port);
            GlobalSetting.setNioSocketChannel((NioSocketChannel) connect.channel());
            ChannelFuture f = connect.sync();
            System.out.println("EchoClient.main ServerBootstrap配置启动完成");
            f.channel().closeFuture().sync();
            System.out.println("EchoClient.end");

//            b.option(ChannelOption.SO_KEEPALIVE, true)
//                    .option(ChannelOption.TCP_NODELAY, true)
//                    .group(workerGroup)
//                    .handler(new LoggingHandler(LogLevel.INFO))
//                    .channel(NioSocketChannel.class)
//                    .handler(new ChannelInitializer<SocketChannel>() {
//                        protected void initChannel(SocketChannel socketChannel) throws Exception {
//                            ChannelPipeline pipeline = socketChannel.pipeline();
//                            pipeline.addLast("http-codec",new HttpClientCodec());
//                            pipeline.addLast("aggregator",new HttpObjectAggregator(1024*1024*10));
//                            pipeline.addLast("hookedHandler", new WebSocketClientHandler(GlobalSetting.getCreateClientVo().getWebSocketPath()));
//                        }
//                    });
//            final Channel channel = b.connect(host, port).sync().channel();//进行握手
//            System.out.println("EchoClient.main ServerBootstrap配置启动完成");
//            channel.closeFuture().sync();
//            System.out.println("EchoClient.end");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
    
    
    public static void main(String[] args) throws IOException{

        CreateClientVo createClientVo = new CreateClientVo();
        createClientVo.setIp("127.0.0.1");
        createClientVo.setPort(10080);
        createClientVo.setNickName("吴程锐");
        createClientVo.setProtocal(Protocal.chat);
        createClientVo.setWebSocketPath("hello");

		new ChatClient(createClientVo).connect();
		
    }
    
}
