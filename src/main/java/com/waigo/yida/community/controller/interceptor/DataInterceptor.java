package com.waigo.yida.community.controller.interceptor;

import com.waigo.yida.community.common.UserHolder;
import com.waigo.yida.community.entity.User;
import com.waigo.yida.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 做数据统计的拦截器
 *
 * author waigo
 * create 2022-05-01 21:27
 */
@Component
public class DataInterceptor implements HandlerInterceptor {
    @Autowired
    DataService dataService;
    @Autowired
    UserHolder userHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //记录UV
        dataService.recordUV(request.getRemoteHost());
        //记录DAU
        User user = userHolder.getUser();
        if(user!=null){
            dataService.recordDAU(user.getId());
        }
        return true;
    }
}
