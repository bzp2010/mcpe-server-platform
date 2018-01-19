package com.iydhp.app.mcmagicbox.view.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.data.model.Event;
import com.iydhp.app.mcmagicbox.data.service.User;
import com.iydhp.app.mcmagicbox.databinding.FragmentMainBinding;
import com.iydhp.app.mcmagicbox.databinding.FragmentMainHomeBinding;
import com.iydhp.app.mcmagicbox.view.BaseFragment;
import com.iydhp.app.mcmagicbox.view.activity.LoginActivity;
import com.iydhp.app.mcmagicbox.view.fragment.main.MainHomeFragment;
import com.iydhp.app.mcmagicbox.view.fragment.main.MainSearchFragment;
import com.iydhp.app.mcmagicbox.view.fragment.main.MainUserFragment;
import com.socks.library.KLog;

import org.greenrobot.eventbus.Subscribe;

import me.yokeyword.fragmentation.SupportFragment;

public class MainFragment extends BaseFragment {

    private FragmentMainBinding binding;

    private SupportFragment[] mFragments = new SupportFragment[3];

    private boolean loginFinish = false;

    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupportFragment firstFragment = findChildFragment(MainHomeFragment.class);
        if (firstFragment == null) {
            mFragments[FIRST] = MainHomeFragment.newInstance();
            mFragments[SECOND] = MainSearchFragment.newInstance();
            mFragments[THIRD] = MainUserFragment.newInstance();

            loadMultipleRootFragment(binding.fragmentMainContainer.getId(), FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD]);
        } else {
            mFragments[FIRST] = firstFragment;
            mFragments[SECOND] = findChildFragment(MainHomeFragment.class);
            mFragments[THIRD] = findChildFragment(MainHomeFragment.class);
        }

        binding.fragmentMainBnbNavigation
                .addItem(new BottomNavigationItem(R.drawable.ic_home_white_24dp, getString(R.string.nav_home)))
                .addItem(new BottomNavigationItem(R.drawable.ic_search_white_24dp, getString(R.string.nav_search)))
                .addItem(new BottomNavigationItem(R.drawable.ic_person_white_24dp, getString(R.string.nav_user)))
                .setMode(BottomNavigationBar.MODE_FIXED)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(int position) {
                        if (position == THIRD){
                            User.getInstance(getContext()).getLoginState(new User.GetLoginStateListener() {
                                @Override
                                public void onFinish(User.LoginState loginState) {
                                    if (loginState == User.LoginState.UNLOGIN){
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        getActivity().startActivity(intent);
                                    }else{
                                        showHideFragment(mFragments[THIRD]);
                                    }
                                }
                            });
                        }else{
                            showHideFragment(mFragments[position]);
                        }

                    }

                    @Override
                    public void onTabUnselected(int position) {

                    }

                    @Override
                    public void onTabReselected(int position) {

                    }
                })
                .initialise();
    }

    @Override
    @Subscribe
    public void onEvent(Event event) {
        if (event.getEventId() == Event.EventId.LOGOUT){
            binding.fragmentMainBnbNavigation.selectTab(FIRST, true);
        }else if (event.getEventId() == Event.EventId.LOGIN_FINISH){
            loginFinish = true;
        }else if (event.getEventId() == Event.EventId.PROFILE_CHANGE){
            loginFinish = true;
        }
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (loginFinish){
            binding.fragmentMainBnbNavigation.selectTab(THIRD, true);
            loginFinish = false;
        }
    }
}
