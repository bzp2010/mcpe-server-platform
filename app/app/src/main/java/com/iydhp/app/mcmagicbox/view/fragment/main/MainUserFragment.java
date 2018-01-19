package com.iydhp.app.mcmagicbox.view.fragment.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.iydhp.app.mcmagicbox.App;
import com.iydhp.app.mcmagicbox.Constants;
import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.data.model.Event;
import com.iydhp.app.mcmagicbox.data.service.User;
import com.iydhp.app.mcmagicbox.databinding.FragmentMainUserBinding;
import com.iydhp.app.mcmagicbox.view.BaseFragment;
import com.iydhp.app.mcmagicbox.view.activity.UserFavouriteActivity;
import com.iydhp.app.mcmagicbox.view.activity.UserModifyActivity;
import com.securepreferences.SecurePreferences;
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;

public class MainUserFragment extends BaseFragment {

    private static MainUserFragment instance;
    private FragmentMainUserBinding binding;

    private com.iydhp.app.mcmagicbox.data.model.User userInfo;

    private boolean isLogined;

    /*public static synchronized MainUserFragment getInstance(){
        if (instance == null){
            instance = new MainUserFragment();
            instance.context = context;
            instance.isLogined = App.getApp().isLogined();
        }
        return instance;
    }*/

    public static MainUserFragment newInstance() {
        
        Bundle args = new Bundle();
        
        MainUserFragment fragment = new MainUserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_user, container, false);
        return binding.getRoot();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.activityMainUserTbHeader);

        //加载昵称，头像，积分
        reloadProfile();

        //收藏列表
        binding.fragmentMainUserRlMyfavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserFavouriteActivity.class);
                getActivity().startActivity(intent);
            }
        });

        //修改个人信息
        binding.fragmentMainUserRlUserinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserModifyActivity.class);
                getActivity().startActivity(intent);
            }
        });

    }

    private void reloadProfile(){
        //昵称，头像，积分
        User.getInstance(getContext()).getLoginedUserInfo(new User.GetLoginedUserInfoListener() {
            @Override
            public void onFinish(com.iydhp.app.mcmagicbox.data.model.User loginedUserInfo) {
                userInfo = loginedUserInfo;
                binding.fragmentMainUserRlMycomment.setVisibility(View.INVISIBLE); //暂时不显示comment
                binding.fragmentMainUserTvNickname.setText(userInfo.getNickname());
                binding.fragmentMainUserTvScore.setText("积分 "+String.valueOf(userInfo.getScore()));
                Picasso.with(getActivity()).load(userInfo.getAvatar()).into(binding.fragmentMainUserCivHead);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main_user, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_main_user_logout:
                new AlertDialog.Builder(getActivity()).setTitle("退出登录")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int which) {
                                User.getInstance(getContext()).logout();
                                App.getEventBus().post(new Event(Event.EventId.LOGOUT, null));
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Subscribe
    public void onNicknameChange(EventModel event){
        if (event.eventId == EventModel.EVENT_ID_NICKNAME_CHANGE){
            binding.fragmentMainUserTvNickname.setText(App.getApp().getLoginedUserInfo().nickname);
        }
        if (event.eventId == EventModel.EVENT_ID_AVATAR_CHANGE){
            Picasso.with(App.getApp()).load(App.getApp().getLoginedUserInfo().avatar).into(binding.fragmentMainUserCivHead);
        }
    }
*/

    @Override
    @Subscribe
    public void onEvent(Event event) {
        super.onEvent(event);
        if (event.getEventId() == Event.EventId.PROFILE_CHANGE){
            reloadProfile();
        }
    }
}
