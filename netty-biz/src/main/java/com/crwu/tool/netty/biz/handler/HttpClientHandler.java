package com.crwu.tool.netty.biz.handler;

import com.crwu.tool.netty.biz.protocol.IMMessage;
import com.crwu.tool.netty.biz.protocol.IMP;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class HttpClientHandler extends BaseClientHandler<IMMessage> implements HandlerFace{


    /**
     * 发送消息
     * @return
     * @throws IOException
     */
    @Override
    public boolean sendMsg(Object obj){
        IMMessage msg = (IMMessage) obj;
        DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,HttpMethod.GET,"/hello/"+msg.getContent());

        getCtx().channel().writeAndFlush(httpRequest);
        System.out.println("继续输入开始对话...");
        return msg.getCmd().equals(IMP.LOGOUT) ? false : true;
    }
    /**
     * 收到消息后调用
     * @throws IOException
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, IMMessage msg) throws IOException {
        IMMessage m = (IMMessage)msg;
        System.out.println((null == m.getSender() ? "" : (m.getSender() + ":")) + removeHtmlTag(m.getContent()));
    }


    public static String removeHtmlTag(String htmlStr){
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式

        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
        Matcher m_script=p_script.matcher(htmlStr);
        htmlStr=m_script.replaceAll(""); //过滤script标签

        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
        Matcher m_style=p_style.matcher(htmlStr);
        htmlStr=m_style.replaceAll(""); //过滤style标签

        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
        Matcher m_html=p_html.matcher(htmlStr);
        htmlStr=m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }

}
