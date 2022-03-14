package com.waigo.yida.community;

import com.waigo.yida.community.common.KaptchaProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * author waigo
 * create 2021-10-03 21:34
 */
@SpringBootTest
public class CaptchaTest {
    @Autowired
    KaptchaProducer producer;
    @Test
    public void testBase64Captcha(){
        String[] text = new String[1];
        System.out.println(producer.createCaptchaBase64(text));
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(text[0]);
    }
}
