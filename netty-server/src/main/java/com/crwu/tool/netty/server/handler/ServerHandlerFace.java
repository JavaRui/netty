package com.crwu.tool.netty.server.handler;

/**
 * @author wuchengrui
 * @Description: \\TODO
 * @date 2020/11/4 16:05
 */
public interface ServerHandlerFace {

    /**
     * 包装消息体
     * */
    default  Object packMsg(Object msg){
        return msg;
    }

}
