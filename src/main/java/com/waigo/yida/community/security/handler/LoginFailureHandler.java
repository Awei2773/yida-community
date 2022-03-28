package com.waigo.yida.community.security.handler;
import static com.waigo.yida.community.constant.PathConstant.LOGIN_PAGE;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author waigo
 * create 2022-03-23 22:55
 */
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String[] msg = exception.getMessage().split(":");
        if(msg.length==2){
            request.setAttribute(msg[0],msg[1]);
        }

        request.getRequestDispatcher(LOGIN_PAGE).forward(request,response);
    }
}
