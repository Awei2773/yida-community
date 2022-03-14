package com.waigo.yida.community.service;

import com.waigo.yida.community.entity.Event;
import com.waigo.yida.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author waigo
 * create 2021-10-23 23:01
 */
@Service
public class EventResolverChain {
    @Autowired
    private List<EventResolver> resolvers;

    //进行责任链处理，将event给填充成最易用的形式，具体信息填充到data中，比如回复类型就给data中加上帖子id
    public void resolve(Event event) {
        if(resolvers!=null&&!resolvers.isEmpty()){
            //遍历责任链中的所有节点，对通知信息进行增强
            for (EventResolver resolver : resolvers) {
                resolver.resolve(event);
            }
        }
    }

}
