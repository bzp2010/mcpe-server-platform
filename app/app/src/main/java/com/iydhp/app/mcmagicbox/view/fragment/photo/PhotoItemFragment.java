package com.iydhp.app.mcmagicbox.view.fragment.photo;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.databinding.FragmentPhotoItemBinding;
import com.iydhp.app.mcmagicbox.view.BaseFragment;
import com.squareup.picasso.Picasso;

public class PhotoItemFragment extends BaseFragment {

    private FragmentPhotoItemBinding binding;

    public static PhotoItemFragment newInstance(String url) {

        Bundle args = new Bundle();
        args.putString("url", url);

        PhotoItemFragment fragment = new PhotoItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_photo_item, container, false);
        return binding.getRoot();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        Picasso.with(getActivity()).load(getArguments().getString("url")).into(binding.fragmentPhotoItemPv);
    }
}
