package com.iydhp.app.mcmagicbox.data.model.api;


public class IsFavourited {

    private Boolean favourited;
    private Long id;
    private Long time;

    public Boolean getFavourited() {
        return favourited;
    }

    public IsFavourited setFavourited(Boolean favourited) {
        this.favourited = favourited;
        return this;
    }

    public Long getId() {
        return id;
    }

    public IsFavourited setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getTime() {
        return time;
    }

    public IsFavourited setTime(Long time) {
        this.time = time;
        return this;
    }
}
