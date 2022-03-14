package com.waigo.yida.community.controller;

import com.waigo.yida.community.annotation.LoginRequired;
import com.waigo.yida.community.common.*;
import com.waigo.yida.community.constant.CommunityConstant;
import com.waigo.yida.community.entity.Comment;
import com.waigo.yida.community.entity.Event;
import com.waigo.yida.community.service.CommentService;
import com.waigo.yida.community.service.DiscussPostService;
import com.waigo.yida.community.service.impl.EventProducer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * author waigo
 * create 2021-10-07 12:38
 */
@Controller
@RequestMapping("/comment")
public class CommentController{
    @Autowired
    UserHolder userHolder;
    @Autowired
    SensitiveFilter sensitiveFilter;
    @Autowired
    CommentService commentService;
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    TransactionTemplate transactionTemplate;
    @Autowired
    EventProducer eventProducer;
    /**
     * 还要传来当前页面的path,不然后端不知道该跳去哪里，page中传来
     * @param comment 需要传来entity_type,entity_id(必须),target_id(如果是回复类型)
     * @return
     */
    @PostMapping("/post")
    @LoginRequired
    public String post(Comment comment, Page page, Model model, RedirectAttributes redirectAttributes){
        if(StringUtils.isBlank(comment.getContent())){
            Status failure = Status.failure().lineAddAttribute("jumpText","评论内容不能为空！！！")
                                             .lineAddAttribute("path","/index");
            model.addAttribute("status",failure);
            return "/site/operate-result";
        }
        int entityType = comment.getEntityType();
        //防止没有这个实体也插入错误的评论，打满数据库
        Status status = null;
        int entityId = comment.getEntityId();
        switch (entityType){
            case CommunityConstant.DISCUSS_POST:{
                if(discussPostService.getDiscussPost(entityId)==null){
                    status = Status.failure().lineAddAttribute("jumpText","该帖子不存在，无法评论！！！")
                                             .lineAddAttribute("path","/index");

                }
                break;
            }
            case CommunityConstant.COMMENT:{
                if(commentService.getCommentById(entityId)==null){
                    status = Status.failure().lineAddAttribute("jumpText","该评论不存在，无法评论！！！")
                                             .lineAddAttribute("path","/index");
                }
                break;
            }
            default:{
                status = Status.failure().lineAddAttribute("jumpText","评论类型错误！！！")
                                         .lineAddAttribute("path","/index");
            }
        }
        if(status!=null){
            model.addAttribute("status",status);
            return "/site/operate-result";
        }
        //1.过滤内容，标签过滤和敏感词过滤
        comment.setContent(sensitiveFilter.filterContent(HtmlUtils.htmlEscape(comment.getContent())));
        //2.填充评论信息
        comment.setCreateTime(new Date());
        comment.setUserId(userHolder.getUser().getId());
        //3.插入评论
        //编程式的事务控制
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionTemplate.execute(transactionStatus->{
            int rows = commentService.addComment(comment);
            //4.同步更新评论数，这里区分了回复和评论
            if(entityType==CommunityConstant.DISCUSS_POST){
                discussPostService.addCommentCount(entityId,rows);
                //5.发送异步消息到Kafka，更新评论数
                Event event = new Event();
                event.setTopic(CommunityConstant.TOPIC_UPDATE_POST)
                        .setEntityType(CommunityConstant.DISCUSS_POST)
                        .setEntityId(entityId);
                eventProducer.publishEvent(event);
            }


            return null;
        });

        //5.保障前端的友好性，需要返回传来的page信息
        redirectAttributes.addFlashAttribute("page",page);
        return "redirect:"+page.getPath();
    }
}
