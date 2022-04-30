package com.waigo.yida.community.service.impl;

import com.waigo.yida.community.common.Page;
import com.waigo.yida.community.constant.CommunityConstant;
import com.waigo.yida.community.dao.DiscussPostMapper;
import com.waigo.yida.community.entity.DiscussPost;
import com.waigo.yida.community.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * author waigo
 * create 2021-10-02 8:07
 */
@Service
public class DiscussPostServiceImpl implements DiscussPostService {
    @Autowired
    DiscussPostMapper discussPostMapper;
    @Override
    public int selectPageAll(Page page) {
        int rows = discussPostMapper.selectDiscussPostRows(page.getSourceId());
        page.setPageAllByRows(rows);
        return page.getPageAll();
    }

    @Override
    public List<DiscussPost> selectTargetPage(Page page) {
        if(page.getPageAll()<=0) return new ArrayList<>();
        //第一页偏移0，第二页偏移一个pageSize...
        return discussPostMapper.selectDiscussPostsByHotOrTime(
                page.getSourceId(), page.getOffset(), page.getPageSize(), page.getOrderMode()
        );
    }

    @Override
    public void addDiscussPost(DiscussPost discussPost) {
        discussPostMapper.insertDiscussPost(discussPost);
    }

    @Override
    public DiscussPost getDiscussPost(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    @Override
    public int addCommentCount(int id, int commentCount) {
        return discussPostMapper.incrCommmentCountById(id,commentCount);
    }

    @Override
    public List<DiscussPost> listAll() {
        return discussPostMapper.listAll();
    }

    @Override
    public void updatePostTypeById(int id, int type) {
        discussPostMapper.updateType(id,type);
    }

    @Override
    public void updatePostStatusById(int id, int status) {
        discussPostMapper.updateStatus(id,status);
    }

    @Override
    public void deletePostById(int id) {
        discussPostMapper.updateStatus(id, CommunityConstant.POST_DELETED);
    }
}
