package com.waigo.yida.community.entity;

import org.springframework.security.core.GrantedAuthority;

/**
 * author waigo
 * create 2022-03-22 23:23
 */
public class UserAuthority implements GrantedAuthority {
    String authority;
    public UserAuthority(String authority){
        this.authority = authority;
    }
    @Override
    public String getAuthority() {
        return authority;
    }
}
