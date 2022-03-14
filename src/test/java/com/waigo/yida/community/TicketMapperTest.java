package com.waigo.yida.community;

import com.waigo.yida.community.constant.AuthConstant;
import com.waigo.yida.community.dao.TicketMapper;
import com.waigo.yida.community.entity.Ticket;
import com.waigo.yida.community.util.RandomCodeUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

/**
 * author waigo
 * create 2021-10-04 10:42
 */
@SpringBootTest
public class TicketMapperTest implements AuthConstant {
    @Autowired
    TicketMapper mapper;
    @Test
    public void insertTicketTest(){
        Ticket ticket = new Ticket();
        ticket.setExpired(DateUtils.addSeconds(new Date(),COMMON_EXPIRE_TIME));
        ticket.setTicket(RandomCodeUtil.getRandomCode(0));
        ticket.setUserId(101);
        mapper.insertTicket(ticket);
    }
    @Test
    public void invalidTicketTest(){
        mapper.invalidTicket("333",-1);
    }
    @Test
    public void selectByTicketTest(){
        System.out.println(mapper.selectByTicket("333"));
    }

    

}
