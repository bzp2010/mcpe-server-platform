package com.iydhp.app.mcmagicbox.data.model;

public class User {

    private Long id;
    private String nickname = "暂未登录";
    private String avatar = "https://svcdn.qi78.com/public/uploads/images/20170807/9d98101ffeeeae4131452ae51bea43fa.png";
    private Long score = 0l;

    public Long getId() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public User setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public User setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public Long getScore() {
        return score;
    }

    public User setScore(Long score) {
        this.score = score;
        return this;
    }
}
