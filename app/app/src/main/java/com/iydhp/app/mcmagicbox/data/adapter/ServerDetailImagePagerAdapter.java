package com.iydhp.app.mcmagicbox.data.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.iydhp.app.mcmagicbox.view.fragment.serverdetail.ServerDetailImageFragment;
import com.iydhp.app.mcmagicbox.view.fragment.serverdetail.ServerDetailImageItemFragment;

public class ServerDetailImagePagerAdapter extends FragmentPagerAdapter {

    private String[] images;
    private ServerDetailImageFragment.ViewPagerItemClickListener listener;

    public ServerDetailImagePagerAdapter(FragmentManager fm, String[] images, ServerDetailImageFragment.ViewPagerItemClickListener listener) {
        super(fm);
        this.images = images;
        this.listener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        return ServerDetailImageItemFragment.newInstance(position, images[position], listener);
    }

    @Override
    public int getCount() {
        if (images != null){
            return images.length;
        }else{
            return 0;
        }
    }
}
