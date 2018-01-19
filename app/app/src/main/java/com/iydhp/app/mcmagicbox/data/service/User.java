package com.iydhp.app.mcmagicbox.data.service;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.iydhp.app.mcmagicbox.App;
import com.iydhp.app.mcmagicbox.Constants;
import com.iydhp.app.mcmagicbox.api.ApiCallback;
import com.iydhp.app.mcmagicbox.api.ApiResponse;
import com.iydhp.app.mcmagicbox.api.ApiService;
import com.iydhp.app.mcmagicbox.data.model.Event;
import com.iydhp.app.mcmagicbox.data.model.api.CheckTokenResponse;
import com.iydhp.app.mcmagicbox.data.model.api.LoginResponse;
import com.iydhp.app.mcmagicbox.view.activity.LoginActivity;
import com.securepreferences.SecurePreferences;
import com.socks.library.KLog;
import com.tencent.bugly.Bugly;
import com.tencent.stat.StatConfig;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 用户服务
 */
public class User {

    private Context context;
    private static User instance;
    private String token;
    private Long tokenExpire;
    private LoginState loginState = LoginState.UNLOGIN;
    private com.iydhp.app.mcmagicbox.data.model.User loginedUserInfo;

    public enum LoginState{
        UNLOGIN,
        LOGINING,
        LOGINED
    }

    public interface LoginListener{
        public void onSuccess(LoginState loginState);
        public void onFailure(LoginState loginState);
    }

    public interface GetLoginStateListener{
        public void onFinish(LoginState loginState);
    }

    public interface GetLoginedUserInfoListener{
        public void onFinish(com.iydhp.app.mcmagicbox.data.model.User loginedUserInfo);
    }

    public interface UpdateLoginStateListener{
        public void onFinish();
    }

    public static User getInstance(Context context){
        if (instance == null){
            instance = new User(context);
        }
        return instance;
    }

    private User(Context context){
        this.context = context;

        reloadToken();
        if (token != null && tokenExpire != null){
            if (tokenExpire * 1000 <= System.currentTimeMillis()){
                loginState = LoginState.UNLOGIN;
            }
        }
    }

    private void reloadToken(){
        SecurePreferences preferences = new SecurePreferences(context, Constants.PREFERENCE_AES_KEY, "user");

        this.token = preferences.getString("token", null);
        this.tokenExpire = preferences.getLong("token_expire", -1) != -1 ? preferences.getLong("token_expire", -1) : null;
    }

    public User login(String code, final LoginListener listener){
        App.getRetrofit()
                .create(ApiService.class)
                .login(code)
                .enqueue(new ApiCallback<ApiResponse<LoginResponse>>(true) {
                    @Override
                    public void onSuccess(ApiResponse<LoginResponse> response) {
                        LoginResponse data = response.getData();
                        token = data.getToken();
                        tokenExpire = data.getExpire();
                        SecurePreferences preferences = new SecurePreferences(context, Constants.PREFERENCE_AES_KEY, "user");
                        preferences.edit()
                                .putString("token", token)
                                .putLong("token_expire", tokenExpire)
                                .apply();
                        User.getInstance(context).updateLoginState(new UpdateLoginStateListener() {
                            @Override
                            public void onFinish() {
                                listener.onSuccess(loginState);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        listener.onFailure(loginState);
                    }
                });

        return this;
    }

    public void logout(){
        SecurePreferences preferences = new SecurePreferences(context, Constants.PREFERENCE_AES_KEY, "user");
        preferences.edit()
                .remove("token")
                .remove("token_expire")
                .apply();
        reloadToken();
        updateLoginState(null);
    }

    public void updateLoginState(final UpdateLoginStateListener listener){
        if (token != null && tokenExpire != null && token.length() == 40){
            App.getRetrofit()
                    .create(ApiService.class)
                    .checkToken(token)
                    .enqueue(new ApiCallback<ApiResponse<CheckTokenResponse>>(true) {
                        @Override
                        public void onSuccess(ApiResponse<CheckTokenResponse> response) {
                            if (!response.getData().expired){
                                loginState = LoginState.LOGINED;
                                loadUserInfo(listener);
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            super.onFailure(t);
                            if (listener != null){
                                listener.onFinish();
                            }
                        }
                    });
        }else {
            loginState = LoginState.UNLOGIN;
            if (listener != null){
                listener.onFinish();
            }
        }
    }

    public void getLoginState(final GetLoginStateListener listener) {
        /*if (loginState == null){
            updateLoginState(null);
        }
        return loginState;*/
        if (loginState == null){
            updateLoginState(new UpdateLoginStateListener() {
                @Override
                public void onFinish() {
                    listener.onFinish(loginState);
                }
            });
        }else{
            listener.onFinish(loginState);
        }
    }

    public void getLoginedUserInfo(final GetLoginedUserInfoListener listener) {
        /*if (loginedUserInfo == null){
            updateLoginState(null);
        }
        return loginedUserInfo;*/
        if (loginedUserInfo == null){
            updateLoginState(new UpdateLoginStateListener() {
                @Override
                public void onFinish() {
                    listener.onFinish(loginedUserInfo);
                }
            });
        }else{
            listener.onFinish(loginedUserInfo);
        }
    }

    private void loadUserInfo(final UpdateLoginStateListener listener) {
        App.getRetrofit()
                .create(ApiService.class)
                .getUserInfo(null)
                .enqueue(new ApiCallback<ApiResponse<com.iydhp.app.mcmagicbox.data.model.User>>(true) {
                    @Override
                    public void onSuccess(ApiResponse<com.iydhp.app.mcmagicbox.data.model.User> response) {
                        loginedUserInfo = response.getData();
                        //设置Bugly上报的UserID
                        Bugly.setUserId(context, String.valueOf(loginedUserInfo.getId()));
                        StatConfig.setCustomUserId(context, String.valueOf(loginedUserInfo.getId()));

                        if (listener != null){
                            listener.onFinish();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        if (listener != null){
                            listener.onFinish();
                        }
                    }

                });
    }

    public String getToken() {
        return token;
    }
}
