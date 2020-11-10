package com.crwu.tool.netty.biz.receiver;

import com.crwu.tool.netty.biz.protocol.IMMessage;

/**
 * @author wuchengrui
 * @Description: 消息接收的管理者
 * @date 2020/10/21 20:04
 */
public class ReceiverMgr {

    public static ReceiverFace receiverFace;

    public static void setReceiver(ReceiverFace receiverFace){
        ReceiverMgr.receiverFace =receiverFace;
    }

    public static ReceiverFace getReceiverFace(){
        return receiverFace;
    }

    public static void addMsg(IMMessage msg){
        receiverFace.receiverMsg(msg);
    }




}
