package com.crwu.tool.netty.biz.handler.im;

import com.crwu.tool.netty.biz.protocol.IMMessage;
import com.crwu.tool.netty.biz.protocol.IMP;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.MessagePack;
import org.msgpack.MessageTypeException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义IM协议的编码器
 */
@Slf4j
public class IMDecoder extends ByteToMessageDecoder {

	//解析IM写一下请求内容的正则
	private Pattern pattern = Pattern.compile("^\\[(.*)\\](\\s\\-\\s(.*))?");
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,List<Object> out) throws Exception {

		try{
			//先获取可读字节数
	        final int length = in.readableBytes();
	        final byte[] array = new byte[length];
	        String content = new String(array,in.readerIndex(),length);
	        
	        //空消息不解析
	        if(!(null == content || "".equals(content.trim()))){
	        	if(!IMP.isIMP(content)){
//	        		ctx.channel().pipeline().remove(this);
	        		return;
	        	}
	        }
	        
	        in.getBytes(in.readerIndex(), array, 0, length);
	        out.add(new MessagePack().read(array,IMMessage.class));
	        in.clear();
		}catch(MessageTypeException e){
//		    e.printStackTrace();
            log.info("不符合解码规则，报错了"+e.getMessage());
            //一定要移除，如果不移除，out没有数据，没有数据就不会调用fireChannelRead，这样程序就走不下去了
            //移除之后，会走ByteToMessageDecoder.handlerRemoved方法。
			ctx.channel().pipeline().remove(this);
//            ctx.channel().pipeline().fireChannelRead(in);
		}
	}

    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception {
        log.info("进来了=============");
        //当有不适合的handler的时候，就应该remove掉，这样，下次进来就不会走这个handler了
        super.channelRead(ctx,msg);
//        if(ctx.isRemoved()){
//            log.info("isRemoved   已经跳过了？？？？？？直接下一个handler");
//        }else if(!(msg instanceof ByteBuf)){
//            log.info("not ByteBuf   已经跳过了？？？？？？直接下一个handler");
//
//        }

    }

	/**
	 * 字符串解析成自定义即时通信协议
	 * @param msg
	 * @return
	 */
	public IMMessage decode(String msg){
		if(null == msg || "".equals(msg.trim())){ return null; }
		try{
			Matcher m = pattern.matcher(msg);
			String header = "";
			String content = "";
			if(m.matches()){
				header = m.group(1);
				content = m.group(3);
			}
			
			String [] heards = header.split("\\]\\[");
			long time = 0;
			try{ time = Long.parseLong(heards[1]); } catch(Exception e){}
			String nickName = heards[2];
			//昵称最多十个字
			nickName = nickName.length() < 10 ? nickName : nickName.substring(0, 9);
			
			if(msg.startsWith("[" + IMP.LOGIN.getName() + "]")){
				return new IMMessage(heards[0],heards[3],time,nickName);
			}else if(msg.startsWith("[" + IMP.CHAT.getName() + "]")){
				return new IMMessage(heards[0],time,nickName,content);
			}else if(msg.startsWith("[" + IMP.FLOWER.getName() + "]")){
				return new IMMessage(heards[0],heards[3],time,nickName);
			}else{
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
