package com.iydhp.app.mcmagicbox.data.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.Toast;

import com.iydhp.app.mcmagicbox.view.activity.PhotoActivity;
import com.iydhp.app.mcmagicbox.view.fragment.photo.PhotoItemFragment;
import com.socks.library.KLog;

import java.util.ArrayList;

public class PhotoAdapter extends FragmentPagerAdapter {

    private ArrayList<String> urls;

    public PhotoAdapter(FragmentManager fm, ArrayList<String> urls) {
        super(fm);
        this.urls = urls;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Fragment getItem(int position) {
        return PhotoItemFragment.newInstance(urls.get(position));
    }

}
