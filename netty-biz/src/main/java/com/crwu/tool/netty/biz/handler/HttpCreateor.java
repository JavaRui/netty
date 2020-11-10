package com.crwu.tool.netty.biz.handler;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.net.URI;

/**
 * @author wuchengrui
 * @Description: \\TODO
 * @date 2020/10/29 20:28
 */
public class HttpCreateor {

    /**
     * 构造HTTP请求
     * @return
     * @throws Exception
     */
    public static HttpRequest createReqGet(String server, URI uri) throws Exception{
        String req = "";
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
                uri.toASCIIString(), Unpooled.wrappedBuffer(req.getBytes("UTF-8")));
        // 构建HTTP请求
        request.headers().set(HttpHeaders.Names.HOST, server);
        request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        request.headers().set("accept-type", "UTF-8");
        request.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
        //    request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());
        // 返回
        return request;
    }

    public static HttpRequest createReqPost(byte [] body, String server, URI uri) throws Exception{

        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
                uri.toASCIIString(), Unpooled.wrappedBuffer(body));
        // 构建HTTP请求
        request.headers().set(HttpHeaders.Names.HOST, server);
        request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        request.headers().set("accept-type", "UTF-8");
        request.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
        request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());
        // 返回
        return request;
    }
}
