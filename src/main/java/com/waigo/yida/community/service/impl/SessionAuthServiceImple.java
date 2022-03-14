package com.waigo.yida.community.service.impl;

import com.waigo.yida.community.common.Status;
import com.waigo.yida.community.dao.TicketMapper;
import com.waigo.yida.community.dao.UserMapper;
import com.waigo.yida.community.entity.Ticket;
import com.waigo.yida.community.entity.User;
import com.waigo.yida.community.service.AuthService;
import com.waigo.yida.community.util.RandomCodeUtil;
import com.waigo.yida.community.util.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * author waigo
 * create 2021-10-04 11:19
 */
@Service
public class SessionAuthServiceImple implements AuthService {
    @Autowired
    TicketMapper ticketMapper;
    @Autowired
    UserMapper userMapper;

    /**
     * @param username 用户名
     * @param password 密码
     * @param expires  过期时间，单位s
     * @return
     */
    @Override
    public Status login(String username, String password, int expires) {
        Status error = Status.failure();
        if (StringUtils.isBlank(username)) {
            error.addAttribute("usernameMsg", "用户名不能为空！！！");
            return error;
        }
        if (StringUtils.isBlank(password)) {
            error.addAttribute("passwordMsg", "密码不能为空！！！");
            return error;
        }
        User user = userMapper.selectByName(username);
        if (user == null || user.getStatus() == 0) {
            error.addAttribute("usernameMsg", "该用户不存在，或者未激活！！！");
            return error;
        }
        if (!SecurityUtil.validPassword(username, password, user.getSalt(), user.getPassword())) {
            error.addAttribute("passwordMsg", "密码不正确！！！");
            return error;
        }
        //1.将之前所有凭证失效
        ticketMapper.invalidTicket(null, user.getId());
        //2.插入新凭证
        Ticket ticket = new Ticket();
        ticket.setExpired(DateUtils.addSeconds(new Date(), expires));
        String ticketText = RandomCodeUtil.getRandomCode(0);
        ticket.setTicket(ticketText);
        ticket.setUserId(user.getId());
        //3.创建新的凭证
        ticketMapper.insertTicket(ticket);
        //4.返回令牌
        Status success = Status.success();
        success.addAttribute("ticket", ticketText);
        return success;
    }

    @Override
    public void logout(String ticket) {
        ticketMapper.invalidTicket(ticket, -1);
    }

    @Override
    public boolean isLogin(String ticket,User user) {
        if (StringUtils.isBlank(ticket)) return false;
        Ticket dbTicket = ticketMapper.selectByTicket(ticket);
        if(user!=null&&dbTicket != null){
            BeanUtils.copyProperties(dbTicket.getUser(),user);//将用户数据对拷回去，减少查库
        }
        return dbTicket != null && dbTicket.getExpired().after(new Date());
    }
}
