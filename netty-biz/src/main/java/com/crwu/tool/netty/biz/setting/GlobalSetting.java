package com.crwu.tool.netty.biz.setting;

import com.crwu.tool.netty.biz.handler.im.ChatClientHandler;
import com.crwu.tool.netty.biz.vo.CreateClientVo;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wuchengrui
 * @Description: 全局变量
 * @date 2020/10/22 18:52
 */
public class GlobalSetting {


    public static CreateClientVo createClientVo;

    public static CreateClientVo getCreateClientVo() {
        return createClientVo;
    }

    public static void setCreateClientVo(CreateClientVo createClientVo) {
        GlobalSetting.createClientVo = createClientVo;
    }

    private static NioSocketChannel nioSocketChannel;

    public static void setNioSocketChannel(NioSocketChannel nioSocketChannel){
        GlobalSetting.nioSocketChannel = nioSocketChannel;
    }

    public static NioSocketChannel getNioSocketChannel(){
        return nioSocketChannel;
    }



    private static Map<String,ChatClientHandler> inboundHandlerMap = new HashMap<>();

    public static void addHandler(String key , ChatClientHandler handler){
        inboundHandlerMap.put(key,handler);
    }

    public static  Map<String,ChatClientHandler> getInboundHandlerMap(){
        return inboundHandlerMap;
    }






}
