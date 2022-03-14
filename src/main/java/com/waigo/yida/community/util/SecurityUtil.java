package com.waigo.yida.community.util;

import org.springframework.util.DigestUtils;

/**
 * author waigo
 * create 2021-10-03 13:03
 */
public class SecurityUtil {
    private SecurityUtil(){}
    public static String encryptPassword(String password,String salt){
        if(password==null||salt==null) return "";
        return DigestUtils.md5DigestAsHex((password+salt).getBytes());
    }
    public static boolean validPassword(String password,String salt,String md5Password){
        return encryptPassword(password,salt).equals(md5Password);
    }
    public static boolean validPassword(String username,String password,String salt,String md5Password){
        return validPassword(username+password,salt,md5Password);
    }
    public static String encryptPassword(String username,String password,String salt){
        return encryptPassword(username+password,salt);
    }

    public static void main(String[] args) {
        System.out.println(SecurityUtil.encryptPassword("aaa","11111111","167f9"));
    }
}
