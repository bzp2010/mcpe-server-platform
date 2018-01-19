package com.iydhp.app.mcmagicbox.view.fragment.main;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.data.adapter.ServerListPagerAdapter;
import com.iydhp.app.mcmagicbox.data.model.ServerListPagerItem;
import com.iydhp.app.mcmagicbox.databinding.FragmentMainHomeBinding;
import com.iydhp.app.mcmagicbox.view.BaseFragment;
import com.iydhp.app.mcmagicbox.view.fragment.ServerListFragment;

import java.util.ArrayList;

public class MainHomeFragment extends BaseFragment {

    private FragmentMainHomeBinding binding;

    public static MainHomeFragment newInstance() {

        Bundle args = new Bundle();

        MainHomeFragment fragment = new MainHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_home, container, false);
        return binding.getRoot();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (!isAdded()) return;
        ArrayList<ServerListPagerItem> pagerItems = new ArrayList<>();

        ServerListPagerItem recommend = new ServerListPagerItem()
                .setTitle("推荐")
                .setFragment(ServerListFragment.newInstance(0l));

        ServerListPagerItem p1 = new ServerListPagerItem()
                .setTitle("RPG")
                .setFragment(ServerListFragment.newInstance(1l));

        ServerListPagerItem p6 = new ServerListPagerItem()
                .setTitle("小游戏")
                .setFragment(ServerListFragment.newInstance(6l));

        ServerListPagerItem p7 = new ServerListPagerItem()
                .setTitle("生存")
                .setFragment(ServerListFragment.newInstance(7l));

        pagerItems.add(recommend);
        pagerItems.add(p1);
        pagerItems.add(p6);
        pagerItems.add(p7);

        ServerListPagerAdapter pagerAdapter = new ServerListPagerAdapter(getChildFragmentManager(), pagerItems);
        binding.fragmentMainHomeVpContainer.setOffscreenPageLimit(12);
        binding.fragmentMainHomeVpContainer.setAdapter(pagerAdapter);
        binding.fragmentMainHomeTlHeader.setTabsFromPagerAdapter(pagerAdapter);
        binding.fragmentMainHomeTlHeader.setupWithViewPager(binding.fragmentMainHomeVpContainer);
    }
}
