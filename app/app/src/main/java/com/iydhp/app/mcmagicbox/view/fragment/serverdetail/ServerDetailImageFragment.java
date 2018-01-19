package com.iydhp.app.mcmagicbox.view.fragment.serverdetail;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.data.adapter.ServerDetailImagePagerAdapter;
import com.iydhp.app.mcmagicbox.databinding.FragmentServerDetailImageBinding;
import com.iydhp.app.mcmagicbox.view.BaseFragment;
import com.iydhp.app.mcmagicbox.view.activity.PhotoActivity;
import com.socks.library.KLog;

public class ServerDetailImageFragment extends BaseFragment {

    private FragmentServerDetailImageBinding binding;

    public static ServerDetailImageFragment newInstance(String[] urls) {

        Bundle args = new Bundle();

        args.putStringArray("urls", urls);

        ServerDetailImageFragment fragment = new ServerDetailImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_server_detail_image, container, false);
        return binding.getRoot();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (!isAdded()) return;
        binding.fragmentServerDetailImageVp.setAdapter(new ServerDetailImagePagerAdapter(getActivity().getSupportFragmentManager(), getArguments().getStringArray("urls"), new ViewPagerItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getActivity(), PhotoActivity.class);
                intent.putExtra("urls", getArguments().getStringArray("urls"));
                intent.putExtra("position", position);
                startActivity(intent);
            }
        }));
        binding.fragmentServerDetailImageVp.setOffscreenPageLimit(10);
        binding.fragmentServerDetailImageVp.setPageMargin(10);
        /*binding.fragmentServerDetailImageVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                page = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/
    }

    public interface ViewPagerItemClickListener {
        public void onClick(int position);
    }

}
