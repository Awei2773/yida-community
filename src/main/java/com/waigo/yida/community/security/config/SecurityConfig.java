package com.waigo.yida.community.security.config;
import static com.waigo.yida.community.constant.PathConstant.*;
import static com.waigo.yida.community.constant.AuthConstant.*;

import com.waigo.yida.community.common.Status;
import com.waigo.yida.community.constant.StatusCode;
import com.waigo.yida.community.security.filter.ImgCaptchaAuthenticationFilter;
import com.waigo.yida.community.security.handler.LoginFailureHandler;
import com.waigo.yida.community.security.handler.LoginSuccessHandler;
import com.waigo.yida.community.security.provider.UserAuthenticationProvider;
import com.waigo.yida.community.util.SecurityUtil;
import com.waigo.yida.community.util.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * author waigo
 * create 2022-03-23 22:43
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserAuthenticationProvider authenticationProvider;
    @Autowired
    ImgCaptchaAuthenticationFilter imgCaptchaAuthenticationFilter;
    @Autowired
    @Qualifier("userService")
    UserDetailsService userDetailsService;
    @Autowired
    LoginFailureHandler loginFailureHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage(LOGIN_PAGE)
                .loginProcessingUrl("/login")
                .successHandler(new LoginSuccessHandler())
                .failureHandler(loginFailureHandler).and()
            .authorizeRequests()
                .antMatchers(LOGOUT_PROCESSING,ADD_COMMENT,ADD_DISCUSS_POST,
                        LIKE_DISCUSS_POST,FOLLOW_SOMEONE,GET_FOLLOWEE,GET_FOLLOWER,UNFOLLOW_SOMEONE,
                        FRIEND_LETTER_PAGE,FRIEND_LETTER_CONVERSATION,POST_FRIEND_LETTER,FRIEND_LETTER_CONVERSATION_MORE_PAGE,
                        GET_NOTICE_PAGE,NOTICE_DETAIL_PAGE,USER_PASSWORD_UPDATE,
                        USER_SETTING,USER_UPLOAD_HEADER,DISCUSS_WRITE_PAGE,VIDEO_UPLOAD_DETAIL
                        ).hasAuthority(ROLE_USER)
                .antMatchers(DISCUSS_STICK,DISCUSS_TO_DIGEST).hasAuthority(ROLE_MODERATOR)
                .antMatchers(DISCUSS_DELETE).hasAuthority(ROLE_ROOT)
                .antMatchers(LOGIN_PAGE,INDEX,INDEX_DEFAULT,INDEX_PAGE,GET_DISCUSS_POST,
                        GET_IMG_CAPTCHA,GET_EMAIL_CAPTCHA,GET_REGISTER_PAGE,REGISTER,PRINCIPAL_ACTIVE,SEARCH,
                        USER_GET_HEADER,USER_PASSWORD_FORGET_PAGE,USER_PASSWORD_FORGET_PROCESSING,USER_PROFILE_PAGE,
                        DENIED_PAGE,USER_EDIT_POSTS,USER_FIRE_REPLYS,
                        "/**/*.css","/**/*.js","/**/*.jpg","/**/*.jpeg","/**/*.png").permitAll()
                .anyRequest().authenticated().and()
            .logout()
                .invalidateHttpSession(true)
                .deleteCookies(REMEMBER_ME_COOKIE)
                .logoutSuccessUrl(INDEX_DEFAULT)
                .and()
            .rememberMe()
                .tokenRepository(new InMemoryTokenRepositoryImpl())
                .rememberMeCookieName(REMEMBER_ME_COOKIE)
                .tokenValiditySeconds(REMEMBER_ME_EXPIRE_TIME)
                .rememberMeParameter(REMEMBER_ME_PARAMETER)
                .userDetailsService(userDetailsService)
                .and()
            .addFilterBefore(imgCaptchaAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .csrf()
                .disable();

        // 权限不够时的处理
        http.exceptionHandling()
                // 1. 未登录时的处理
                .authenticationEntryPoint((request, response, e) -> {
                    if (ServletUtils.isAjaxRequest(request)) {
                        // 异步请求
                        response.setContentType("application/plain;charset=utf-8");
                        PrintWriter writer = response.getWriter();
                        writer.write(Status.otherFailure(StatusCode.UNAUTHORIZED).addMessage("您还没登录~~~").toString());
                    }else{
                        response.sendRedirect(request.getContextPath() + LOGIN_PAGE);
                    }
                })
                // 2. 权限不够时的处理
                .accessDeniedHandler((request, response, e) -> {
                    if (ServletUtils.isAjaxRequest(request)) {
                        // 异步请求
                        response.setContentType("application/plain;charset=utf-8");
                        PrintWriter writer = response.getWriter();
                        writer.write(Status.otherFailure(StatusCode.FORBIDDEN).addMessage("您没有访问该功能的权限").toString());
                    }
                    else {
                        // 普通请求
                        response.sendRedirect(request.getContextPath() + DENIED_PAGE);
                    }
                });
    }



    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }
}
