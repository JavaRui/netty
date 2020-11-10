package com.crwu.tool.netty.client;

import com.crwu.tool.netty.client.left.LeftPart;
import com.crwu.tool.netty.client.right.RightPart;
import com.yt.tool.swt.base.SwtVoid;
import com.yt.tool.swt.base.YtComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Shell;

/**
 * @author wuchengrui
 * @Description: \\TODO
 * @date 2020/8/27 19:37
 */
public class NettyClient extends YtComposite {

    public static void main(String[] args) {
    //如果报thread access 异常，在vm option中添加-XstartOnFirstThread
        SwtVoid.createSwt(shell->{

//            GlobalSetting.addHandler("message",new ChatClientHandler());

            shell.setText("netty 调试工具 - ");
            shell.setSize(800,800);
            new NettyClient(shell,0);
        });
    }

//    1.先实现一套handler的处理逻辑：
//    可以用来处理，单个handler的对应关系
//    客户端，可以自动切换handler
//    服务端，可以不断叠加handler
//2.实现了多种handler的处理之后
//            接入到client中

//    DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.toASCIIString(), Unpooled.wrappedBuffer(content.getBytes("UTF-8")));
    public NettyClient(Shell shell, int i) {
        super(shell,i);
        setFillLayout();
        SashForm sashForm = new SashForm(this,0);
        LeftPart leftPart = new LeftPart(sashForm, SWT.BORDER);
        RightPart rightPart = new RightPart(sashForm, SWT.BORDER);
        sashForm.setWeights(new int[]{30,70});
    }


}
