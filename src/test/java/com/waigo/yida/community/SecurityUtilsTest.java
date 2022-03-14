package com.waigo.yida.community;

import com.waigo.yida.community.util.SecurityUtil;

/**
 * author waigo
 * create 2021-10-09 19:11
 */
public class SecurityUtilsTest {
    public static void main(String[] args) {
        String aaa = SecurityUtil.encryptPassword("lhh", "11111111", "5abfc");
        System.out.println(aaa);
    }
}
