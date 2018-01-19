package com.iydhp.app.mcmagicbox.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.iydhp.app.mcmagicbox.App;
import com.iydhp.app.mcmagicbox.data.model.Event;
import com.socks.library.KLog;

import org.greenrobot.eventbus.Subscribe;
import me.yokeyword.fragmentation.SupportFragment;

public class BaseFragment extends SupportFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getEventBus().register(this);
        KLog.i(this);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onDestroy() {
        App.getEventBus().unregister(this);
        KLog.i(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(Event event){}

}
