package com.waigo.yida.community.controller.interceptor;

import com.waigo.yida.community.annotation.LoginRequired;
import com.waigo.yida.community.common.UserHolder;
import com.waigo.yida.community.exception.AjaxException;
import com.waigo.yida.community.exception.ReqException;
import com.waigo.yida.community.util.R;
import com.waigo.yida.community.util.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * author waigo
 * create 2021-10-04 22:43
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    UserHolder userHolder;
    private static final Logger logger = LoggerFactory.getLogger(LoginRequiredInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            boolean unLogin = handlerMethod.getMethod().getDeclaredAnnotation(LoginRequired.class) != null
                    && userHolder.getUser() == null;
            //需要登录但是没有登录，滚去登录
            if(unLogin){
                if(!ServletUtils.isAjaxRequest(request)){
                    //模板请求
                    throw new ReqException("UnAuthorized!!!",request.getServletPath()+"/login");
                }else{
                    //ajax请求
                    throw new AjaxException(401, R.create(401,"UnAuthorized!!!"));
                }
            }
        }
        return true;
    }
}
