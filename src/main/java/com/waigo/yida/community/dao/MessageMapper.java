package com.waigo.yida.community.dao;

import com.waigo.yida.community.entity.Message;

import java.util.List;

/**
 * author waigo
 * create 2021-10-07 20:32
 */
public interface MessageMapper {
    /**
     * 查出有userId参与的所有会话中的最后一条消息
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectAllConservationLastMsg(int userId,int offset,int limit);

    /**
     * 分页配套的，查出userId参与的会话总数
     * @param userId
     * @return
     */
    int selectConservationCount(int userId);
    /**
     * 查出某个会话的已读或者未读消息条数
     * @param status 0表示未读，1表示已读,2表示删除，-1表示除了删除之外的总数
     * @param conversationId 会话id
     * @return
     */
    int selectMessageCount(int status,String conversationId);

    /**
     * 查出userId的未读通知数目，如果isSys=0则是朋友私信否则是系统通知
     * @param userId
     * @param isSys
     * @return
     */
    int selectUnreadLetterCount(int userId,int isSys);

    /**
     * 查出某个会话中某个用户未读的信息数目
     * @param conversationId
     * @param userId
     * @return
     */
    int selectConservationUnreadCount(String conversationId, int userId);

    /**
     * 按照时间倒序查出某个会话下的所有消息
     * @param conversationId 会话ID
     * @param offset         偏移量
     * @param limit          查几条，做分页
     * @return
     */
    List<Message> selectConversationPageMsgs(String conversationId, int offset, int limit);

    /**
     * 将某个用户的某个会话消息置为已读
     * @param conversationId
     * @param userId
     * @return
     */
    int updateUnreadInConversation(String conversationId,int userId);
    int insertMessage(Message message);

    int selectNoticeCount(int status, int userId);

    List<Message> selectAllNoticeLastMsg(int userId);

    List<Message> listTopicAllNotices(int userId, String topic);

    int updateNoticeStatusByTopic(int userId, String topic, int status);

    int updateUnreadTopicNotices(int userId, String topic);

    int selectTopicNoticeCountByUserId(String topic, int userId);
}
