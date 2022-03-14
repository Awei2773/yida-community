package com.waigo.yida.community.service;

import com.waigo.yida.community.common.Page;
import com.waigo.yida.community.entity.Message;
import com.waigo.yida.community.vo.MessageVo;

import java.util.ArrayList;
import java.util.List;

/**
 * author waigo
 * create 2021-10-07 22:12
 */
public interface MessageService {
    /**
     * 获取用户的朋友私信列表中未读的数量
     * @param userId
     * @return
     */
    int getFriendLetterUnread(int userId);


    /**
     * 查出当前页面的会话的最后一条消息
     * @param page
     * @return
     */
    List<Message> findAllConversationLastMsg(int userId, Page page);

    /**
     * 查出某个会话此用户未读的数目
     * @param conversationId
     * @return
     */
    int getConversationUnread(String conversationId, int userId);

    /**
     * 查出某个会话的消息总数
     * @param conversation
     * @return
     */
    int getConversationMsgCount(String conversation);

    /**
     * 设置会话分页的总页数，需要查出会话的数量进行分页
     * @param page
     */
    int getConversationPageAll(int userId, Page page);

    int getConversationDetailPageCount(String conservationId, Page page);

    /**
     * 根据分页组件，查出对应页的对话消息
     * @param conservationId
     * @param page
     * @return
     */
    List<Message> selectConservationMsgs(String conservationId, Page page);

    void addMessage(Message message);

    void readConversationMsgs(String conservationId, int userId);

    List<Message> getMorePage(int offset, int pageSize, String conservationId);

    int getNoticeUnreadCount(int userId);

    List<Message> findAllNoticeLastMsg(int userId);

    List<Message> listTopicAllNotices(int userId, String topic);

    int changeTopicAllNoticesStatus(int userId, String topic ,int status);

    int readTopicNotices(int userId, String topic);

    int getTopicNoticeCountByUserId(String topic, int userId);
}
