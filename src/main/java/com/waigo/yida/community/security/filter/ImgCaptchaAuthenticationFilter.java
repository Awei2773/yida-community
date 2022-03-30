package com.waigo.yida.community.security.filter;

import static com.waigo.yida.community.constant.PathConstant.LOGIN_PROCESSING;

import com.waigo.yida.community.exception.BadCaptchaException;
import com.waigo.yida.community.security.handler.LoginFailureHandler;
import com.waigo.yida.community.service.KaptchaService;
import org.apache.catalina.manager.util.SessionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * author waigo
 * create 2022-03-30 21:09
 */
@Component
public class ImgCaptchaAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(ImgCaptchaAuthenticationFilter.class);

    @Autowired
    @Qualifier("img-kaptcha")
    KaptchaService kaptchaService;

    @Autowired
    LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (StringUtils.equalsIgnoreCase(request.getServletPath(), request.getContextPath() + LOGIN_PROCESSING) && StringUtils.equalsIgnoreCase(request.getMethod(), "post")) {
            try {
                validateCaptcha(request);
            } catch (AuthenticationException e) {
                logger.info("验证码校验不通过,ip:{},e:{}", request.getPathInfo(), e.getMessage());
                loginFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void validateCaptcha(HttpServletRequest request) throws AuthenticationException {
        String captcha = ServletRequestUtils.getStringParameter(request, "captcha", null);
        HttpSession session = request.getSession(false);
        if (session == null) {
            logger.warn("检查验证码时，session为空");
            throw new BadCaptchaException("captchaMsg:验证码未生成！！！");
        }
        if (!kaptchaService.checkKaptcha(captcha, session)) {
            throw new BadCaptchaException("captchaMsg:验证码校验不通过！！！");
        }
    }
}
