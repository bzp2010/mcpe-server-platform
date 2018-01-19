package com.iydhp.app.mcmagicbox.api;

public class ApiResponsePage <T> {

    public Long total;
    public Long per_page;
    public Long current_page;
    public Long last_page;
    public T data;

}
