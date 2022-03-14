package com.waigo.yida.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.waigo.yida.community.annotation.LoginRequired;
import com.waigo.yida.community.common.Status;
import com.waigo.yida.community.common.UserHolder;
import com.waigo.yida.community.constant.CommunityConstant;
import com.waigo.yida.community.constant.StatusCode;
import com.waigo.yida.community.entity.Event;
import com.waigo.yida.community.entity.Message;
import com.waigo.yida.community.entity.User;
import com.waigo.yida.community.exception.ReqException;
import com.waigo.yida.community.service.EventResolverChain;
import com.waigo.yida.community.service.MessageService;
import com.waigo.yida.community.service.UserService;
import com.waigo.yida.community.service.impl.resolvers.CommentResolver;
import com.waigo.yida.community.vo.MessageVo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

/**
 * author waigo
 * create 2021-10-22 21:32
 */
@Controller
@RequestMapping("/notice")
public class NoticeController {
    @Autowired
    UserHolder userHolder;
    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;
    @GetMapping
    @LoginRequired
    public String getPage(Model model){
        //1.1 查出朋友私信中未读的数目
        User currentUser = userHolder.getUser();
        int friendLetterUnread = messageService.getFriendLetterUnread(currentUser.getId());
        //1.2 查出系统通知中未读的数目
        int noticeUnread = messageService.getNoticeUnreadCount(currentUser.getId());
        //2.查出所有的系统通知的最后一句消息
        List<Message> allNoticeLastMsgs = messageService.findAllNoticeLastMsg(currentUser.getId());
        final Status status = Status.success();
        //遍历这些最后一句消息做出MessageVoList
        Map<String,MessageVo> messageVoMap = allNoticeLastMsgs.stream().map(msg->{
            int conversationUnread = messageService.getConversationUnread(msg.getConversationId(), currentUser.getId());
            //获取事件的发起人
            Event event = JSONObject.parseObject(msg.getContent(), Event.class);
            status.lineAddAttribute(event.getTopic()+"Content",event);
            User otherOne = userService.getUser(event.getPostId());
            int conservationMsgCount = messageService.getTopicNoticeCountByUserId(msg.getConversationId(),currentUser.getId());
            return new MessageVo(conversationUnread,otherOne,msg,conservationMsgCount);
        }).collect(Collectors.toMap(msg->msg.getMessage().getConversationId(),msg->msg));

        status.lineAddAttribute("messageVoMap",messageVoMap)
                .lineAddAttribute("noticeUnread",noticeUnread)
                .lineAddAttribute("friendLetterUnread",friendLetterUnread);
        model.addAttribute("status",status);
        return "/site/notice";
    }
    @GetMapping("/notice-detail/{topic}")
    @LoginRequired
    public String getDetailPage(Model model, @PathVariable("topic")String topic){
        if(Strings.isBlank(topic)){
            throw new ReqException("通知类型错误",StatusCode.BAD_REQUEST);
        }
        User currentUser = userHolder.getUser();
        //1.查出所有此页的消息
        List<Message> messageList = messageService.listTopicAllNotices(currentUser.getId(),topic);
        List<MessageVo> messageVoList = messageList.stream().map(msg->{
            Event event = JSONObject.parseObject(msg.getContent(), Event.class);
            User postUser = userService.getUser(event.getPostId());
            MessageVo messageVo = new MessageVo();
            messageVo.setOtherOne(postUser);
            messageVo.setMessage(msg);
            Object postId = event.getData().get("postId");
            messageVo.addData("postId", Objects.isNull(postId)?"":postId.toString());
            if(event.getEntityType()==CommunityConstant.DISCUSS_POST){
                messageVo.addData("postId",event.getEntityId()+"");
            }
            messageVo.addData("topic",event.getTopic());
            return messageVo;
        }).collect(Collectors.toList());
        model.addAttribute("messageVoList",messageVoList);
        //3.将此会话下的所有消息设置为已读,一个写操作，不用事务控制
        messageService.readTopicNotices(userHolder.getUser().getId(),topic);
        return "/site/notice-detail";
    }

}
