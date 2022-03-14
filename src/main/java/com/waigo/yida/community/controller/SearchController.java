package com.waigo.yida.community.controller;

/**
 * author waigo
 * create 2022-02-21 22:50
 */

import com.waigo.yida.community.common.Page;
import com.waigo.yida.community.constant.CommunityConstant;
import com.waigo.yida.community.constant.StatusCode;
import com.waigo.yida.community.entity.DiscussPost;
import com.waigo.yida.community.exception.ReqException;
import com.waigo.yida.community.service.ESDiscussPostService;
import com.waigo.yida.community.service.LikeService;
import com.waigo.yida.community.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 搜索模块控制层
 */
@Controller
public class SearchController {
    @Autowired
    ESDiscussPostService esDiscussPostService;
    @Autowired
    UserService userService;
    @Autowired
    LikeService likeService;
    @Value("${server.servlet.context-path}")
    String context_path;
    @GetMapping("/search")
    public String search(String keyword, Page page, Model model){
        //1.默认进来page中属性都是空的,或者违规页码,需要初始化一下,current需要>=1
        //2.参数校验
        if(StringUtils.isBlank(keyword)){
            throw new ReqException("查询关键字不能为空！！！", StatusCode.BAD_REQUEST);
        }
        List<DiscussPost> res = esDiscussPostService.findKeywordInTitleOrContent(keyword, page);
        if(res==null||res.isEmpty()){
            return "/error/100.html";
        }
        //循环查出帖子作者
        for (DiscussPost post : res) {
            post.setUser(userService.getUser(post.getUserId()));
            post.setLikeCount(likeService.likeCount(CommunityConstant.DISCUSS_POST, post.getId()));
        }
        //3.设置page的path好做分页
        page.setPath(context_path+"/search?keyword="+keyword+"&");
        page.checkPageFrom();
        model.addAttribute("keyword",keyword).addAttribute("postList",res);
        return "/site/search";
    }
}
