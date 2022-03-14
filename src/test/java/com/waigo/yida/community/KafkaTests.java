package com.waigo.yida.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * author waigo
 * create 2021-10-22 15:56
 */
@SpringBootTest
public class KafkaTests {
    @Autowired
    TestProducer producer;
    @Test
    public void testKafka() throws InterruptedException {
        producer.sendMsg("test","你好~");
        producer.sendMsg("test","哈皮");
        Thread.sleep(10000);
    }
}
@Component
class TestProducer{
    @Autowired
    KafkaTemplate kafkaTemplate;
    public void sendMsg(String topic,String msg){
        kafkaTemplate.send(topic,msg);

    }
}
@Component
class TestConsumer{
    @KafkaListener(topics = {"test"})
    public void handle(ConsumerRecord record){
        System.out.println(record.value());
    }
}
