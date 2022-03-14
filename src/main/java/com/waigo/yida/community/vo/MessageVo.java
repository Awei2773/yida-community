package com.waigo.yida.community.vo;

import com.waigo.yida.community.entity.Message;
import com.waigo.yida.community.entity.User;

import java.util.HashMap;

/**
 * author waigo
 * create 2021-10-07 21:55
 */
public class MessageVo {
    private int conservationUnread;
    private User otherOne;//会话的另一个人
    private Message message;//消息主体
    private int conservationMsgCount;//会话总条数
    private HashMap<String,String> data;
    public MessageVo() {
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public MessageVo(int conservationUnread, User otherOne, Message message, int conservationMsgCount) {
        this.conservationUnread = conservationUnread;
        this.otherOne = otherOne;
        this.message = message;
        this.conservationMsgCount = conservationMsgCount;
    }

    public int getConservationUnread() {
        return conservationUnread;
    }

    public void setConservationUnread(int conservationUnread) {
        this.conservationUnread = conservationUnread;
    }

    public User getOtherOne() {
        return otherOne;
    }

    public void setOtherOne(User otherOne) {
        this.otherOne = otherOne;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public int getConservationMsgCount() {
        return conservationMsgCount;
    }

    public void setConservationMsgCount(int conservationMsgCount) {
        this.conservationMsgCount = conservationMsgCount;
    }

    @Override
    public String toString() {
        return "MessageVo{" + "conservationUnread=" + conservationUnread + ", otherOne=" + otherOne + ", message=" + message + ", conservationMsgCount=" + conservationMsgCount + '}';
    }
    public void addData(String key,String value){
        if(data==null){
            data = new HashMap<>();
        }
        data.put(key,value);
    }
}

