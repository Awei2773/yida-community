package com.waigo.yida.community.security.config;
import static com.waigo.yida.community.constant.PathConstant.*;
import static com.waigo.yida.community.constant.AuthConstant.*;

import com.waigo.yida.community.security.filter.ImgCaptchaAuthenticationFilter;
import com.waigo.yida.community.security.handler.LoginFailureHandler;
import com.waigo.yida.community.security.handler.LoginSuccessHandler;
import com.waigo.yida.community.security.provider.UserAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;

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
                        USER_SETTING,USER_UPLOAD_HEADER,DISCUSS_WRITE_PAGE
                        ).hasAuthority(ROLE_USER)
                .antMatchers(LOGIN_PAGE,INDEX,INDEX_DEFAULT,INDEX_PAGE,GET_DISCUSS_POST,
                        GET_IMG_CAPTCHA,GET_EMAIL_CAPTCHA,GET_REGISTER_PAGE,REGISTER,PRINCIPAL_ACTIVE,SEARCH,
                        USER_GET_HEADER,USER_PASSWORD_FORGET_PAGE,USER_PASSWORD_FORGET_PROCESSING,USER_PROFILE_PAGE,
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
    }

    /*@Override
    protected void configure(HttpSecurity http) throws Exception {
        // 登录相关配置
        // 退出相关配置
        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        response.sendRedirect(request.getContextPath() + "/index");
                    }
                });

        // 授权配置
        http.authorizeRequests()
                .antMatchers("/letter").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/admin").hasAnyAuthority("ADMIN")
                .and().exceptionHandling().accessDeniedPage("/denied");

        // 增加Filter,处理验证码
        http.addFilterBefore(new Filter() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                HttpServletRequest request = (HttpServletRequest) servletRequest;
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                if (request.getServletPath().equals("/login")) {
                    String verifyCode = request.getParameter("verifyCode");
                    if (verifyCode == null || !verifyCode.equalsIgnoreCase("1234")) {
                        request.setAttribute("error", "验证码错误!");
                        request.getRequestDispatcher("/loginpage").forward(request, response);
                        return;
                    }
                }
                // 让请求继续向下执行.
                filterChain.doFilter(request, response);
            }
        }, UsernamePasswordAuthenticationFilter.class);

        // 记住我
        http.rememberMe()
                .tokenRepository(new InMemoryTokenRepositoryImpl())
                .tokenValiditySeconds(3600 * 24)
                .userDetailsService(userService);

    }*/

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }
}
