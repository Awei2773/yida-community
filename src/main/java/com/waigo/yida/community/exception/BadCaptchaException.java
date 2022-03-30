package com.waigo.yida.community.exception;


import org.springframework.security.core.AuthenticationException;

/**
 * 验证码错误的异常
 * author waigo
 * create 2022-03-30 21:18
 */
public class BadCaptchaException extends AuthenticationException {
    public BadCaptchaException(String msg) {
        super(msg);
    }

    public BadCaptchaException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
