package com.waigo.yida.community;

import com.waigo.yida.community.dao.DiscussPostMapper;
import com.waigo.yida.community.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringBootTest
class LoggerTests {
    private static final Logger logger = LoggerFactory.getLogger(LoggerTests.class);

    @Test
    public void testLogger() {
        logger.trace("Trace:{}","这是Trace级别");
        logger.debug("Debug:{}","这是Debug级别");
        logger.info("Info:{}","这是Info级别");
        logger.warn("Warn:{}","这是Warn级别");
        logger.error("Error:{}","这是Error级别");
    }
}
