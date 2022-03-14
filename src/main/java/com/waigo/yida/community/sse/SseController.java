package com.waigo.yida.community.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
public class SseController {
    private static final Logger logger = LoggerFactory.getLogger(SseController.class);
    @Autowired
    private SseService sseService;
    @GetMapping("/start/{clientId}")
    public SseEmitter start(@PathVariable("clientId") String clientId) {
        return sseService.start(clientId);
    }
    /**
     * 将SseEmitter对象设置成完成
     *
     * @param clientId
     * @return
     */
    @RequestMapping("/end")
    public String close(String clientId) {
        return sseService.close(clientId);
    }
}