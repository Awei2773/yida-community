package com.waigo.yida.community.log.aspect;

import com.waigo.yida.community.common.UserHolder;
import com.waigo.yida.community.entity.User;
import com.waigo.yida.community.log.annotation.LogUserOpt;
import com.waigo.yida.community.util.ServletUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * author waigo
 * create 2021-10-08 16:58
 */
@Aspect
@Component
public class LogUserOptAspect {
    @Autowired
    UserHolder userHolder;
    @Pointcut("@annotation(com.waigo.yida.community.log.annotation.LogUserOpt)")
    public void logPointCut(){

    }
    private final Logger logger = LoggerFactory.getLogger(LogUserOptAspect.class);
    //注意只有Before的时候才能通过参数列表绑定注解，其他的绑定不到的，但是可以通过JoinPoint的方法签名获得
    @AfterReturning(value = "logPointCut()",returning = "res")
    public void success(JoinPoint joinPoint,Object res){
        handleLog(joinPoint,null,res);
    }

    @AfterThrowing(value = "logPointCut()",throwing = "e")
    public void failure(JoinPoint joinPoint,Exception e){
        handleLog(joinPoint,e,null);
    }
    private void handleLog(JoinPoint joinPoint, Exception e,Object res) {
        //获取注解
        LogUserOpt log = getAnnotation(joinPoint);
        String username = getUserName();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String template = "用户[%s]，在[%s],使用[%s]尝试进行[%s]操作，结果:[%s]";
        String ipAddr = ServletUtils.getRequest().getRemoteHost();
        if(e!=null){
            //失败
            template += ",原因:[%s]";
            logger.info(String.format(template,username, date,ipAddr,log.value().getName(),"失败",e.getMessage() ));
        }else{
            //成功
            logger.info(String.format(template, username, date,ipAddr,log.value().getName(),"成功"));
        }
    }

    private LogUserOpt getAnnotation(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(LogUserOpt.class);
    }
    public String getUserName(){
        User user = userHolder.getUser();
        return user==null?ServletUtils.getRequest().getParameter("username"):user.getUsername();
    }
}
