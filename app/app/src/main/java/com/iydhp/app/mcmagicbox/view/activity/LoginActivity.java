package com.iydhp.app.mcmagicbox.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.iydhp.app.mcmagicbox.App;
import com.iydhp.app.mcmagicbox.Constants;
import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.api.ApiCallback;
import com.iydhp.app.mcmagicbox.api.ApiResponse;
import com.iydhp.app.mcmagicbox.api.ApiService;
import com.iydhp.app.mcmagicbox.data.model.Event;
import com.iydhp.app.mcmagicbox.data.model.api.LoginResponse;
import com.iydhp.app.mcmagicbox.data.service.User;
import com.iydhp.app.mcmagicbox.databinding.ActivityLoginBinding;
import com.iydhp.app.mcmagicbox.view.BaseActivity;
import com.securepreferences.SecurePreferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private String action;

    private String loginUrl = "https://account.qi78.com/authorize?client_id=aaa384cbfe8a7641b59b3cd7114ec3c5&response_type=code&scope=user_id&redirect_uri=mgc%3A%2F%2Fapp%2Flogin&embedded=1";
    private String registerUrl = "https://account.qi78.com/user/register.html?return_url=%2Fauthorize%3Fclient_id%3Daaa384cbfe8a7641b59b3cd7114ec3c5%26amp%3Bresponse_type%3Dcode%26amp%3Bscope%3Duser_id%26amp%3Bredirect_uri%3Dmgc%253A%252F%252Fapp%252Flogin%26amp%3Bembedded%3D1&embedded=1";
    private String pwdResetUrl = "https://account.qi78.com/user/forget.html?return_url=%2Fauthorize%3Fclient_id%3Daaa384cbfe8a7641b59b3cd7114ec3c5%26amp%3Bresponse_type%3Dcode%26amp%3Bscope%3Duser_id%26amp%3Bredirect_uri%3Dmgc%253A%252F%252Fapp%252Flogin%26amp%3Bembedded%3D1&embedded=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        action = getIntent().getStringExtra("action");
        if (action == null) action = new String("login");
        switch (action){
            case "register":
                initView("注册");
                initRegisterView();
                break;
            case "resetpwd":
                initView("重置密码");
                initPwdResetView();
                break;
            case "loginFinish":
                initView("登录中");
                doLogin();
                break;
            case "logout":
                initView("退出账号");
                doLogout();
                break;
            default:
                initView("登录");
                initLoginView();
                break;
        }
    }

    private void initView(String toolbarTitle) {
        setSupportActionBar(binding.activityLoginTbHeader);
        getSupportActionBar().setTitle(toolbarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //WebView.setWebContentsDebuggingEnabled(true);
        binding.activityLoginWv.getSettings().setJavaScriptEnabled(true);
        binding.activityLoginWv.getSettings().setAppCacheEnabled(true);
        binding.activityLoginWv.getSettings().setAppCacheEnabled(true);
        binding.activityLoginWv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.indexOf("http://") != -1 || url.indexOf("https://") != -1){
                    view.loadUrl(url);
                }else{
                    CookieSyncManager cookieSyncManager =  CookieSyncManager.createInstance(binding.activityLoginWv.getContext());
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.removeSessionCookie();
                    cookieManager.removeAllCookie();

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });
        binding.activityLoginWv.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
                        .setMessage(message)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                result.confirm();
                                dialogInterface.dismiss();
                            }
                        }).show();
                return true;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    binding.activityLoginPb.setVisibility(View.GONE);
                } else {
                    if (View.INVISIBLE == binding.activityLoginPb.getVisibility()) {
                        binding.activityLoginPb.setVisibility(View.VISIBLE);
                    }
                    binding.activityLoginPb.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    private void initLoginView() {
        binding.activityLoginWv.loadUrl(loginUrl);
    }

    private void initRegisterView() {
        binding.activityLoginWv.loadUrl(registerUrl);
    }

    private void initPwdResetView() {
        binding.activityLoginWv.loadUrl(pwdResetUrl);
    }

    private void doLogin() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "登录中");
        String code = getIntent().getStringExtra("code");

        if (code != null && code != ""){
            User.getInstance(getApplicationContext())
                    .login(code, new User.LoginListener() {
                        @Override
                        public void onSuccess(User.LoginState loginState) {
                            if (progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            if (loginState == User.LoginState.LOGINED){
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                App.getEventBus().post(new Event(Event.EventId.LOGIN_FINISH, null));
                                finish();
                            }else{
                                onFailure(loginState);
                            }
                        }

                        @Override
                        public void onFailure(User.LoginState loginState) {
                            AlertDialog retryDialog = new AlertDialog.Builder(LoginActivity.this)
                                    .setMessage("登录失败")
                                    .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            doLogin();
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            finish();
                                        }
                                    })
                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialogInterface) {
                                            return;
                                        }
                                    })
                                    .show();
                        }
                    });
        }
    }

    private void doLogout() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (action){
            case "login":
                getMenuInflater().inflate(R.menu.menu_login, menu);
                break;
            case "register":
                getMenuInflater().inflate(R.menu.menu_login_register, menu);
                break;
            case "pwdreset":
                getMenuInflater().inflate(R.menu.menu_login_register, menu);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_login_item_register:
                intent.putExtra("action", "register");
                startActivity(intent);
                finish();
                break;
            case R.id.menu_login_item_login:
                intent.putExtra("action", "login");
                startActivity(intent);
                finish();
                break;
            case R.id.menu_login_item_resetpwd:
                intent.putExtra("action", "resetpwd");
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
