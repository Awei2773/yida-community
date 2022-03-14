package com.waigo.yida.community.controller.advice;

import com.waigo.yida.community.constant.StatusCode;
import com.waigo.yida.community.exception.AjaxException;
import com.waigo.yida.community.exception.ReqException;
import com.waigo.yida.community.util.R;
import com.waigo.yida.community.util.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * author waigo
 * create 2021-10-08 18:40
 */
//指定扫描范围，提升效率
@ControllerAdvice(annotations = {Controller.class})
public class GlobalExceptionAdvice {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionAdvice.class);
    /**
     * ajax请求异常感知
     * @param e
     */
    @ExceptionHandler({AjaxException.class})
    public void ajaxException(AjaxException e, HttpServletRequest request,HttpServletResponse response){
        //1.记录日志
        logger.info("ajax请求{}异常，code:{},msg:{}",request.getContextPath(),e.getRes().getCode(),e.getRes().getMsg());
        //2.返回错误json
        try {
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(e.getRes().toString());
        } catch (IOException e1) {
            logger.error(e1.getMessage());
        }
    }

    /**
     * 模板请求异常感知，处理那种不通过页面直接发请求的异常，比如参数不传等等
     * @param e
     */
    @ExceptionHandler({ReqException.class})
    public void reqException(ReqException e, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.记录日志
        logger.info("ip:[{}]进行模板请求:[{}]异常，msg:[{}]",request.getRemoteHost(),request.getServletPath(),e.getMsg());
        //2.返回失败页面 有重定向和请求转发两种
        if(e.getRedirectPath()!=null){
            try {
                response.sendRedirect(e.getRedirectPath());
            } catch (IOException e1) {
                logger.error(e1.getMessage());
            }
        }else{
            //TODO:100这种状态码无法寻到100.html
            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE,e.getCode());
            request.getRequestDispatcher(request.getContextPath()+"/error").forward(request,response);
        }

    }
    /**
     * 其它异常感知，如果是ajax的就返回{code:500,msg:"服务端异常"},其它的就转去500消息页面
     * @param e
     */
    @ExceptionHandler({Exception.class})
    public void otherException(Exception e, HttpServletRequest request,HttpServletResponse response){
        boolean committed = response.isCommitted();
        if(!committed){//已经返回的就不能再返回数据了
            logger.error("服务器发生异常:{}",e.getMessage());
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                logger.error(stackTraceElement.toString());
            }
            if(ServletUtils.isAjaxRequest(request)){
                response.setContentType("application/plain;charset=utf-8");
                PrintWriter writer;
                try {
                    writer = response.getWriter();
                    writer.write(R.create(StatusCode.FAILURE,"服务端异常").toString());
                } catch (IOException e1) {
                    logger.error(e1.getMessage());
                }
            }else{
                //转去/error目录下，会自动根据错误状态找到对应的错误页面，比如500.html
                try {
                    //2.使用请求转发，重定向会重新发送一次请求，这个状态码设置就没用了
                    request.getRequestDispatcher("/error").forward(request,response);
                } catch (Exception e1) {
                    logger.error(e1.getMessage());
                }

            }
        }

    }
}
