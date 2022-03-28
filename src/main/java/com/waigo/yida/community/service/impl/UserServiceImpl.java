package com.waigo.yida.community.service.impl;

import com.waigo.yida.community.config.properties.CommunityProperties;
import com.waigo.yida.community.common.MailClient;
import com.waigo.yida.community.common.Status;
import com.waigo.yida.community.dao.UserMapper;
import com.waigo.yida.community.entity.User;
import com.waigo.yida.community.service.UserService;
import com.waigo.yida.community.util.RandomCodeUtil;
import com.waigo.yida.community.util.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * author waigo
 * create 2021-10-03 13:48
 */
@Service("userService")
public class UserServiceImpl implements UserService, UserDetailsService {


    @Autowired
    CommunityProperties communityProperties;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    @Autowired
    UserMapper userMapper;
    @Autowired
    MailClient mailClient;
    @Override
    public Status register(User user) {
        //名字
        if(StringUtils.isBlank(user.getUsername())){
            Status failure = Status.failure();
            failure.addAttribute("usernameMsg","名字不能为空！！！");
            return failure;
        }
        //密码
        if(StringUtils.isBlank(user.getPassword())){
            Status failure = Status.failure();
            failure.addAttribute("passwordMsg","密码不能为空！！！");
            return failure;
        }
        //邮箱
        if(StringUtils.isBlank(user.getEmail())){
            Status failure = Status.failure();
            failure.addAttribute("emailMsg","邮箱地址不能为空！！！");
            return failure;
        }
        //密码长度校验
        int pLen = user.getPassword().length();
        if(pLen <8||pLen>64){
            Status failure = Status.failure();
            failure.addAttribute("passwordMsg","密码长度不能小于8位，不能大于64位！！！");
            return failure;
        }
        //名字唯一
        if(userMapper.selectByName(user.getUsername())!=null){
            Status failure = Status.failure();
            failure.addAttribute("usernameMsg","该昵称已被使用！！！");
            return failure;
        }
        //邮箱唯一
        if(userMapper.selectByEmail(user.getEmail())!=null){
            Status failure = Status.failure();
            failure.addAttribute("emailMsg","该邮箱已经注册过了！！！");
            return failure;
        }
        //校验通过，开始注册逻辑
        user.setSalt(RandomCodeUtil.getRandomCode(10));
        user.setActivationCode(RandomCodeUtil.getRandomCode(0));
        user.setCreateTime(new Date());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",(int)(Math.random()*1001)));//0~1000
        //密码使用：username+原生密码+salt进行MD5摘要
        user.setPassword(SecurityUtil.encryptPassword(user.getUsername()+user.getPassword(),user.getSalt()));
        userMapper.insertUser(user);
        LOGGER.info("用户{}，注册成功~",user.getUsername());
        //发送激活邮件protocol://host/contextPath/activation/userid/activationCode
        //发送邮件
        Map<String,Object> ctxVariables = new HashMap<>();
        //contextPath需要判断是否为空
        String path = communityProperties.SERVER_CONTEXT_PATH
                + "activation" + "/" + user.getId() + "/" + user.getActivationCode();
        ctxVariables.put("path", path);
        ctxVariables.put("username",user.getUsername());
        mailClient.sendMimeMessage("/mail/activation",user.getEmail(),"激活邮件",ctxVariables);
        return Status.success();
    }

    @Override
    public Status activate(int userId, String activationCode) {
        if(StringUtils.isBlank(activationCode)||userId<=0){
            return Status.failure();
        }
        User user = userMapper.selectById(userId);
        if(user==null||user.getStatus()==1||!activationCode.equals(user.getActivationCode())){
            return Status.failure();
        }
        userMapper.updateStatus(userId,1);
        return Status.success();
    }

    @Override
    public void updateHeaderUrl(int userId,String url) {
        userMapper.updateHeader(userId,url);
    }

    @Override
    public User getUser(int userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public void changePassword(int id, String newPassword) {
        userMapper.updatePassword(id,newPassword);
    }

    @Override
    public User getUserByName(String username) {
        return userMapper.selectByName(username);
    }

    @Override
    public boolean containsEmail(String email) {
        User user = userMapper.selectByEmail(email);
        return user!=null;
    }

    @Override
    public User getUserByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(StringUtils.isBlank(username)){
            throw new UsernameNotFoundException("usernameMsg:用户名不能为空！！！");
        }
        User user = getUserByName(username);
        if (user == null || !user.isAccountNonLocked()) {
            throw new UsernameNotFoundException("usernameMsg:该用户不存在，或者未激活！！！");
        }
        return user;
    }
}
