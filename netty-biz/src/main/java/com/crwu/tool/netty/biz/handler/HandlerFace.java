package com.crwu.tool.netty.biz.handler;

import java.io.IOException;

/**
 * @author wuchengrui
 * @Description: 基础的发送接口
 * @date 2020/10/31 23:06
 */
public interface HandlerFace {

    /**
     * 发送消息
     * @return
     * @throws IOException
     */
    boolean sendMsg(Object object);

    /**
     * 包装消息体
     * */
    Object packMsg(Object msg);

}
