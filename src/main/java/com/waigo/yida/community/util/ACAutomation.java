package com.waigo.yida.community.util;

/**
 * author waigo
 * create 2021-10-05 9:57
 */

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * AC自动机
 */
public class ACAutomation {
    private TrieNode root = new TrieNode('0');//头节点不用带数据

    public ACAutomation(List<String> keywords) {
        if(keywords==null||keywords.size()==0) {
            throw new RuntimeException("敏感词库不能为空！！！");
        }
        this.init_AC_automation(keywords);
    }

    private class TrieNode {
        TrieNode fail;
        String keyword;
        char val;
        Map<Character, TrieNode> paths;

        public TrieNode(char val) {
            this.val = val;
            paths = new HashMap<>();
        }
    }

    /**
     * 1.含义，当我在father这里，我的son的fail应该由哪个节点G来确定。
     * 用途：
     * 1.在构建Trie的fail节点的时候使用
     * 2.在全文检索的时候，这条路走不通了，下条路应该由哪个节点给出
     *
     * @param father
     * @param son
     * @return 要么这个返回是头，要么就是这个节点有走下去的路，每次使用需要判断  是否有走下去的路
     */
    private TrieNode findCorrectFail(TrieNode father, TrieNode son) {
        TrieNode correctFail = father.fail;
        while (correctFail != root && !correctFail.paths.containsKey(son.val)) {
            //要么找到correctFail是头，要么找到correctFail含有走向son.val的路
            correctFail = correctFail.fail;
        }
        return correctFail;
    }

    /**
     * 将给定的关键字给生成前缀树
     *
     * @param keywords
     * @return
     */
    private ACAutomation buildTrie(List<String> keywords) {
        for (String keyword : keywords) {
            if(keyword==null||keyword.length()==0) continue;
            TrieNode temp = root;//从头出发，有路复用，无路则建路
            char[] wordPath = keyword.toCharArray();
            for (char path : wordPath) {
                if (!temp.paths.containsKey(path)) {
                    temp.paths.put(path, new TrieNode(path));
                }
                temp = temp.paths.get(path);//走下去
            }
            //temp来到了最后一个节点位置，给这个放上敏感词
            temp.keyword = keyword;
        }
        return this;
    }

    private void init_AC_automation(List<String> keywords) {
        //建好前缀树并且指好fail指针
        this.buildTrie(keywords).initialFailPointer();
    }

    /**
     * 将fail指针初始化好
     * 思路：每个节点的fail指针由它的父节点来指向，所以需要遍历一下树
     * 应该采用层序遍历的方式，这样当前的son再往上找fail指针落脚点的时候才不会碰上没有初始化fail的节点
     * <p>
     * 第一层，也就是root的子节点的fail指向root，不然会出现死循环，自己指向自己了
     */
    private void initialFailPointer() {
        root.fail = root;
        Queue<TrieNode> queue = new LinkedList<>();
        //前序非递归的精髓，弹打印，压入右，压入左
        queue.add(root);
        while (!queue.isEmpty()) {
            //一次处理一层
            int levelSize = queue.size();
            for(int i = 0;i<levelSize;i++){
                TrieNode father = queue.poll();
                //遍历孩子们
                for (Map.Entry<Character, TrieNode> sonEntry : father.paths.entrySet()) {
                    TrieNode son = sonEntry.getValue();
                    if (father == root) {
                        son.fail = root;
                    } else {
                        TrieNode correctFail = findCorrectFail(father, son);
                        son.fail = correctFail.paths.containsKey(son.val) ? correctFail.paths.get(son.val) : root;
                    }
                    queue.add(son);
                }
            }
        }
    }

    /**
     * 含义，将文章传给我，我将检查出来的敏感词信息存在map中给你
     *
     * @param content
     * @return Map < 敏感词结束下标，敏感词>
     */
    public Map<Integer, String> checkSensitiveWords(char[] content) {
        Map<Integer, String> res = new HashMap<>();
        if (content == null || content.length == 0) return res;
        TrieNode temp = root;
        for (int i = 0; i < content.length; i++) {
            char word = content[i];
            if (!isValidWord(word)) continue;
            //当前走不下去就从fail指针试一下，一直到了temp来到root位置都走不下去这个word就处理完了
            while (temp != root && !temp.paths.containsKey(word)) {
                temp = temp.fail;
            }
            if (temp.paths.containsKey(word)) {
                temp = temp.paths.get(word);
            }
            //如果走完这一步就来到了一个敏感词的结束位置就抓一个答案
            if (temp.keyword != null) {
                res.put(i, temp.keyword);
            }
        }
        return res;
    }

    private boolean isValidWord(char word) {
        //0x2E80~0x9FFF是东亚文字范围  
        //CharUtils.isAsciiAlphanumeric判断是否是ascii字符  
        return CharUtils.isAsciiAlphanumeric(word) || (word > 0x2E80 && word < 0x9FFF);
    }

}
