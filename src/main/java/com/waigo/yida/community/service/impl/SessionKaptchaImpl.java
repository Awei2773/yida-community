package com.waigo.yida.community.service.impl;

/**
 * author waigo
 * create 2021-10-03 21:46
 */

import com.google.code.kaptcha.Constants;
import com.waigo.yida.community.common.KaptchaProducer;
import com.waigo.yida.community.config.properties.KaptchaProperties;
import com.waigo.yida.community.service.KaptchaService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.util.Date;

/**
 * 单体应用下直接使用session来完成，之后如果升级可以添加新的实现类
 */
@Service("img-kaptcha")
public class SessionKaptchaImpl implements KaptchaService {
    public static final String CREATETIME = Constants.KAPTCHA_SESSION_KEY + "createTime";
    public static final String KAPTCHA = Constants.KAPTCHA_SESSION_KEY;
    @Autowired
    KaptchaProducer producer;
    @Autowired
    KaptchaProperties kaptchaProperties;

    @Override
    public String createKaptchaBase64(HttpSession session) {
        //每个用户登录用的key可以相同，因为处理的session不同
        //存储的key设置为KAPTCHA_SESSION_KEY
        //同时存储一下验证码的生成时间KAPTCHA_SESSION_KEY+createTime
        String[] text = new String[1];
        String captchaBase64 = producer.createCaptchaBase64(text);
        session.setAttribute(KAPTCHA, text[0]);
        session.setAttribute(CREATETIME, new Date());
        return captchaBase64;
    }

    @Override
    public BufferedImage createKaptcha(HttpSession session) {
        String[] text = new String[1];
        BufferedImage img = producer.createCaptcha(text);
        session.setAttribute(KAPTCHA, text[0]);
        session.setAttribute(CREATETIME, new Date());
        return img;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(KaptchaService.class);

    @Override
    public boolean checkKaptcha(String captcha, HttpSession session) {
        try {
            Date createTime = (Date) session.getAttribute(CREATETIME);
            String kaptcha = (String) session.getAttribute(KAPTCHA);
            if (kaptcha == null || createTime == null ||
                    DateUtils.addMinutes(createTime,kaptchaProperties.getTtl()).getTime()
                            < new Date().getTime()
                    || !kaptcha.equals(captcha))
                return false;

        } catch (Exception e) {
            LOGGER.error("校验验证码失败:{}", e.getMessage());
        }
        //删去，节省内存
        session.removeAttribute(CREATETIME);
        session.removeAttribute(KAPTCHA);
        return true;
    }
}
