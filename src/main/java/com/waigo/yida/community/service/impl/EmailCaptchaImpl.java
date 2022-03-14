package com.waigo.yida.community.service.impl;

import com.waigo.yida.community.common.MailClient;
import com.waigo.yida.community.common.Status;
import com.waigo.yida.community.service.KaptchaService;
import com.waigo.yida.community.util.RandomCodeUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * author waigo
 * create 2021-10-09 17:31
 */

/**
 * 邮箱验证码实现
 */
@Service("email-captcha")
public class EmailCaptchaImpl implements KaptchaService {
    public static final String EMAIL_CAPTCHA = "EMAIL_CAPTCHA";
    public static final String EMAIL_CAPTCHA_INVALID_TIME = "EMAIL_CAPTCHA_INVALID_TIME";
    @Autowired
    MailClient mailClient;

    /**
     * 发验证码给to
     * @param session
     * @param to
     * @return
     */
    @Override
    public String createEmailKaptcha(HttpSession session,String to) {
        //1.创造验证码
        String verifyCode = RandomCodeUtil.getRNumCodeInBits(6);
        //2.存一份
        session.setAttribute(EMAIL_CAPTCHA,verifyCode);
        session.setAttribute(EMAIL_CAPTCHA_INVALID_TIME, DateUtils.addMinutes(new Date(),5));
        //3.发送
        Status ctxData = Status.success().lineAddAttribute("email", to).lineAddAttribute("verifyCode", verifyCode);
        mailClient.sendMimeMessage("/mail/forget",to,"YIDA社区-找回密码",ctxData);
        return verifyCode;
    }

    @Override
    public boolean checkKaptcha(String captcha, HttpSession session) {
        Object verifyCode =session.getAttribute(EMAIL_CAPTCHA);
        Object invalidTime =  session.getAttribute(EMAIL_CAPTCHA_INVALID_TIME);
        if(!(verifyCode instanceof String)||!(invalidTime instanceof Date)){
            return false;
        }
        if(new Date().after((Date) invalidTime)){
            return false;
        }
        if(!captcha.equals(verifyCode)){
            return false;
        }
        return true;
    }
}
