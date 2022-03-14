package com.waigo.yida.community.controller.interceptor;

import com.waigo.yida.community.common.UserHolder;
import com.waigo.yida.community.entity.User;
import com.waigo.yida.community.service.AuthService;
import com.waigo.yida.community.util.CookieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * author waigo
 * create 2021-10-04 16:37
 */
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Autowired
    UserHolder userHolder;
    @Autowired
    AuthService authService;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginCheckInterceptor.class);
    /** handler是拦截的controller方法，这个方法在handler之前前调用*/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        //检查凭证，先抓到ticket,cookie在request中，这里需要自己去拿，springmvc帮不了我们了
        String ticket = CookieUtil.get(request,"ticket");
        if(ticket!=null){
            User user = new User();
            if(authService.isLogin(ticket,user)){
                userHolder.setUser(user);
            }
            LOGGER.debug("preHandle:{}",handler);
            //和过滤器一样，返回true才会继续执行下去
        }
        return true;
    }
    /** handler处理完，模板渲染前*/
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        LOGGER.debug("postHandle:{}",handler);
        if(modelAndView!=null&&userHolder.getUser()!=null){
            //直接将这个用户数据存到model中，也就是thymeleaf的context，到时候模板中直接用
            modelAndView.addObject("user",userHolder.getUser());
        }
    }
    /** 渲染结束后*/
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        if(userHolder.getUser()!=null){
            //收尾工作，清理threadLocal
            userHolder.clear();
        }
        LOGGER.debug("afterCompletion:{}",handler);
    }
}
