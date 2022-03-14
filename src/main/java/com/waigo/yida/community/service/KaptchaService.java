package com.waigo.yida.community.service;

/**
 * author waigo
 * create 2021-10-03 20:40
 */

import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;

/**
 * 不管是使用什么方式存储验证码，都需要提供两个方法
 * createKaptcha
 * checkKaptcha
 */
public interface KaptchaService {
    default String createKaptchaBase64(HttpSession session){return "";};//返回Base64编码后的验证码
    default BufferedImage createKaptcha(HttpSession session){return null;};//返回Base64编码后的验证码
    default String createEmailKaptcha(HttpSession session,String to){return "";};
    boolean checkKaptcha(String captcha,HttpSession session);//校验验证码

}
