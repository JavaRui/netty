package com.crwu.tool.netty.client.right;

import com.crwu.tool.netty.biz.handler.HandlerMgr;
import com.crwu.tool.netty.biz.setting.GlobalSetting;
import com.yt.tool.swt.base.YtComposite;
import com.yt.tool.swt.ui.text.YtText;
import com.yt.tool.swt.util.LayoutUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import java.util.Set;

/**
 * @author wuchengrui
 * @Description: \\TODO
 * @date 2020/9/15 20:04
 */
public class SendPart extends YtComposite {

    YtText sendText;
    Button sendBtn;
    Combo combo;


    public SendPart(Composite parent, int style) {
        super(parent, style);
        init();
    }

    private void init() {

        sendText = new YtText(this);
        sendText.setLayoutData(LayoutUtil.createFillGrid(1));


        setGridLayout(1,false);

        YtComposite btnComp = new YtComposite(this);
        btnComp.setGd(true,false);

        sendBtn = new Button(btnComp,SWT.PUSH);
        sendBtn.setText("发送咯");
        sendBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                HandlerMgr.sendMsg(sendText.getText());
//                HttpClientHandler.addLinked(sendText.getText());
            }
        });


        Set<String> strings = GlobalSetting.getInboundHandlerMap().keySet();

        combo = new Combo(btnComp,SWT.READ_ONLY);
        combo.add("");
        strings.forEach(key->{
            combo.add(key);
        });
        btnComp.setGridLayoutByChildren();


    }
}
