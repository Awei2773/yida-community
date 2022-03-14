package com.waigo.yida.community.service;

import com.waigo.yida.community.common.Status;
import com.waigo.yida.community.entity.User;

/**
 * author waigo
 * create 2021-10-03 13:48
 */
public interface UserService {
    Status register(User user);

    Status activate(int userId, String activationCode);

    void updateHeaderUrl(int userId,String url);

    User getUser(int userId);

    void changePassword(int id, String newPassword);

    User getUserByName(String username);

    boolean containsEmail(String email);

    User getUserByEmail(String email);
}
