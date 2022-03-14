package com.waigo.yida.community.util;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * author waigo
 * create 2021-10-04 17:18
 */
public class CookieUtil {
    public static String get(HttpServletRequest request, String name) {
        if(name==null||request.getCookies()==null) return null;
        for (Cookie cookie : request.getCookies()) {
            if(name.equals(cookie.getName())) return cookie.getValue();
        }
        return "";
    }
}
