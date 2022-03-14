package com.waigo.yida.community.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {
    /**
     * 新建连接
     *
     * @param clientId 客户端ID
     * @return
     */
    SseEmitter start(String clientId);
    /**
     * 发送数据
     *
     * @param clientId 客户端ID
     * @return
     */
    String send(String clientId,String data);
    /**
     * 关闭连接
     *
     * @param clientId 客户端ID
     * @return
     */
    String close(String clientId);
    String send(String event,String clientId,String data);
}