package com.iydhp.app.mcmagicbox.view.fragment.serverdetail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.databinding.FragmentServerDetailImageItemBinding;
import com.iydhp.app.mcmagicbox.view.BaseFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ServerDetailImageItemFragment extends BaseFragment {

    private FragmentServerDetailImageItemBinding binding;
    private static ServerDetailImageFragment.ViewPagerItemClickListener listener;

    public static ServerDetailImageItemFragment newInstance(int id, String url, ServerDetailImageFragment.ViewPagerItemClickListener listener) {
        ServerDetailImageItemFragment.listener = listener;

        Bundle args = new Bundle();

        args.putInt("id", id);
        args.putString("url", url);

        ServerDetailImageItemFragment fragment = new ServerDetailImageItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_server_detail_image_item, container, false);
        binding.fragmentServerDetailImageItemIv.setVisibility(View.INVISIBLE);
        return binding.getRoot();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        Picasso.with(getActivity()).load(getArguments().getString("url")).into(binding.fragmentServerDetailImageItemIv, new Callback() {
            @Override
            public void onSuccess() {
                binding.fragmentServerDetailImageItemPb.setVisibility(View.GONE);
                binding.fragmentServerDetailImageItemIv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
                binding.fragmentServerDetailImageItemPb.setVisibility(View.GONE);
                binding.fragmentServerDetailImageItemIv.setVisibility(View.VISIBLE);
            }
        });
        binding.fragmentServerDetailImageItemIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerDetailImageItemFragment.listener.onClick(getArguments().getInt("id"));
            }
        });
    }
}
