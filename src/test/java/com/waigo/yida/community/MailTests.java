package com.waigo.yida.community;

import com.waigo.yida.community.common.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;

@SpringBootTest
class MailTests {

    public static final String FROM = "yida_community@163.com";
    public static final String TO = "1617731215@qq.com";
    @Autowired
    JavaMailSender mailSender;
    @Test
    public void testSimpleMailSend() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM);
        simpleMailMessage.setTo(TO);
        simpleMailMessage.setText("天若有情天亦老，人间正道是沧桑");
        simpleMailMessage.setSubject("靓仔，晚上好");
        mailSender.send(simpleMailMessage);
    }
    @Autowired
    TemplateEngine templateEngine;
    @Test
    public void testMimeMailSend( ) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom(FROM);
        mimeMessageHelper.setTo(TO);
        mimeMessageHelper.setSubject("录用通知");
        Context ctx = new Context();
        ctx.setVariable("name","waigo");
        String mailHtml = templateEngine.process("mail/mail-test", ctx);
        mimeMessageHelper.setText(mailHtml,true);
        mailSender.send(mimeMessage);
    }
    @Autowired
    MailClient mailClient;
    @Test
    public void testMyMailClientUtil(){
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("name","waigo");
        mailClient.sendMimeMessage("mail/mail-test",TO,"录用通知",stringObjectHashMap);
    }
}
