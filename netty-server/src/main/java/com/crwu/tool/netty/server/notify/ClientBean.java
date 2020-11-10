package com.crwu.tool.netty.server.notify;

import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;

/**
 * @author wuchengrui
 * @Description: \\TODO
 * @date 2020/10/22 19:13
 */
@Data
public class ClientBean {

    private String nickName;
    private String channelId;
    private NioSocketChannel nioSocketChannel;




}
