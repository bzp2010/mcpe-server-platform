package com.iydhp.app.mcmagicbox.data.model;

public class Comment {

    private Long id;
    private Double score;
    private String text;
    private Long time;
    private String type;
    private String serverid;

    private Integer status;
    private User user;

    public Long getId() {
        return id;
    }

    public Comment setId(Long id) {
        this.id = id;
        return this;
    }

    public Double getScore() {
        return score;
    }

    public Comment setScore(Double score) {
        this.score = score;
        return this;
    }

    public String getText() {
        return text;
    }

    public Comment setText(String text) {
        this.text = text;
        return this;
    }

    public Long getTime() {
        return time;
    }

    public Comment setTime(Long time) {
        this.time = time;
        return this;
    }

    public String getType() {
        return type;
    }

    public Comment setType(String type) {
        this.type = type;
        return this;
    }

    public String getServerid() {
        return serverid;
    }

    public Comment setServerid(String serverid) {
        this.serverid = serverid;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public Comment setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Comment setUser(User user) {
        this.user = user;
        return this;
    }
}
