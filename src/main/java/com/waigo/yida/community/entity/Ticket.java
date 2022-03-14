package com.waigo.yida.community.entity;

import java.util.Date;

/**
 * author waigo
 * create 2021-10-04 9:37
 */
public class Ticket {
    private int id;
    private int userId;
    private int status;
    private String ticket;
    private Date expired;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    @Override
    public String toString() {
        return "Ticket{" + "id=" + id + ", sourceId=" + userId + ", status=" + status + ", ticket='" + ticket + '\'' + ", expired=" + expired + ", user=" + user + '}';
    }
}
