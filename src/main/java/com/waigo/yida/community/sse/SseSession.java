package com.waigo.yida.community.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * author waigo
 * create 2021-10-08 21:21
 */
public class SseSession {
    private static final Logger logger = LoggerFactory.getLogger(SseSession.class);
    /**
     * 维护session的map,key为sessionId
     */
    public final static  Map<String, SseEmitter> SESSION = new ConcurrentHashMap<>();

    /**
     * 判断session是否存在
     * @param sessionId
     * @return
     */
    public static boolean exist(String sessionId){
        return SESSION.get(sessionId)!=null;
    }
    public static void add(String sessionId,SseEmitter sseEmitter){
        SESSION.put(sessionId,sseEmitter);
    }
    public static boolean del(String sessionId){
        final SseEmitter emitter = SESSION.remove(sessionId);
        if(emitter!=null){
            emitter.complete();
            return true;
        }
        return false;
    }

    /**
     * 发送data:数据
     * @param sessionId
     * @return
     */
    public static boolean send(String sessionId,SseEmitter.SseEventBuilder sseEventBuilder){
        final SseEmitter emitter = SESSION.get(sessionId);
        if(emitter!=null){
            try {
                emitter.send(sseEventBuilder);
                return true;
            } catch (IOException e) {
                logger.error("MSG: SendMessageError-IOException | ID: " + sessionId + " | Date: " +
                        new Date() + " |", e.getMessage());
                emitter.completeWithError(e);
            }
        }
        return false;
    }
    public static boolean retry(String sessionId, int time, TimeUnit timeUnit){
        SseEmitter.SseEventBuilder retryBuilder = SseEmitter.event().reconnectTime(timeUnit.toMillis(time));
        return send(sessionId,retryBuilder);
    }
    public static boolean sendData(String sessionId,String msg){
        SseEmitter.SseEventBuilder data = SseEmitter.event().data(msg);
//        LAST_MSG_TIME.put(sessionId,System.currentTimeMillis());
        return send(sessionId,data);
    }

    public static void onError(String id, SseException e) {
        SseEmitter sseEmitter = SESSION.get(id);
        if(sseEmitter!=null){
            sseEmitter.completeWithError(e);
        }
    }

    public static void onTimeOut(String clientId) {
        //10s之内发过，就立刻重连
        //40s内发过，就30s重连
        //5分钟内发过，就1分钟重连
        /*Long lastSendData = LAST_MSG_TIME.get(clientId);
        long gap = System.currentTimeMillis()-(lastSendData ==null?0:lastSendData);
        if(TimeUnit.MILLISECONDS.toSeconds(gap)<10){
            retry(clientId,0,TimeUnit.SECONDS);
        }else if(TimeUnit.MILLISECONDS.toSeconds(gap)<40){
            retry(clientId,30,TimeUnit.SECONDS);
        }else{
            retry(clientId,1,TimeUnit.MINUTES);
        }*/

    }

    public static boolean sentComment(String clientId, String heartbeats) {
        SseEmitter.SseEventBuilder comment = SseEmitter.event().comment(heartbeats);
        return send(clientId,comment);
    }

    public static boolean event(String event, String clientId, String data) {
        SseEmitter.SseEventBuilder dataBuilder = SseEmitter.event().name(event).data(data);
        return send(clientId,dataBuilder);

    }
}
