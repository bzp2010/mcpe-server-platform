package com.iydhp.app.mcmagicbox.data.model;


import android.support.v4.app.Fragment;

public class ServerListPagerItem {

    private String title;
    private Fragment fragment;

    public String getTitle() {
        return title;
    }

    public ServerListPagerItem setTitle(String name) {
        this.title = name;
        return this;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public ServerListPagerItem setFragment(Fragment fragment) {
        this.fragment = fragment;
        return this;
    }
}
