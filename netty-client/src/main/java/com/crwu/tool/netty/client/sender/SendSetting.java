package com.crwu.tool.netty.client.sender;

import com.yt.tool.swt.base.YtComposite;
import com.yt.tool.swt.ui.text.YtText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author wuchengrui
 * @Description: \\TODO
 * @date 2020/8/27 19:56
 */
public class SendSetting extends YtComposite {

    public SendSetting(Composite parent, int style) {
        super(parent, style);

        Group group = new Group(parent,0);
        group.setText("发送区设置");
        group.setLayout(new FillLayout());

        setParent(group);

        setGridLayout(2,false);
        init();
    }

    private void init(){
        createLabel("并发数量：");
        YtText concurrentText = new YtText(this, SWT.BORDER);
        concurrentText.setGdFill(true,false);

        Button autoAnswer = new Button(this,SWT.CHECK);
        autoAnswer.setText("自动应答");
        createLabel("");

        createLabel("自动应答间隔：");
        YtText autoTimeText = new YtText(this, SWT.BORDER);
        autoTimeText.setGdFill(true,false);
    }

}
