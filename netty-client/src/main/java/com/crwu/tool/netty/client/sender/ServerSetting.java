package com.crwu.tool.netty.client.sender;

import cn.hutool.core.util.RandomUtil;
import com.crwu.tool.netty.biz.client.ChatClient;
import com.crwu.tool.netty.biz.setting.GlobalSetting;
import com.crwu.tool.netty.biz.vo.CreateClientVo;
import com.crwu.tool.netty.biz.vo.Protocal;
import com.yt.tool.swt.base.YtComposite;
import com.yt.tool.swt.ui.text.YtText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author wuchengrui
 * @Description: \\TODO
 * @date 2020/8/27 19:43
 */
public class ServerSetting extends YtComposite {

    private YtText ipText;
    private YtText portText;
    private YtText nickNameText;
    private YtText webSocketText;
    private Button protacolWebBtn;
    private Button protocalChatBtn;


    private Combo combo;

    public ServerSetting(Composite parent, int style) {
        super(parent, style);

        Group group = new Group(parent,0);
        group.setText("服务端设置");
        group.setLayout(new FillLayout());

        setParent(group);

        setGridLayout(2,false);
        init();
    }

    private void init(){
        createLabel("目标ip：");
        ipText = new YtText(this, SWT.BORDER);
        ipText.setGdFill(true,false);
        ipText.setText("127.0.0.1");

        createLabel("目标端口：");
        portText = new YtText(this, SWT.BORDER);
        portText.setGdFill(true,false);
        portText.setText("10080");

        createLabel("用户名：");
        nickNameText = new YtText(this, SWT.BORDER);
        nickNameText.setGdFill(true,false);
        nickNameText.setText("cr-"+RandomUtil.randomString(3));

        createLabel("webSocketPath：");
        webSocketText = new YtText(this, SWT.BORDER);
        webSocketText.setGdFill(true,false);
        webSocketText.setText("hello");


        protacolWebBtn = new Button(this,SWT.RADIO);
        protacolWebBtn.setText("web");
        protacolWebBtn.setSelection(true);

        protocalChatBtn = new Button(this,SWT.RADIO);
        protocalChatBtn.setText("chat");


        Button connectBtn = new Button(this,SWT.PUSH);
        connectBtn.setText("连接");

        connectBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String ip = ipText.getText();
                Integer integer = Integer.valueOf(portText.getText());
                String text = nickNameText.getText();

                String socketPath = "ws://"+ip+":"+integer+"/"+webSocketText.getText();

                CreateClientVo createClientVo = new CreateClientVo();
                createClientVo.setIp(ip);
                createClientVo.setPort(portText.getText());
                createClientVo.setNickName(text);
//                createClientVo.setProtocal(Protocal.chat);
                createClientVo.setWebSocketPath(socketPath);
                if(protacolWebBtn.getSelection()){
                    createClientVo.setProtocal(Protocal.websocket);
                }else if(protocalChatBtn.getSelection()){
                    createClientVo.setProtocal(Protocal.chat);
                }

                super.widgetSelected(e);
                new Thread(()->{
                    new ChatClient(createClientVo).connect(ip, integer);

                }).start();
            }
        });


        Button closeBtn = new Button(this,SWT.PUSH);
        closeBtn.setText("关闭");
        closeBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
//                GlobalSetting.getNioSocketChannel().close();
                GlobalSetting.getNioSocketChannel().shutdown();
            }
        });



    }




}
