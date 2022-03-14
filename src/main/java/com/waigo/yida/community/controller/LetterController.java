package com.waigo.yida.community.controller;

import com.waigo.yida.community.annotation.LoginRequired;
import com.waigo.yida.community.common.Page;
import com.waigo.yida.community.common.SensitiveFilter;
import com.waigo.yida.community.common.Status;
import com.waigo.yida.community.common.UserHolder;
import com.waigo.yida.community.constant.CommunityConstant;
import com.waigo.yida.community.constant.StatusCode;
import com.waigo.yida.community.entity.Message;
import com.waigo.yida.community.entity.User;
import com.waigo.yida.community.exception.AjaxException;
import com.waigo.yida.community.exception.ReqException;
import com.waigo.yida.community.log.annotation.LogUserOpt;
import com.waigo.yida.community.log.enums.UserOption;
import com.waigo.yida.community.service.MessageService;
import com.waigo.yida.community.service.UserService;
import com.waigo.yida.community.sse.SseService;
import com.waigo.yida.community.util.R;
import com.waigo.yida.community.vo.MessageVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * author waigo
 * create 2021-10-07 17:51
 */
@Controller
@RequestMapping("/letter")
public class LetterController {
    @Autowired
    MessageService messageService;
    @Autowired
    UserHolder userHolder;
    @Autowired
    UserService userService;
    @Autowired
    SensitiveFilter sensitiveFilter;
    @Autowired
    SseService sseService;
    @GetMapping("/friend")
    @LoginRequired
    public String letterPage(Page page, Model model){
        //1.1 查出朋友私信中未读的数目
        User user = userHolder.getUser();
        int friendLetterUnread = messageService.getFriendLetterUnread(user.getId());
        //1.2 查出系统通知中未读的数目
        int noticeUnread = messageService.getNoticeUnreadCount(user.getId());
        //2.查出所有的会话的最后一句消息
        page.setPageSize(CommunityConstant.MESSAGE_PAGE_SIZE);
        page.init(messageService.getConversationPageAll(user.getId(),page),"/letter/friend");
        List<MessageVo> messageVoList = new ArrayList<>();
        if(page.getPageAll()>0){
            List<Message> conservationLastMsgs = messageService.findAllConversationLastMsg(user.getId(),page);
            //3.遍历这些最后一条消息，封装成一个一个的VO对象
            messageVoList = conservationLastMsgs.stream().map(msg->{
                String conversation = msg.getConversationId();
                int conservationUnread = messageService.getConversationUnread(conversation,user.getId());
                User otherOne = userService.getUser(getOtherOneId(conversation,user.getId()));
                int conservationMsgCount = messageService.getConversationMsgCount(conversation);
                return new MessageVo(conservationUnread,otherOne,msg,conservationMsgCount);
            }).collect(Collectors.toList());
        }
        Status status = Status.success().lineAddAttribute("messageVoList",messageVoList)
                        .lineAddAttribute("noticeUnread",noticeUnread)
                        .lineAddAttribute("friendLetterUnread",friendLetterUnread);
        model.addAttribute("status",status);
        return "/site/letter";
    }

    private int getOtherOneId(String conversation, int id) {
        String[] s = conversation.split("_");
        int min = Integer.parseInt(s[0]);
        return min ==id?Integer.parseInt(s[1]):min;
    }
    @GetMapping("/friend/{conservationId}")
    @LoginRequired
    @LogUserOpt(UserOption.GET_CONVERSATION)
    public String letterDetail(@PathVariable("conservationId")String conservationId, Page page, Model model, HttpServletResponse response){
        page.setPageSize(CommunityConstant.MESSAGE_PAGE_SIZE);
        page.init(messageService.getConversationDetailPageCount(conservationId,page),"/letter/friend/"+conservationId);
        if(page.getPageAll()==0){
            //没有也来查，肯定非法的，跳到400页面
            throw new ReqException("非法请求，此会话未创建！！！",StatusCode.BAD_REQUEST);
        }
        List<Message> messageList = null;
        //1.查出所有此页的消息
        messageList = messageService.selectConservationMsgs(conservationId,page);
        //2.查出私信对方的信息
        User other = userService.getUser(getOtherOneId(conservationId, userHolder.getUser().getId()));
        model.addAttribute("messageList",messageList==null? Collections.EMPTY_LIST:messageList);
        model.addAttribute("other",other);
        //3.将此会话下的所有消息设置为已读,一个写操作，不用事务控制
        messageService.readConversationMsgs(conservationId,userHolder.getUser().getId());
        return "/site/letter-detail";
    }

    /**
     * 发送私信，如果对方在线，则利用SSE直接推送到位，给自己也发一份，这样自己的页面才能渲染
     * 发送事件为会话名
     * @param toName
     * @param content
     * @return
     */
    @PostMapping("/post")
    @LoginRequired
    @LogUserOpt(UserOption.SEND_LETTER)
    @ResponseBody
    public String post(@RequestParam("to") String toName,String content){
        //1.校验名字内容不能为空
        if(StringUtils.isBlank(toName)){
            throw  new AjaxException(400,R.create(400,"名字不能为空"));
        }
        if(StringUtils.isBlank(content)){
            throw  new AjaxException(400,R.create(400,"内容不能为空"));
        }
        //这里的名字也得做一下标签处理，后台数据库里都没有标签的
        //2.查出目标用户
        User to = userService.getUserByName(HtmlUtils.htmlEscape(toName));
        User from = userHolder.getUser();
        if(to==null){
            throw  new AjaxException(400,R.create(400,"该用户不存在！！！"));
        }
        //准备插入
        Message message = new Message();
        //3.过滤敏感词和标签处理
        message.setContent(sensitiveFilter.filterContent(HtmlUtils.htmlEscape(content)));
        message.setCreateTime(new Date());
        message.setFromId(from.getId());
        message.setToId(to.getId());
        message.setConversationId(getConversationId(from.getId(),to.getId()));
        message.setStatus(0);
        //4.一个写库操作，不用事务控制
        messageService.addMessage(message);
        //5.发送SSE内容,自己一份，对方一份,随缘发送，不管收不收得到，反正刷新一下重新获取就可以了
        String data = R.create(200, "OK").addAttribute("message", message).toString();
        sseService.send(message.getConversationId(),message.getFromId()+"", data);
        sseService.send(message.getConversationId(),message.getToId()+"", data);

        return R.create(200,"OK").toString();
    }

    private String getConversationId(int from, int to) {
        return Math.min(from,to)+"_"+Math.max(from,to);
    }

    /**
     * 增量查出新页
     */
    @GetMapping("/friend/{conservationId}/ajax")
    @LoginRequired
    @ResponseBody
    public String getMorePage(@PathVariable("conservationId") String conservationId,int offset){
        offset = Math.max(offset,0);
        List<Message> messageList = messageService.getMorePage(offset,CommunityConstant.MESSAGE_PAGE_SIZE,conservationId);
        return R.create(200,"OK").addAttribute("messageList",messageList).toString();
    }
}
