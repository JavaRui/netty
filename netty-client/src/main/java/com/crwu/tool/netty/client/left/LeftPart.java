package com.crwu.tool.netty.client.left;

import com.crwu.tool.netty.client.sender.SendSetting;
import com.crwu.tool.netty.client.sender.ServerSetting;
import com.yt.tool.swt.base.YtComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author wuchengrui
 * @Description: \\TODO
 * @date 2020/8/27 19:41
 */
public class LeftPart extends YtComposite {

    public LeftPart(Composite parent, int style) {
        super(parent, style);

        init();
    }

    private void init(){
        ServerSetting serverSetting = new ServerSetting(this,0);
        setLayout(new FillLayout(SWT.VERTICAL));

        SendSetting sendSetting = new SendSetting(this,0);

    }

}
