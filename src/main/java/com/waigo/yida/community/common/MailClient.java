package com.waigo.yida.community.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Locale;
import java.util.Map;

/**
 * author waigo
 * create 2021-10-02 21:53
 */

/**
 * 通用的邮件发送工具
 */
@Component
public class MailClient {
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String from;
    @Autowired
    TemplateEngine templateEngine;
    private static final Logger LOGGER = LoggerFactory.getLogger(MailClient.class);
    /**
     * 发送text邮件去to，主题为subject,
     *
     * @param text
     * @param to
     * @param subject
     * @param isMimeMessage true是网页类型
     */
    public void send(String text, String to, String subject, boolean isMimeMessage) {
        try {
            if (!isMimeMessage) {
                SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
                simpleMailMessage.setSubject(subject);
                simpleMailMessage.setText(text);
                simpleMailMessage.setFrom(from);
                simpleMailMessage.setText(to);
                javaMailSender.send(simpleMailMessage);
            } else {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
                mimeMessageHelper.setText(text, true);
                mimeMessageHelper.setTo(to);
                mimeMessageHelper.setFrom(from);
                mimeMessageHelper.setSubject(subject);
                javaMailSender.send(mimeMessage);
            }
        } catch (MessagingException e) {
            LOGGER.error("邮件发送失败：{}",e.getMessage());

        }
    }
    public void sendSimpleText(String text,String to,String subject){
        send(text,to,subject,false);
    }
    public void sendMimeMessage(String templatePath, String to, String subject, Map<String,Object> ctxData){
        Context context = new Context(Locale.SIMPLIFIED_CHINESE,ctxData);
        String mimeText = templateEngine.process(templatePath, context);
        send(mimeText,to,subject,true);
    }
}
