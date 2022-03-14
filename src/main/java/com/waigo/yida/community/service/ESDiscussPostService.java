package com.waigo.yida.community.service;

import com.waigo.yida.community.common.Page;
import com.waigo.yida.community.dao.DiscussPostRepository;
import com.waigo.yida.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * author waigo
 * create 2022-02-20 22:12
 */

/**
 * ES操控帖子的服务
 */
@Service
public class ESDiscussPostService {
    @Autowired
    DiscussPostRepository discussPostRepository;
    public List<DiscussPost> findKeywordInTitleOrContent(String keyword, Page page){
        Sort sortInfo = Sort.by(new Sort.Order(Sort.Direction.DESC, "type"))
                .and(Sort.by(new Sort.Order(Sort.Direction.DESC, "score")))
                .and(Sort.by(new Sort.Order(Sort.Direction.DESC, "createTime")));
        page.setCurrent(page.getCurrent()-1);
        PageRequest pageRequest = PageRequest.of(Math.max(page.getCurrent(),0), page.getPageSize())
                .withSort(sortInfo);
        SearchPage<DiscussPost> pageRes = discussPostRepository.findByTitleOrContentWithPage(keyword,pageRequest);
        //设置Page的总页码
        page.setPageAll(pageRes.getTotalPages());
        //设置page.current+1，为了pageHelper的显示
        page.setCurrent(page.getCurrent() +1);
        List<DiscussPost> pageList = pageRes.getSearchHits().map(hit->{
            DiscussPost post = hit.getContent();
            Map<String, List<String>> highlightFields = hit.getHighlightFields();
            List<String> title = highlightFields.get("title");
            if(title!=null&&!title.isEmpty()){
                post.setTitle(title.get(0));
            }
            List<String> content = highlightFields.get("content");
            if(content!=null&&!content.isEmpty()){
                post.setContent(content.get(0));
            }
            return post;
        }).toList();
        return pageList;
    }
    public void saveDiscussPost(DiscussPost discussPost){
        discussPostRepository.save(discussPost);
    }
    public void deleteDiscussPostById(int id){
        discussPostRepository.deleteById(id);
    }
}
