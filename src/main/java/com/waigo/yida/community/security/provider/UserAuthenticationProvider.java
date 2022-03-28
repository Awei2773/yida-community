package com.waigo.yida.community.security.provider;

import com.waigo.yida.community.common.Status;
import com.waigo.yida.community.entity.Ticket;
import com.waigo.yida.community.entity.User;
import com.waigo.yida.community.util.RandomCodeUtil;
import com.waigo.yida.community.util.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * author waigo
 * create 2022-03-23 22:10
 */
@Component
public class UserAuthenticationProvider extends DaoAuthenticationProvider{
    @Autowired
    @Qualifier("userService")
    UserDetailsService userDetailsService;
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            this.logger.debug("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException("passwordMsg:密码不能为空！！！");
        }
        //1.前端传来的密码
        String presentedPassword = authentication.getCredentials().toString();
        if (!SecurityUtil.validPassword(userDetails.getUsername(), presentedPassword
                                       , ((User)userDetails).getSalt(), userDetails.getPassword())) {
            throw new BadCredentialsException("passwordMsg:密码不正确！！！");
        }
    }

    @Override
    protected void doAfterPropertiesSet() {
        setUserDetailsService(userDetailsService);
        setHideUserNotFoundExceptions(false);
    }
}
