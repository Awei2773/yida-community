package com.waigo.yida.community.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class SseServiceImpl implements SseService {
    private static final Logger logger = LoggerFactory.getLogger(SseServiceImpl.class);
    /**
     * 发送心跳线程池
     */
    private static ScheduledExecutorService heartbeatExecutors = Executors.newScheduledThreadPool(4);
    /**
     * 新建连接
     *
     * @param clientId 客户端ID
     * @return
     */
    @Override
    public SseEmitter start(String clientId) {
        // 设置为0L为永不超时
        // 次数设置30秒超时,方便测试 timeout 事件
        //timeout事件指的是服务端响应的时间，比如30s就是只能响应30s,超过了就要结束了
        SseEmitter emitter = new SseEmitter(0L);
        logger.info("MSG: SseConnect | EmitterHash: {} | ID: {} | Date: {}", emitter.hashCode(), clientId, new Date());
        SseSession.add(clientId, emitter);
        //30s一次心跳检测
        HeartBeatTask command = new HeartBeatTask(clientId,emitter);
        final ScheduledFuture<?> future = heartbeatExecutors.scheduleAtFixedRate(command, 0, 30, TimeUnit.SECONDS);
        command.setFuture(future);
        emitter.onCompletion(() -> {
            logger.info("MSG: SseConnectCompletion | EmitterHash: {} |ID: {} | Date: {}", emitter.hashCode(), clientId, new Date());
        });
        emitter.onTimeout(() -> {
            logger.error("MSG: SseConnectTimeout | EmitterHash: {} |ID: {} | Date: {}", emitter.hashCode(), clientId, new Date());
            SseSession.onTimeOut(clientId);
        });
        emitter.onError(t -> {
            logger.error("MSG: SseConnectError | EmitterHash: {} |ID: {} | Date: {}|Error:{}", emitter.hashCode(), clientId, new Date(),t.getMessage());
        });
        return emitter;
    }
    /**
     * 发送数据
     *
     * @param clientId 客户端ID
     * @return
     */
    @Override
    public String send(String clientId,String data) {
        if (SseSession.sendData(clientId,data)) {
            return "Succeed!";
        }
        return "error";
    }
    /**
     * 关闭连接
     *
     * @param clientId 客户端ID
     * @return
     */
    @Override
    public String close(String clientId) {
        logger.info("MSG: SseConnectClose | ID: {} | Date: {}", clientId, new Date());
        if (SseSession.del(clientId)) return "Succeed!";
        return "Error!";
    }

    @Override
    public String send(String event, String clientId, String data) {
        return SseSession.event(event, clientId, data) ?"Succeed!":"Error!";
    }
}