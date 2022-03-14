package com.waigo.yida.community.aspect;

import com.waigo.yida.community.annotation.LoginRequired;
import com.waigo.yida.community.common.UserHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * author waigo
 * create 2021-10-04 22:12
 */
@Aspect
@Component
public class LoginCheckAspect {
    @Autowired
    UserHolder userHolder;
    private static final Logger logger = LoggerFactory.getLogger(LoginCheckAspect.class);
    @Around("@annotation(loginRequired)")
    public Object loginRequireCheck(ProceedingJoinPoint proceedingJoinPoint, LoginRequired loginRequired) throws Throwable {
        //看是否登录了
        logger.debug("环绕通知感知到loginRequired了");
        if(userHolder.getUser()==null){
            //没登录就重定向到登录页面/login
            Object[] args = proceedingJoinPoint.getArgs();
            for(Object arg:args){
                logger.debug("{}",arg);
            }
        }
        return proceedingJoinPoint.proceed();
    }
}
