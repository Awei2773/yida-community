package com.waigo.yida.community;

import com.waigo.yida.community.common.Page;
import com.waigo.yida.community.dao.DiscussPostRepository;
import com.waigo.yida.community.entity.DiscussPost;
import com.waigo.yida.community.service.DiscussPostService;
import com.waigo.yida.community.service.ESDiscussPostService;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * author waigo
 * create 2022-02-19 21:00
 */
@SpringBootTest
public class ElasticSearchTests {
    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    DiscussPostRepository discussPostRepository;
    @Test
    public void test1(){

        List<DiscussPost> discussPost = discussPostService.listAll();
        elasticsearchRestTemplate.save(discussPost);

    }
    @Test
    public void createIndex(){
        System.out.println(elasticsearchRestTemplate.indexOps(DiscussPost.class).exists());
        if(elasticsearchRestTemplate.indexOps(DiscussPost.class).exists()){
            System.out.println("索引创建成功");
        }
    }
    @Test
    public void testFindDiscussPostById(){
        Optional<DiscussPost> byId = discussPostRepository.findById(109);
        System.out.println(byId.get());
    }
    @Test
    public void testFindByTitleOrContent(){
//        List<SearchHit<DiscussPost>> res = discussPostRepository.findByTitleOrContentWith("因特网", "互联网");
//        for (SearchHit<DiscussPost> hit : res) {
//            System.out.println(hit.getContent());
//            for (Map.Entry<String, List<String>> entry : hit.getHighlightFields().entrySet()) {
//                System.out.println("----------------"+entry.getKey()+" hight light-----------------");
//                System.out.println(entry.getValue());
//            }
//        }
    }
    @Test
    public void testFindByTitleOrContentWith(){
        /*Sort.TypedSort<DiscussPost> postTypedSort = Sort.sort(DiscussPost.class);
        Sort sort = postTypedSort.by(DiscussPost::getType).descending().and(
                postTypedSort.by(DiscussPost::getScore).descending()
        ).and(
                postTypedSort.by(DiscussPost::getCreateTime).descending()
        );

        FieldSortBuilder type = SortBuilders.fieldSort("type").order(SortOrder.DESC);
        Sort.by(,
                SortBuilders.fieldSort("score").order(SortOrder.DESC),
                SortBuilders.fieldSort("createTime").order(SortOrder.DESC))

        Sort.by(Sort.Direction.DESC,"type")*/
        Sort sortInfo = Sort.by(new Sort.Order(Sort.Direction.DESC, "type"))
                .and(Sort.by(new Sort.Order(Sort.Direction.DESC, "score")))
                .and(Sort.by(new Sort.Order(Sort.Direction.DESC, "createTime")));
        PageRequest pageRequest = PageRequest.of(0, 10)
                .withSort(sortInfo);
//        PageRequest pageRequest = PageRequest.of(0, 10);
//        PageRequest type = pageRequest.withSort(Sort.by(new Sort.Order(Sort.Direction.DESC, "type")));
        SearchPage<DiscussPost> page = discussPostRepository.findByTitleOrContent("哈哈","哈哈",pageRequest);
        SearchHits<DiscussPost> searchHits = page.getSearchHits();
        for (SearchHit<DiscussPost> hit : searchHits) {
            System.out.println(hit.getContent());
        }
    }
    @Autowired
    ESDiscussPostService esDiscussPostService;
    @Test
    public void testESDiscussPostService(){
        Page page = new Page();
        page.setCurrent(0);
        page.setPageSize(10);
        List<DiscussPost> list = esDiscussPostService.findKeywordInTitleOrContent("哈哈", page);
        for (DiscussPost discussPost : list) {
            System.out.println(discussPost);
            System.out.println("?????????????????????????????????????");
        }
    }
}
