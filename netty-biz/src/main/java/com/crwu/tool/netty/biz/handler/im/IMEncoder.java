package com.crwu.tool.netty.biz.handler.im;

import com.crwu.tool.netty.biz.protocol.IMMessage;
import com.crwu.tool.netty.biz.protocol.IMP;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.MessagePack;

/**
 * 自定义IM协议的编码器
 */
@Slf4j
public class IMEncoder extends MessageToByteEncoder<IMMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, IMMessage msg, ByteBuf out)
			throws Exception {
	    log.info("encode-----------");
		out.writeBytes(new MessagePack().write(msg));
	}


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        log.info("write-----------");
        super.write(ctx,msg,promise);


    }

	
	public String encode(IMMessage msg){
		if(null == msg){ return ""; }
		String prex = "[" + msg.getCmd() + "]" + "[" + msg.getTime() + "]";
		if(IMP.LOGIN.getName().equals(msg.getCmd()) ||
			IMP.FLOWER.getName().equals(msg.getCmd())){
			prex += ("[" + msg.getSender() + "][" + msg.getTerminal() + "]");
		}else if(IMP.CHAT.getName().equals(msg.getCmd())){
			prex += ("[" + msg.getSender() + "]");
		}else if(IMP.SYSTEM.getName().equals(msg.getCmd())){
			prex += ("[" + msg.getOnline() + "]");
		}
		if(!(null == msg.getContent() || "".equals(msg.getContent()))){
			prex += (" - " + msg.getContent());
		}
		return prex;
	}

}
