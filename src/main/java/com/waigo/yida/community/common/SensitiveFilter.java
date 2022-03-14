package com.waigo.yida.community.common;

import com.waigo.yida.community.util.ACAutomation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

/**
 * author waigo
 * create 2021-10-05 18:33
 */
@Component
public class SensitiveFilter {
    private static final char REPLACE_CHAR = '*';
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    private ACAutomation acAutomation;

    private class DeSortCompEntry implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2 - o1;
        }
    }

    private final Comparator<Integer> deSortComp = new DeSortCompEntry();

    /**
     * 1)实现思路：通过AC自动机全文检索到这里的所有敏感词之后，遍历这个Map，从敏感词的结尾位置开始向前遍历，
     * 直到遍历到敏感词的第一个字符为止，全修改成（*：这个可以配置）
     *
     * @param content
     * @return
     */
    public String filterContent(String content) {
        if (StringUtils.isBlank(content) || acAutomation == null) return "";
        char[] contentChars = content.toCharArray();
        Map<Integer, String> sensitiveWords = acAutomation.checkSensitiveWords(contentChars);
        //sensitiveEntry,key是结尾下标，value是敏感词
        //TODO:这里有个问题，就是存在前缀的情况，比如：“嫖、嫖娼"都是敏感词，如果我们先处理的是嫖娼，可能嫖想要处理的时候已经是*了
        //TODO:解决方法,将这些找到的敏感词列表按照下标进行逆序，从下标高的先处理，低下标的只要看一下当前是*就不用接着处理了
        //这里如果说当前敏感词位置已经变成*了，就说明有一个和当前敏感词连接的敏感词处理过了，一般敏感词如果覆盖了就是这一长串都是敏感词
        Set<Map.Entry<Integer, String>> entries = sensitiveWords.entrySet();
        TreeMap<Integer, String> treeMap = new TreeMap<>(deSortComp);
        for (Map.Entry<Integer, String> sensitiveEntry : entries) {
            treeMap.put(sensitiveEntry.getKey(), sensitiveEntry.getValue());
        }
        while (!treeMap.isEmpty()) {
            Map.Entry<Integer, String> sensitiveEntry = treeMap.pollFirstEntry();
            int i = sensitiveEntry.getKey();
            if (contentChars[i] == '*') continue;
            String keyword = sensitiveEntry.getValue();
            int s = keyword.length()-1;
            while(s>=0&&i>=0){//管你隔多远，一定把keyword给找齐了
                if(contentChars[i]==keyword.charAt(s)){
                    s--;
                }
                contentChars[i--] = '*';
            }
        }
        return new String(contentChars);
    }

    @PostConstruct
    public void loadSensitiveMethod() {
        //字符缓冲流读取敏感词库
        //try()这个小括号里的流会自动添加finally进行关闭，since @jdk1.7
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("keywords"); InputStreamReader isr = new InputStreamReader(is); BufferedReader reader = new BufferedReader(isr)) {
            ArrayList<String> keywords = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                keywords.add(line);
            }
            acAutomation = new ACAutomation(keywords);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("敏感词库的读取失败:{}", e.getMessage());
        }

    }
}
