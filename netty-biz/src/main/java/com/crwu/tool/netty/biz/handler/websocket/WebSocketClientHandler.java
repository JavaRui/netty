package com.crwu.tool.netty.biz.handler.websocket;

import com.alibaba.fastjson.JSONObject;
import com.crwu.tool.netty.biz.handler.BaseClientHandler;
import com.crwu.tool.netty.biz.protocol.IMMessage;
import com.crwu.tool.netty.biz.protocol.IMP;
import com.crwu.tool.netty.biz.receiver.ReceiverMgr;
import com.crwu.tool.netty.biz.setting.GlobalSetting;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wuchengrui
 * @Description: \\TODO
 * @date 2020/11/1 02:09
 */
    //因为websocket比较特殊，他是由http升级上来的，所以，第一次的时候是会有一个fullHttpResponse，第二次才是websocketFrame
    //所以会有channelRead0和channelRead1的方法的区别
//如果不想写channelRead0和channelRead1的话，可以使用BaseClientHandler<Object>前期最好就指定object，这样在channelRead会有代码自动判断

public class WebSocketClientHandler extends BaseClientHandler<TextWebSocketFrame> {

    //握手的状态信息
    WebSocketClientHandshaker handshaker;
    //netty自带的异步处理
    ChannelPromise handshakeFuture;
    public static ExecutorService executor =  Executors.newFixedThreadPool(10);
    private URI uri;
    public WebSocketClientHandler(String uri){
        try {
            this.uri = new URI(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean release = true;
        boolean autoRelease = false;
        try {
            if (acceptInboundMessage(msg)) {

                if(msg instanceof TextWebSocketFrame) {
                    channelRead0(ctx, (TextWebSocketFrame) msg);
                }else{
                    channelRead1(ctx, (FullHttpResponse) msg);
                }
            } else {
                release = false;
                ctx.fireChannelRead(msg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (autoRelease && release) {
            ReferenceCountUtil.release(msg);
        }
    }



    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        return super.acceptInboundMessage(msg)||(msg instanceof FullHttpResponse);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg)  {
        //接收服务端的消息
        WebSocketFrame frame = (WebSocketFrame)msg;
        System.out.println("frame---->    "+frame);
        //文本信息
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame)frame;
            String text = textFrame.text();
            JSONObject jsonObject = (JSONObject) JSONObject.parse(text);

            IMMessage imMessage = JSONObject.toJavaObject(jsonObject, IMMessage.class);

            ReceiverMgr.addMsg(imMessage);
//                System.out.println("客户端接收的消息是:    "+textFrame.text());
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
            ctx.channel().close();
        }
    }

    public void channelRead1(ChannelHandlerContext ctx, FullHttpResponse msg)  {
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

            handshaker = WebSocketClientHandshakerFactory.newHandshaker(uri,WebSocketVersion.V13, (String) null, true, httpHeaders);
            handshaker.handshake(channel);
            //阻塞等待是否握手成功
            try {
                handshakeFuture.sync();
                //给服务端发送的内容，如果客户端与服务端连接成功后，可以多次掉用这个方法发送消息
                for(int i = 0 ; i<10 ;i++){
//                    sendMsg("hello    "+i);
//                    Thread.sleep(5000);
                }

                System.out.println("握手成功,可以开发发送了------------");

                IMMessage imMessage = new IMMessage();
                imMessage.setSender(GlobalSetting.getCreateClientVo().getNickName());
                imMessage.setCmd(IMP.LOGIN.getName());
                sendMsg(imMessage);

            }catch ( Exception e){
                e.printStackTrace();
            }
        });
        super.channelActive(ctx);
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
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)  {
        System.out.println("连接异常："+cause.getMessage());
        ctx.close();
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

    @Override
    public  boolean sendMsg(Object msg){
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(msg);
        //发送的内容，是一个文本格式的内容
//        String putMessage="你好，我是客户端:  "+RandomUtil.randomString(3);
        TextWebSocketFrame frame = new TextWebSocketFrame(jsonObject.toString());
        getCtx().channel().writeAndFlush(frame).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    System.out.println("消息发送成功，发送的消息是："+jsonObject.toJSONString());
                } else {
                    System.out.println("消息发送失败 " + channelFuture.cause().getMessage());
                }
            }
        });
        return true;
    }

    @Override
    public Object packMsg(Object msg) {

        return  new TextWebSocketFrame(msg.toString());
    }
}
