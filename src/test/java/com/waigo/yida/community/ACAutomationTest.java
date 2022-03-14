package com.waigo.yida.community;

import com.waigo.yida.community.common.SensitiveFilter;
import com.waigo.yida.community.util.ACAutomation;
import org.apache.commons.lang3.CharUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * author waigo
 * create 2021-10-05 9:29
 */
@SpringBootTest
public class ACAutomationTest {
    public static void main(String[] args) {
        List<String> strings = Arrays.asList("abcd", "bce", "f", "d");
        String content = "abcefbcdabcfedfsabcdefg";
        ACAutomation acAutomation = new ACAutomation(strings);
        Map<Integer, String> sensitiveWords = acAutomation.checkSensitiveWords(content.toCharArray());
        for (Map.Entry<Integer, String> entry : sensitiveWords.entrySet()) {
            System.out.println(entry.getValue());
        }
    }
    public static boolean isValidChar(char c){
        //0x2E80~0x9FFF是东亚文字范围
        //CharUtils.isAsciiAlphanumeric判断是否是ascii字符
        return CharUtils.isAsciiAlphanumeric(c)||(c>0x2E80&&c<0x9FFF);
    }
    @Autowired
    SensitiveFilter filter;
    @Test
    public void testSensitiveCheck(){
        String s = filter.filterContent("我以一声妈妈为理由，向她无尽的索取；她以一声妈妈为枷锁，向我无限的付出。（歌颂母爱，感恩）");
        String s1 = filter.filterContent("打倒中国共产党，大日本帝国万岁");
        System.out.println(s);
        System.out.println(s1);
    }
}
