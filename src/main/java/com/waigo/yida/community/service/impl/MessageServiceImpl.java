package com.waigo.yida.community.service.impl;

import com.waigo.yida.community.common.Page;
import com.waigo.yida.community.dao.MessageMapper;
import com.waigo.yida.community.entity.Message;
import com.waigo.yida.community.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author waigo
 * create 2021-10-07 22:21
 */
@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    MessageMapper messageMapper;
    @Override
    public int getFriendLetterUnread(int userId) {
        return messageMapper.selectUnreadLetterCount(userId,0);
    }

    @Override
    public List<Message> findAllConversationLastMsg(int userId, Page page) {
        return messageMapper.selectAllConservationLastMsg(userId,page.getOffset(),page.getPageSize());
    }

    @Override
    public int getConversationUnread(String conversationId, int userId) {
        return messageMapper.selectConservationUnreadCount(conversationId,userId);
    }

    @Override
    public int getConversationMsgCount(String conversation) {
        return messageMapper.selectMessageCount(-1,conversation);
    }

    @Override
    public int getConversationPageAll(int userId, Page page) {
        int counts = messageMapper.selectConservationCount(userId);
        page.setPageAllByRows(counts);
        return page.getPageAll();
    }

    @Override
    public int getConversationDetailPageCount(String conservationId, Page page) {
        int messageCount = messageMapper.selectMessageCount(-1, conservationId);
        page.setPageAllByRows(messageCount);
        return page.getPageAll();
    }

    @Override
    public List<Message> selectConservationMsgs(String conservationId, Page page) {
        return messageMapper.selectConversationPageMsgs(conservationId, page.getOffset(), page.getPageSize());
    }

    @Override
    public void addMessage(Message message) {
        messageMapper.insertMessage(message);
    }

    @Override
    public void readConversationMsgs(String conservationId, int userId) {
        messageMapper.updateUnreadInConversation(conservationId,userId);
    }

    @Override
    public List<Message> getMorePage(int offset, int pageSize, String conservationId) {
        return messageMapper.selectConversationPageMsgs(conservationId,offset,pageSize);
    }

    @Override
    public int getNoticeUnreadCount(int userId) {
        return messageMapper.selectNoticeCount(0,userId);
    }

    @Override
    public List<Message> findAllNoticeLastMsg(int userId) {
        return messageMapper.selectAllNoticeLastMsg(userId);
    }

    @Override
    public List<Message> listTopicAllNotices(int userId, String topic) {
        return messageMapper.listTopicAllNotices(userId,topic);
    }

    @Override
    public int changeTopicAllNoticesStatus(int userId, String topic,int status) {
        return messageMapper.updateNoticeStatusByTopic(userId,topic,status);
    }

    @Override
    public int readTopicNotices(int userId, String topic) {
        return messageMapper.updateUnreadTopicNotices(userId,topic);
    }

    @Override
    public int getTopicNoticeCountByUserId(String topic, int userId) {
        return messageMapper.selectTopicNoticeCountByUserId(topic,userId);
    }
}
