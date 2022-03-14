package com.waigo.yida.community.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HeartBeatTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatTask.class);
    private final String clientId;
    //当前心跳的任务
    private ScheduledFuture<?> future;
    //当前针对哪个连接做心跳任务，如果不是的话得停止任务的
    private final SseEmitter emitter;

    public HeartBeatTask(String clientId,SseEmitter emitter) {
        // 这里可以按照业务传入需要的数据
        this.clientId = clientId;
        this.emitter = emitter;
    }
    private AtomicInteger heartBeatsCount = new AtomicInteger(5);
    @Override
    public void run() {
        boolean heartbeats = SseSession.sentComment(clientId, "heartbeats");
        if(heartbeats){
            heartBeatsCount.set(0);
            //心跳成功，用户在线
            logger.info("MSG: SseHeartbeat | ID: {} | Date: {}", clientId, new Date());
        }else if(heartBeatsCount.getAndDecrement()>0&&emitter==SseSession.SESSION.get(clientId)){
            //心跳失败用户断连了
            //2.尝试让用户连接
            SseSession.retry(clientId,0, TimeUnit.SECONDS);
        }else{
            //用户走了，做清理工作，两种情况，map中换上了新的emitter了，此时只需要失效旧的emitter就行，否则需要清理map
            SseSession.SESSION.remove(clientId,emitter);
            emitter.completeWithError(new SseException("用户断连了...."));
            //取消任务
            future.cancel(true);
        }

    }

    public void setFuture(ScheduledFuture<?> future) {
        this.future = future;
    }

}