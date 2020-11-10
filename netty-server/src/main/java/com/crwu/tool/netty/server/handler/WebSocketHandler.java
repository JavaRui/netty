package com.crwu.tool.netty.server.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.crwu.tool.netty.biz.protocol.IMMessage;
import com.crwu.tool.netty.biz.protocol.IMP;
import com.crwu.tool.netty.server.notify.NotifyMgr;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 自定义的handler类
 */
@Slf4j
public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> implements ServerHandlerFace{

    //客户端组
    public  static ChannelGroup channelGroup;

    static {
        channelGroup=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }
    //存储ip和channel的容器
    private static ConcurrentMap<String, Channel> channelMap = new ConcurrentHashMap<>();



    /**
     * Handler活跃状态，表示连接成功
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("与客户端连接成功");
        channelGroup.add(ctx.channel());
//        super.channelActive(ctx);
    }

    /**
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        log.info("========   websocket.channelRead0   ==========");
        //文本消息
        if (msg instanceof TextWebSocketFrame) {
            String text = ((TextWebSocketFrame) msg).text();
            IMMessage imMessage = IMMessage.toJava(text);

            //TODO 消息体是一定要包装成TextWebSocketFrame的，不然发送不成功
            NotifyMgr.inst().send2All(ctx.channel(),this,imMessage);
//            sendMessageAll(imMessage);

            //获取当前channel绑定的IP地址
            InetSocketAddress ipSocket = (InetSocketAddress)ctx.channel().remoteAddress();
            String address = ipSocket.getAddress().getHostAddress();
            System.out.println("address为:"+address);
            //将IP和channel的关系保存
            if (!channelMap.containsKey(address)){
                channelMap.put(address,ctx.channel());
            }

        }
        //二进制消息
        if (msg instanceof BinaryWebSocketFrame) {
            System.out.println("收到二进制消息：" + ((BinaryWebSocketFrame) msg).content().readableBytes());
            BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(Unpooled.buffer().writeBytes("hello".getBytes()));
            //给客户端发送的消息
            ctx.channel().writeAndFlush(binaryWebSocketFrame);
        }
        //ping消息
        if (msg instanceof PongWebSocketFrame) {
            System.out.println("客户端ping成功");
        }
        //关闭消息
        if (msg instanceof CloseWebSocketFrame) {
            System.out.println("客户端关闭，通道关闭");
            Channel channel = ctx.channel();
            channel.close();
        }
    }

    /**
     * 未注册状态
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        IMMessage imMessage = new IMMessage();
        imMessage.setCmd(IMP.LOGOUT.getName());
        NotifyMgr.inst().send2All(ctx.channel(),this,imMessage);
        super.channelUnregistered(ctx);
    }

    /**
     * 非活跃状态，没有连接远程主机的时候。
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端关闭");
        channelGroup.remove(ctx.channel());
    }


    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("连接异常："+cause.getMessage());
        ctx.close();
    }


    /**
     * 给指定用户发内容
     * 后续可以掉这个方法推送消息给客户端
     */
    public void  sendMessage(String address){
        Channel channel=channelMap.get(address);
        String message="你好，这是指定消息发送";
        channel.writeAndFlush(new TextWebSocketFrame(message));
    }

    /**
     * 群发消息
     */
    public void  sendMessageAll(String text){
        String msg="收到信息:   "+text;
        log.info("收到信息---->"+msg);
        IMMessage imMessage = new IMMessage();
        imMessage.setContent(text);
        imMessage.setCmd(IMP.CHAT.getName());
        channelGroup.writeAndFlush(new TextWebSocketFrame(text));
    }

    /**
     * 群发消息
     */
    public void  sendMessageAll(IMMessage msg){
        log.info("收到信息---->"+msg);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(msg);
        channelGroup.writeAndFlush(new TextWebSocketFrame(jsonObject.toString()));
    }


    @Override
    public Object packMsg(Object msg) {
        IMMessage msg1 = (IMMessage) msg;
        JSONObject jsonObject = IMMessage.toJson(msg1);
        return new TextWebSocketFrame(jsonObject.toString());
    }


}
