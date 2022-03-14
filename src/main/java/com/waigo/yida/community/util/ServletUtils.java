package com.waigo.yida.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * author waigo
 * create 2021-10-08 17:21
 */
public class ServletUtils {
    private ServletUtils(){

    }

    /**
     * 获取servetRequest对象，通过RequestAttr
     * @return
     */
    public static HttpServletRequest getRequest(){
        return getServletAttributes().getRequest();
    }

    private static ServletRequestAttributes getServletAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    /**
     * 判断一个请求是否是ajax请求，X-Requested-With这个请求头的值表示此次ajax请求发起的原生对象，值是XMLHttpRequest
     * @param request
     * @return
     */
    public static boolean isAjaxRequest(HttpServletRequest request){
        //1.前端在等待接收的类型是application/json类型
        String accept = request.getHeader("accept");
        if(accept!=null&&accept.contains("application/json")){
            return true;
        }
        String xRequestedWith = request.getHeader("X-Requested-With");
        if(xRequestedWith!=null&&xRequestedWith.contains("XMLHttpRequest")){
            return true;
        }
        return false;
    }
}
