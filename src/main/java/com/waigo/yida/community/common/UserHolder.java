package com.waigo.yida.community.common;

import com.waigo.yida.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * author waigo
 * create 2021-10-04 16:52
 */

/**
 * ThreadLocal封装一层，简化用户信息的存取使用
 */
@Component
public class UserHolder {
    private static final ThreadLocal<User> holder;

    static {
        holder = new ThreadLocal<>();
    }
    /** 将用户数据存到ThreadLocal中*/
    public void setUser(User user) {
        holder.set(user);
    }
    /** 从ThreadLocal中获取user*/
    public User getUser() {
        return holder.get();
    }
    /** 防止内存泄漏，总是记得使用完后清理一下*/
    public void clear() {
        holder.remove();
    }
}
