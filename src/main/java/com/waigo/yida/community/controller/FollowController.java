package com.waigo.yida.community.controller;

import com.waigo.yida.community.annotation.LoginRequired;
import com.waigo.yida.community.common.KafkaClient;
import com.waigo.yida.community.common.Page;
import com.waigo.yida.community.common.UserHolder;
import com.waigo.yida.community.constant.CommunityConstant;
import com.waigo.yida.community.entity.Event;
import com.waigo.yida.community.entity.User;
import com.waigo.yida.community.exception.ReqException;
import com.waigo.yida.community.service.FollowService;
import com.waigo.yida.community.service.UserService;
import com.waigo.yida.community.service.impl.EventProducer;
import com.waigo.yida.community.util.R;
import com.waigo.yida.community.vo.FollowVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * author waigo
 * create 2021-10-15 16:43
 */
@Controller
@RequestMapping("/follow")
@SuppressWarnings("Duplicates")
public class FollowController {
    @Autowired
    EventProducer eventProducer;
    @Autowired
    FollowService followService;
    @Autowired
    UserHolder userHolder;
    @Autowired
    UserService userService;
    /**
     * AJAX请求：
     * 当前登录用户对某个实体(entityType,entityId)进行关注，还需要传这个实体所属的用户Id
     * 关注两步走：
     * 1.先让关注实体将userId添加进入自己的粉丝列表
     * 2.用户自己的关注列表加入实体Id
     * @param entityType
     * @param entityId
     * @return
     */
    @PostMapping("/follow")
    @ResponseBody
    @LoginRequired
    public String follow(int entityType,int entityId,int fromUserId){
        //不能自己关注自己
        User currentUser = userHolder.getUser();
        if(entityType!=CommunityConstant.USER|| currentUser.getId()!=entityId){
            followService.follow(entityType,entityId, currentUser.getId(),fromUserId);
        }
        Event event = new Event().setUserId(fromUserId)
                   .setPostId(currentUser.getId())
                   .setTopic(CommunityConstant.TOPIC_FOCUS)
                   .setEntityType(entityType)
                   .setEntityId(entityId);
        eventProducer.publishEvent(event);
        return R.create(200,"OK").toString();
    }
    /**
     * AJAX请求：
     * 当前登录用户对某个实体(entityType,entityId)进行取消关注
     * 关注两步走：
     * 1.先让关注实体将userId从自己的粉丝列表删去
     * 2.用户自己的关注列表删去实体Id
     * @param entityType
     * @param entityId
     * @return
     */
    @PostMapping("/unFollow")
    @ResponseBody
    @LoginRequired
    public String unFollow(int entityType,int entityId){
        followService.unFollow(entityType,entityId,userHolder.getUser().getId());
        return R.create(200,"OK").toString();
    }

    /**
     * 传入分页组件和实体，支持分页查出当前用户的粉丝
     * @param page
     * @param model
     * @return
     */
    @GetMapping(value = "/follower",produces ={MimeTypeUtils.TEXT_HTML_VALUE})
    public String getUserFollowerPage(Page page, Model model, int userId, HttpServletRequest request){
        //1.page组件初始化
        page.setPageSize(CommunityConstant.FOLLOWER_LIST_PAGE_SIZE);
        page.setPath(request.getServletPath());
        //2.查出总页数
        page.setPageAllByRows(followService.findEntityFollowersCount(CommunityConstant.USER,userId));
        page.checkCurrent();
        page.checkPageFrom();
        List<FollowVO> followers = null;
        //3.做页数判断，一页都无就不用查了
        if(page.getPageAll()>0){
            Set<ZSetOperations.TypedTuple<String>> followerIds = followService.selectFollowerPage(CommunityConstant.USER,userId,page);
            //4.遍历这些用户，查出他们的信息
            followers = followerIds.stream().map(followerMember->{
                Object value = followerMember.getValue();
                int fansId = (int) value;
                User user = userService.getUser(fansId);
                if(user==null)
                    throw new ReqException("关注业务出现问题，存在关注了不存在粉丝的情况,粉丝Id:"+fansId+",目标用户ID:"+userId,500);
                User curLoginUser = userHolder.getUser();
                boolean isFollower = curLoginUser !=null&&curLoginUser.getId()!=user.getId()
                        &&followService.isFollower(CommunityConstant.USER,user.getId(),curLoginUser.getId());
                return new FollowVO(user,isFollower,new Date(followerMember.getScore().longValue()));
            }).collect(Collectors.toList());
        }
        //5.查出当前实体的信息
        model.addAttribute("pageMaster",userService.getUser(userId));
        //6.将当前页返回model
        model.addAttribute("followers",followers==null? Collections.EMPTY_LIST:followers);
        return "/site/follower";
    }

    /**
     * 传入分页组件和实体，支持分页查出userId关注的用户列表
     * @param page
     * @param model
     * @param userId
     * @return
     */
    @GetMapping(value = "/followee",produces ={MimeTypeUtils.TEXT_HTML_VALUE})
    public String getFolloweePage(Page page, Model model,int userId,HttpServletRequest request){
        //1.page组件初始化
        page.setPageSize(CommunityConstant.FOLLOWEE_LIST_PAGE_SIZE);
        page.setPath(request.getServletPath());
        //2.查出总页数
        page.setPageAllByRows(followService.findUserFolloweesCountByType(userId,CommunityConstant.USER));
        page.checkCurrent();
        page.checkPageFrom();
        List<FollowVO> followees = null;
        //3.做页数判断，一页都无就不用查了
        if(page.getPageAll()>0){
            Set<ZSetOperations.TypedTuple<String>> followeeIds = followService.selectFolloweePageByType(CommunityConstant.USER,userId,page);
            //4.遍历这些用户，查出他们的信息
            followees = followeeIds.stream().map(followeeMember->{
                Object value = followeeMember.getValue();
                int followeeId = (int) value;
                User user = userService.getUser(followeeId);
                if(user==null)
                    throw new ReqException("关注业务出现问题，存在关注了不存在粉丝的情况,粉丝Id:"+userId+",目标用户ID:"+followeeId,500);
                User curLoginUser = userHolder.getUser();
                //1.如果当前登录用户就是这个查的userId，那肯定都是关注的
                //2.如果当前没有登录用户，那么肯定是没有关注的
                //3.否则，就需要当前登录用户去查一下自己的关注列表是否存在这个user
                boolean isFollower = curLoginUser !=null&&(
                        (curLoginUser.getId()==userId)||
                        (curLoginUser.getId()!=user.getId()
                                &&
                         followService.isFollower(CommunityConstant.USER,user.getId(),curLoginUser.getId()))
                        );
                return new FollowVO(user,isFollower,new Date(followeeMember.getScore().longValue()));
            }).collect(Collectors.toList());
        }
        //5.查出当前实体的信息
        model.addAttribute("pageMaster",userService.getUser(userId));
        //6.将当前页返回model
        model.addAttribute("followees",followees==null? Collections.EMPTY_LIST:followees);
        return "/site/followee";
    }

}
