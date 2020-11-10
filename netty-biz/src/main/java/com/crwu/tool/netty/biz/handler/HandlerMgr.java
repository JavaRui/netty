package com.crwu.tool.netty.biz.handler;

import com.crwu.tool.netty.biz.protocol.IMMessage;
import com.crwu.tool.netty.biz.protocol.IMP;
import com.crwu.tool.netty.biz.setting.GlobalSetting;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * @author wuchengrui
 * @Description: \\TODO
 * @date 2020/10/31 23:10
 */
public class HandlerMgr {


    public static HandlerFace handlerFace;
    private static boolean initFlag = false;

    public static ExecutorService executor =     Executors.newFixedThreadPool(10);



    public static void setHandlerFace(HandlerFace handlerFace){
        HandlerMgr.handlerFace =handlerFace;
    }

    public static HandlerFace getHandlerFace(){
        return handlerFace;
    }

    public static void sendMsg(IMMessage msg){
        handlerFace.sendMsg(msg);
    }
    public static void sendMsg(String text){
        IMMessage msg = new IMMessage();
        msg.setSender(GlobalSetting.createClientVo.getNickName());
        msg.setContent(text);
        msg.setCmd(IMP.CHAT.getName());
        sendMsg(msg);
    }

    private static LinkedBlockingQueue<String> queue = new LinkedBlockingQueue();

    public static void addLinked(String content){
        queue.add(content);
    }

    public static void init() {
        try{
            if(initFlag){
                return ;
            }
            initConsole();
            initQuenue();
            initFlag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**启动客户端控制台*/
    private static void initQuenue() throws IOException {
        if(initFlag){
            return ;
        }
        initFlag = true;

        executor.execute(()->{

            IMMessage message = null;

            while(true){
                try {
                    if(queue.isEmpty()){
                        Thread.sleep(10);
                        continue;
                    }

                    String input = queue.poll();

                    message = new IMMessage(IMP.CHAT.getName(),System.currentTimeMillis(),GlobalSetting.createClientVo.getNickName(),input);

                    if(handlerFace !=null) {

                        handlerFace.sendMsg(message);
                    }
                    if(queue.isEmpty()){
                        Thread.sleep(10);
                        continue;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

    }


    /**启动客户端控制台*/
    public static void initConsole() throws IOException {
        if(initFlag){
            return ;
        }
        initFlag = true;
        executor.execute(()->{
            IMMessage message = null;
            Scanner scanner = new Scanner(System.in);
            do{
                if(scanner.hasNext()){
                    String input = scanner.nextLine();
                    System.out.println(GlobalSetting.createClientVo.getNickName() + ",你好，请在控制台输入对话内容");
                    if(input.equals("exit")){
                        message = new IMMessage(IMP.LOGOUT.getName(),"Console",System.currentTimeMillis(),GlobalSetting.createClientVo.getNickName());
                    }else{
                        message = new IMMessage(IMP.CHAT.getName(),"Console",System.currentTimeMillis(),GlobalSetting.createClientVo.getNickName());
                        message.setContent(input);

                    }
                }
            }
            while (handlerFace.sendMsg(message));
            scanner.close();
//            System.exit(0);
        });
    }

}
