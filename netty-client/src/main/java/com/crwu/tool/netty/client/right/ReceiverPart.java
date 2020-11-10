package com.crwu.tool.netty.client.right;

import com.crwu.tool.netty.biz.protocol.IMMessage;
import com.crwu.tool.netty.biz.protocol.IMP;
import com.crwu.tool.netty.biz.receiver.ReceiverFace;
import com.crwu.tool.netty.biz.receiver.ReceiverMgr;
import com.crwu.tool.netty.biz.setting.GlobalSetting;
import com.yt.tool.swt.base.SwtVoid;
import com.yt.tool.swt.base.YtComposite;
import com.yt.tool.swt.ui.text.YtText;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.eclipse.swt.widgets.Composite;

/**
 * @author wuchengrui
 * @Description: 接收消息的面板
 * @date 2020/9/15 20:16
 */
public class ReceiverPart extends YtComposite implements ReceiverFace {

    private YtText receiverText;

    public ReceiverPart(Composite parent, int style) {
        super(parent, style);
        init();

    }

    private void init(){
        receiverText = new YtText(this);
        receiverText.setGdFill(true,true);

        setGridLayout();

        ReceiverMgr.setReceiver(this);
    }

    @Override
    public void receiverMsg(IMMessage msg) {
        SwtVoid.delayAsy(0,()->{

            analysis(msg);


        });
    }

    private void analysis(IMMessage msg) {


        if(IMP.LOGIN.getName().equals(msg.getCmd())){
            //登录
            analysisLogin(msg);
        } else if(IMP.LOGOUT.getName().equals(msg.getCmd())){
            analysisLogout(msg);
        }else{
            analysisChat(msg);
        }


    }

    private void analysisLogout(IMMessage msg) {

        receiverText.append(msg.getContent()+"\n");
    }

    private void analysisLogin(IMMessage msg){
        String say = "喜迎 [ %s ] 登录！！！！";
        say= String.format(say,msg.getSender());
        receiverText.append(say+"\n");

    }

    private void analysisChat(IMMessage msg){
        String say = "%s 说：  %s";
        if(msg.getSender().equals(GlobalSetting.getCreateClientVo().getNickName())){
            say= String.format(say,"你",msg.getContent());
        }else{
            say = String.format(say,msg.getSender(),msg.getContent());
        }

        receiverText.append(say+"\n");
    }


    @Override
    public void setChannel(NioSocketChannel nioSocketChannel) {
        GlobalSetting.setNioSocketChannel(nioSocketChannel);
    }
}
