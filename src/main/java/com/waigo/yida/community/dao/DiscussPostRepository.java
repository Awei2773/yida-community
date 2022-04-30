package com.waigo.yida.community.dao;

import com.waigo.yida.community.entity.DiscussPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.HighlightParameters;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


/**
 * author waigo
 * create 2022-02-20 14:03
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
    /**
     * 奇怪的bug，查"互联网"能查出来,查"哈哈"查不出来 -> 分页从0开始，需要注意
     * @param title
     * @param content
     * @param pageable
     * @return
     */
    @Deprecated
    @Highlight(
            parameters = @HighlightParameters(preTags = "<em>",postTags = "</em>"),
            fields = {
                    @HighlightField(name = "title"),
                    @HighlightField(name="content")
            }
    )
    SearchPage<DiscussPost> findByTitleOrContent(String title, String content, Pageable pageable);

    @Highlight(
            parameters = @HighlightParameters(preTags = "<em>",postTags = "</em>"),
            fields = {
                    @HighlightField(name = "title"),
                    @HighlightField(name="content")
            }
    )
    @Query("{\"bool\" : {\"should\" : [{ \"query_string\" : { \"query\" : \"?0\", \"fields\" : [ \"title\" ] } },{ \"query_string\" : { \"query\" : \"?0\", \"fields\" : [ \"content\" ] } }]}}")
    SearchPage<DiscussPost> findByTitleOrContentWithPage(String keyword, Pageable pageable);
}
