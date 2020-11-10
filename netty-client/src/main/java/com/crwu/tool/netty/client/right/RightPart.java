package com.crwu.tool.netty.client.right;

import com.yt.tool.swt.base.YtComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;

/**
 * @author wuchengrui
 * @Description: \\TODO
 * @date 2020/8/27 19:41
 */
public class RightPart extends YtComposite {

    SendPart sendPart;
    ReceiverPart receiverPart;


    public RightPart(Composite parent, int style) {
        super(parent, style);

        setFillLayout();
//
        SashForm sashForm = new SashForm(this,SWT.VERTICAL);
        receiverPart = new ReceiverPart(sashForm,SWT.BORDER);
        sendPart = new SendPart(sashForm,SWT.BORDER);
        sashForm.setWeights(new int[]{70,30});



    }
}
