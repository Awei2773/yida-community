package com.waigo.yida.community.controller;

import com.waigo.yida.community.annotation.LoginRequired;
import com.waigo.yida.community.common.*;
import com.waigo.yida.community.config.properties.MinioProperties;
import com.waigo.yida.community.constant.CommunityConstant;
import com.waigo.yida.community.constant.StatusCode;
import com.waigo.yida.community.entity.*;
import com.waigo.yida.community.exception.AjaxException;
import com.waigo.yida.community.log.annotation.LogUserOpt;
import com.waigo.yida.community.log.enums.UserOption;
import com.waigo.yida.community.service.*;
import com.waigo.yida.community.service.impl.EventProducer;
import com.waigo.yida.community.util.MinioBucketUtil;
import com.waigo.yida.community.util.MinioObjectUtil;
import com.waigo.yida.community.util.R;
import com.waigo.yida.community.vo.CommentVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * author waigo
 * create 2021-10-01 22:53
 */
@Controller
@RequestMapping("/discuss")
@SuppressWarnings("Duplicates")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    UserHolder userHolder;
    @Autowired
    SensitiveFilter sensitiveFilter;
    @Autowired
    UserService userService;
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;
    @Autowired
    RedisClient redisClient;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    MinioService minioService;
    @Autowired
    MinioProperties minioProperties;

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostController.class);
    @PostMapping("/post")
    @ResponseBody
    //@LoginRequired，前端此时在等待ajax返回，所以重定向被忽略了
    public String post(String title, String content){
        //1.非空校验
        if(StringUtils.isBlank(title)||StringUtils.isBlank(content)){
            return R.create(StatusCode.BAD_REQUEST,"标题和内容都不能为空").toString();
        }
        //2.登录校验
        if(userHolder.getUser()==null){
            return R.create(StatusCode.UNAUTHORIZED,"登录之后才能发表帖子!!!").toString();
        }
        //3.html标签处理，防止XSS注入
        title = HtmlUtils.htmlEscape(title);
        content = HtmlUtils.htmlEscape(content);
        //4.敏感词过滤
        title = sensitiveFilter.filterContent(title);
        content = sensitiveFilter.filterContent(content);
        //4.初始化帖子对象
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(userHolder.getUser().getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);
        //5.更新ES，加入这个新帖子的数据
        Event event = new Event();
        event.setTopic(CommunityConstant.TOPIC_UPDATE_POST)
                .setEntityType(CommunityConstant.DISCUSS_POST)
                .setEntityId(discussPost.getId());
        eventProducer.publishEvent(event);
        return R.create(200,"OK").toString();
    }
    @GetMapping("/get/{id}")
    public String get(@PathVariable("id") int id, Model model, Page page, HttpServletRequest request){
        //1.帖子详情
        DiscussPost discussPost = discussPostService.getDiscussPost(id);
        if(discussPost==null){
            logger.error("{}这篇帖子不存在，用户进行了查询",id);
            Status failure = Status.failure().lineAddAttribute("jumpText","这篇帖子不存在哦！！！")
                                             .lineAddAttribute("path","/index");
            model.addAttribute("status",failure);
            return "/site/operate-result";//找不到，转去中间页面准备跳转首页，并且记录一下，这里出大问题,可能是攻击
        }
        //2.查出这个帖子对应的用户信息
        User user = userService.getUser(discussPost.getUserId());
        //3.查出帖子对应的评论数据
        //4.处理好分页组件
        page.setPageSize(DISCUSS_POST_PAGE_SIZE);
        page.setSourceId(id);
        //5.查出帖子对应的点赞数
        discussPost.setLikeCount(likeService.likeCount(DISCUSS_POST,discussPost.getId()));
        //6.查出当前登录用户是否点过赞
        final User curLoginUser = userHolder.getUser();
        discussPost.setLikeStatus(curLoginUser ==null?0:likeService.isLike(DISCUSS_POST,discussPost.getId(), curLoginUser.getId()));
        int pageAll = commentService.selectPageAll(DISCUSS_POST,page);
        List<CommentVo> commentVoList = null;
        page.init(pageAll,request.getServletPath());
        if(pageAll>0){
            List<Comment> commentList = commentService.selectComments(DISCUSS_POST,discussPost.getId(),page);
            //遍历这些评论，封装commentVo
            commentVoList = commentList.stream().map(comment -> {
                //1.查出每条评论的作者
                User commentAuthor = userService.getUser(comment.getUserId());
                //2.查询这个评论的回复
                List<Comment> replyList = commentService.selectReplys(COMMENT,comment.getId(),null);
                //3.这些回复遍历一遍查出他们的作者
                List<CommentVo> replys = replyList.stream().map(reply->{
                    User replyUser = userService.getUser(reply.getUserId());
                    User targetUser = null;
                    if(reply.getTargetId()!=0){
                        targetUser = userService.getUser(reply.getTargetId());
                    }
                    //5.查出这些回复的点赞数
                    long l = likeService.likeCount(COMMENT, reply.getId());
                    //6.查出这些回复，当前用户是否点赞过
                    int isLike = curLoginUser ==null?0:likeService.isLike(COMMENT,reply.getId(), curLoginUser.getId());
                    return new CommentVo(replyUser,targetUser,reply,l,isLike);
                }).collect(Collectors.toList());
                //4.查出每条评论的点赞数
                long likeCount = likeService.likeCount(COMMENT,comment.getId());
                //5.查出当前用户是否点赞过
                int isLike = curLoginUser ==null?0:likeService.isLike(COMMENT,comment.getId(), curLoginUser.getId());
                return new CommentVo(commentAuthor,comment,replys,likeCount,isLike);
            }).collect(Collectors.toList());
        }
        Status success = Status.success().lineAddAttribute("user",user)
                                         .lineAddAttribute("discussPost",discussPost)
                                         .lineAddAttribute("commentList",commentVoList==null? Collections.EMPTY_LIST:commentVoList);
        model.addAttribute("status",success);
        return "/site/discuss-detail";
    }

    //点赞异步请求
    @GetMapping("/like")
    @LogUserOpt(UserOption.LIKE)
    @ResponseBody
    public String like(int entityType,int entityId,int targetUserId){
        if(userHolder.getUser()==null){
            throw new AjaxException(401,R.create(401,"请先登录~~~"));
        }
        //之后通过Redis缓存用户进行优化
        if(userService.getUser(targetUserId)==null){
            throw new AjaxException(400,R.create(400,"该用户不存在"));
        }
        int userId = userHolder.getUser().getId();
        likeService.like(entityType,entityId, userId,targetUserId);
        int isLike = likeService.isLike(entityType,entityId,userId);
        //2.当前的点赞数
        long likeCount = likeService.likeCount(entityType, entityId);
        return R.create(200,"OK")
                .addAttribute("isLike",isLike)
                .addAttribute("likeCount",likeCount).toString();
    }
    @GetMapping("/image/uploadPolicy")
    @ResponseBody
    public Status getImageUploadPolicy(String imageName){
        FileUploadResponse fileUploadResponse = minioService.getFileUploadPolicy(minioProperties.getImageBucket(),imageName);
        if(fileUploadResponse==null){
            logger.warn("获取预签名上传Url失败！！！");
            return Status.failure();
        }
        return Status.success().lineAddAttribute("info",fileUploadResponse);
    }
}
