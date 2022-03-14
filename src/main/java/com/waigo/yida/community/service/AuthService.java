package com.waigo.yida.community.service;

import com.waigo.yida.community.common.Status;
import com.waigo.yida.community.entity.User;

/**
 * author waigo
 * create 2021-10-04 11:15
 */
public interface AuthService {
    /**
     * 校验用户名、密码、创建凭证，将凭证返回
     * @param username
     * @param password
     * @param expires
     * @return
     */
    Status login(String username, String password, int expires);

    /**
     * 退出登录，将凭证状态修改为1
     * @param ticket
     */
    void logout(String ticket);

    /**
     * 判断当前是否是登录状态，true表示登录状态,如果是登录状态就将用户信息拷贝到user对象中
     * @param ticket
     * @return
     */
    boolean isLogin(String ticket, User user);
}
