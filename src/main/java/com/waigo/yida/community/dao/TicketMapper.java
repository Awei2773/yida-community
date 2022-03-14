package com.waigo.yida.community.dao;

import com.waigo.yida.community.entity.Ticket;
import org.apache.ibatis.annotations.*;

/**
 * author waigo
 * create 2021-10-04 9:25
 */
public interface TicketMapper {
    /**
     * 插入新的凭证
     * insert into `login_ticket` (字段...) values(value...);
     * @param ticket
     * @return
     */
    @Options(useGeneratedKeys = true,keyProperty = "id")
    @Insert({"insert into `login_ticket` ",
            "(`user_id`,`ticket`,`status`,`expired`) ",
            "values(#{userId},#{ticket},#{status},#{expired})"})
    int insertTicket(Ticket ticket);
    /**
     * 凭证失效
     * update `login_ticket` set status = 1 where ticket = #{ticket}
     */
    //如果注解方式下想要使用动态SQL，就需要添加script标签
    @Update({"<script>",
                "update `login_ticket` set status = 1 where ",
                "<choose>",
                    "<when test='ticket!=null'>",
                        "ticket = #{ticket}",
                    "</when>",
                    "<otherwise>",
                        "user_id = #{userId}",
                    "</otherwise>",
                "</choose>",
            "</script>"
    })
    int invalidTicket(String ticket,int userId);
    /**
     * 查询凭证
     * selectByTicket:select [字段] from `login_ticket` where status = 0 and ticket = #{ticket};
     */

    @Result(column = "user_id",property = "user",one =
        @One(select = "com.waigo.yida.community.dao.UserMapper.selectById")//这里的select要指向一个方法
    )
    @Select({"SELECT `user_id`,`ticket`,`status`,`expired` FROM `login_ticket` " +
            "WHERE STATUS = 0 AND ticket = #{ticket}" })
    Ticket selectByTicket(String ticket);

}
