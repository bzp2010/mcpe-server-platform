package com.iydhp.app.mcmagicbox.data.model.api;

/**
 * 登录返回数据
 */
public class LoginResponse {

    private String token;
    private Long expire;

    public String getToken() {
        return token;
    }

    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public Long getExpire() {
        return expire;
    }

    public LoginResponse setExpire(Long expire) {
        this.expire = expire;
        return this;
    }
}
