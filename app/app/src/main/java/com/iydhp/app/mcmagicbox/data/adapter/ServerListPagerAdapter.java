package com.iydhp.app.mcmagicbox.data.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.iydhp.app.mcmagicbox.data.model.ServerListPagerItem;

import java.util.List;

public class ServerListPagerAdapter extends FragmentPagerAdapter {

    private List<ServerListPagerItem> pagerItems;

    public ServerListPagerAdapter(FragmentManager fm, List<ServerListPagerItem> pagerItems) {
        super(fm);
        this.pagerItems = pagerItems;
    }

    @Override
    public Fragment getItem(int position) {
        if (pagerItems.get(position) != null){
            return pagerItems.get(position).getFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        if (pagerItems != null){
            return pagerItems.size();
        }
        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (pagerItems.get(position) != null){
            return pagerItems.get(position).getTitle();
        }
        return null;
    }
}
