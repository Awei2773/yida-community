package com.waigo.yida.community.entity;

import static com.waigo.yida.community.constant.AuthConstant.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * author waigo
 * create 2021-10-01 19:18
 */
public class User implements UserDetails {

    private int id;
    private String username;
    private String password;
    private String salt;
    private String email;
    /**
     * 0-普通用户; 1-超级管理员; 2-版主;
     */
    private int type;
    private int status;
    private String activationCode;
    private String headerUrl;
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", email='" + email + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", activationCode='" + activationCode + '\'' +
                ", headerUrl='" + headerUrl + '\'' +
                ", createTime=" + createTime +
                '}';
    }
    /**
     * security相关方法
     */
    /**
     * 判断账号是否未过期
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 未激活用户就说明是锁定状态
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return status == UNLOCKED;
    }

    /**
     * 判断密码是否未过期
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账号是否可用
     * @return
     */
    @Override
    public boolean isEnabled() {
        return status == UNLOCKED;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<UserAuthority> userAuthorities = new ArrayList<>();
        if(type == TYPE_ROOT){
            userAuthorities.add(new UserAuthority(ROLE_ROOT));
        }
        if(type == TYPE_MODERATOR){
            userAuthorities.add(new UserAuthority(ROLE_MODERATOR));
        }
        userAuthorities.add(new UserAuthority(ROLE_USER));
        return userAuthorities;
    }

}