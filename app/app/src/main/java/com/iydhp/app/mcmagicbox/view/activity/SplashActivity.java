package com.iydhp.app.mcmagicbox.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;

import com.iydhp.app.mcmagicbox.App;
import com.iydhp.app.mcmagicbox.BuildConfig;
import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.api.ApiResponse;
import com.iydhp.app.mcmagicbox.api.ApiCallback;
import com.iydhp.app.mcmagicbox.api.ApiService;
import com.iydhp.app.mcmagicbox.databinding.ActivitySplashBinding;
import com.iydhp.app.mcmagicbox.data.model.Splash;
import com.iydhp.app.mcmagicbox.view.BaseActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class SplashActivity extends BaseActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        if (BuildConfig.DEBUG){
            goToMainActivity(0);
            finish();
            return;
        }

        initView();
    }

    private void initView() {
        final AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(2000);
        animation.setRepeatCount(0);
        animation.setFillAfter(true);

        App.getRetrofit().create(ApiService.class)
                .getSplash()
                .enqueue(new ApiCallback<ApiResponse<Splash>>(true) {
                    @Override
                    public void onSuccess(ApiResponse<Splash> response) {
                        if (response.getData().url != null){
                            Picasso.with(SplashActivity.this)
                                    .load(response.getData().url)
                                    .into(binding.activitySplashIvBg, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            binding.activitySplashIvBg.setAnimation(animation);
                                            goToMainActivity(3000);
                                        }

                                        @Override
                                        public void onError() {
                                            goToMainActivity(0);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        goToMainActivity(0);
                    }
                });
    }

    private final static int GOTO_MAIN_ACTIVITY = 1;

    private void goToMainActivity(int delay) {
        handler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY, delay);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GOTO_MAIN_ACTIVITY:
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

}
