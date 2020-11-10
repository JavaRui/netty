package com.crwu.tool.netty.biz.vo;

import lombok.Data;

/**
 * @author wuchengrui
 * @Description: \\TODO
 * @date 2020/11/4 09:32
 */
@Data
public class CreateClientVo {

    private String ip;
    private int port ;
    private String nickName;
    private String webSocketPath;
    /**
     * 协议名称
     * */
    private Protocal protocal;

    public void setPort(String pp){
        port = Integer.valueOf(pp);
    }

    public void setPort(int port){
        this.port = port;
    }

}
