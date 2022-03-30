package com.waigo.yida.community.security.handler;
import static com.waigo.yida.community.constant.PathConstant.LOGIN_PAGE;

import com.waigo.yida.community.util.RedirectAttributes;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author waigo
 * create 2022-03-23 22:55
 */
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String[] msg = exception.getMessage().split(":");
        RedirectAttributes redirectAttributes = new RedirectAttributes(request);
        if(msg.length==2){
            redirectAttributes.addFlashAttribute(msg[0],msg[1]);
        }
        redirectAttributes.completeAttributesSet();
        redirectStrategy.sendRedirect(request,response,LOGIN_PAGE);
    }
}
