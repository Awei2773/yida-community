package com.waigo.yida.community.service.impl.resolvers;

import com.waigo.yida.community.constant.CommunityConstant;
import com.waigo.yida.community.entity.Comment;
import com.waigo.yida.community.entity.DiscussPost;
import com.waigo.yida.community.entity.Event;
import com.waigo.yida.community.entity.User;
import com.waigo.yida.community.service.CommentService;
import com.waigo.yida.community.service.DiscussPostService;
import com.waigo.yida.community.service.EventResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * author waigo
 * create 2021-11-05 23:36
 */
//如果是给评论进行评论、点赞等，那么就需要给这个事件加上评论所属主体id
//由于评论可能是对评论、可能是对帖子，可能是其他实体，所以要写一个万用的
@Component
public class CommentResolver implements EventResolver {
    @Autowired
    CommentService commentService;
    @Autowired
    DiscussPostService discussPostService;
    @Override
    public void resolve(Event event) {
        if(event.getEntityType()== CommunityConstant.COMMENT){
            Comment comment = commentService.getCommentById(event.getEntityId());
            boolean isReply = comment.getEntityType() == CommunityConstant.COMMENT;
            //1.不管是评论还是回复都需要查出具体的内容
            //目标对象如果是回复则是targetId
            event.setUserId(comment.getTargetId());
            event.addData("content",comment.getContent());
            //2.不管是评论还是回复都得查出所属的顶级资源的id和资源的标题，比如说帖子id，帖子的标题，目前顶级资源只有帖子
            while(comment.getTargetId()!=0){
                comment = commentService.getCommentById(comment.getEntityId());
            }
            DiscussPost discussPost = discussPostService.getDiscussPost(comment.getEntityId());
            event.addData("postId",discussPost.getId());
            event.addData("postTitile",discussPost.getTitle());
            //3.回复类型还要查出所属评论的id和评论的内容
            event.addData("isReply",isReply);
            if(isReply){
                event.addData("belongCommentId",comment.getId());
                event.addData("belongCommentContent",comment.getContent());
            }else if(CommunityConstant.TOPIC_LIKE.equals(event.getTopic())){
                //是评论的话需要注意，如果是点赞类型那目标对象就是评论的作者，是回复类型那才是帖子的作者
                event.setUserId(comment.getUserId());

            }else{
                event.setUserId(discussPost.getUserId());
            }

        }
    }


}
