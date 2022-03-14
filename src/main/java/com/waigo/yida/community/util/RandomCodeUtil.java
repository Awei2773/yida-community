package com.waigo.yida.community.util;

import java.util.UUID;

/**
 * author waigo
 * create 2021-10-03 12:43
 */
public class RandomCodeUtil {
    private RandomCodeUtil(){}
    /**
     * 生成from~to的随机码
     * 保证to>from
     *
     * @param from
     * @param to
     * @return
     */
    public static String getRandomNumCode(int from, int to) {
        //(int)(Math.random()*(to-from+1))  0~to-from
        return String.valueOf((int) (Math.random() * (to - from + 1)) + from);
    }

    /**
     * 指定生成位数
     *
     * @param bit
     * @return
     */
    public static String getRNumCodeInBits(int bit) {
        int from = 1;
        int to = 9;
        for(int i = 1;i<bit;i++){
            from*=10;
            to = to*10+9;
        }
        return getRandomNumCode(from,to);
    }

    /**
     * 直接UUID生成后去掉短线，再根据需要的长度截取
     *
     * @param len
     * @return
     */
    public static String getRandomCode(int len) {
        String origin = UUID.randomUUID().toString().replace("-", "");
        return len<=0?origin:origin.substring(0,len);
    }

}
