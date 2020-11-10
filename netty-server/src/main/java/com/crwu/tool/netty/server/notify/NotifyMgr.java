package com.crwu.tool.netty.server.notify;

import com.crwu.tool.netty.biz.protocol.IMMessage;
import com.crwu.tool.netty.biz.protocol.IMP;
import com.crwu.tool.netty.server.handler.ServerHandlerFace;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wuchengrui
 * @Description: \\TODO
 * @date 2020/10/21 19:06
 */
@Slf4j
public class NotifyMgr {

    private ConcurrentHashMap<String,ClientBean> channelMap = new ConcurrentHashMap<>();

    private static NotifyMgr instance;

    private NotifyMgr(){

    }

    public static NotifyMgr inst(){
        if(instance == null){
            synchronized (NotifyMgr.class){
                if(instance == null){
                    instance = new NotifyMgr();
                }
            }
        }
        return instance;
    }

    public void addChannelBean(ClientBean client){
        String s = client.getNioSocketChannel().id().asShortText();
        channelMap.put(s,client);
    }

    public ClientBean getChannelBean(String channelShortText){
        return channelMap.get(channelShortText);
    }

    public ClientBean removeChannel(String channelShortText){
        ClientBean remove = channelMap.remove(channelShortText);
        return remove;
    }

    public void send2All(Channel channel , ServerHandlerFace serverHandlerFace , IMMessage msg){
        String s = channel.id().asShortText();
        if(msg.getCmd().equals(IMP.LOGIN.getName())) {

            msg.setContent("喜迎[ "+msg.getSender()+" ]上线");

            ClientBean clientBean = new ClientBean();
            clientBean.setChannelId(s);
            clientBean.setNickName(msg.getSender());
            clientBean.setNioSocketChannel((NioSocketChannel) channel);
            NotifyMgr.inst().addChannelBean(clientBean);
        }else if(msg.getCmd().equals(IMP.LOGOUT.getName())){

            ClientBean clientBean = NotifyMgr.inst().removeChannel(s);
            clientBean.getNioSocketChannel().close();
            msg.setContent("欢送 [ "+clientBean.getNickName()+" ] 下线!!!");
        }
        send2All(serverHandlerFace ,msg);
    }

    public void send2All(ServerHandlerFace serverHandlerFace, IMMessage msg){

        Collection<ClientBean> values = channelMap.values();
        values.forEach(ch -> {
            ChannelFuture channelFuture = ch.getNioSocketChannel().writeAndFlush(serverHandlerFace.packMsg(msg));
            log.info("发送给了id--->"+ch.getNioSocketChannel().id().asShortText());
        });
    }








}
