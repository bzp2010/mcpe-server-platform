package com.iydhp.app.mcmagicbox.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.databinding.ActivityServerDetailBinding;
import com.iydhp.app.mcmagicbox.view.BaseActivity;

import java.util.List;

public class ServerDetailActivity0 extends BaseActivity {

    private long id;
    private ActivityServerDetailBinding binding;

    private boolean isFavourited = false;
    private long favouriteId = -1;

    private final static int REQUEST_CODE_DO_COMMENT = 1;

    private boolean loadFinish = false;
    private String serverName;
    private String serverIconUrl;

    private boolean joinGame = false;

    private boolean isToPermissionSetting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_server_detail);
        this.id = getIntent().getLongExtra("id", 1l);
        this.joinGame = getIntent().getBooleanExtra("joinGame", false);
        initView();
    }

    private void initView() {
        //setSupportActionBar(binding.activityServerDetailTbHeader);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*binding.activityServerDetailBtnDoscore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*if (App.getApp().isLogined()) {
                    Intent intent = new Intent(ServerDetailActivity.this, ServerSendCommentActivity.class);
                    intent.putExtra("id", id);
                    startActivityForResult(intent, REQUEST_CODE_DO_COMMENT);
                }else{
                    Intent intnet = new Intent(ServerDetailActivity.this, LoginActivity.class);
                    startActivity(intnet);
                    ToastUtils.show(ServerDetailActivity.this, "请先登录");
                }*//*
            }
        });
        binding.activityServerDetailBtnListscore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*Intent intent = new Intent(ServerDetailActivity.this, ServerCommentActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);*//*
            }
        });*/
        loadData();
    }

    private void loadData() {
        /*final ProgressDialog dialog = ProgressDialog.show(this, "", "加载中");
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                return;
            }
        });
        loadMyCommentData(dialog);
        App.getRetrofit()
                .create(ApiService.class)
                .getServerInfo(this.id)
                .enqueue(new ApiCallback<ApiResponse<ServerInfo>>(true) {
                    @Override
                    public void onSuccess(ApiResponse<ServerInfo> response) {
                        final ServerInfo info = response.getData();
                        binding.activityServerDetailTvDesc.setText(info.getOneworddesc());
                        binding.activityServerDetailTvManager.setText(info.getManager());
                        binding.activityServerDetailTvName.setText(info.getName());
                        Picasso.with(ServerDetailActivity.this).load(info.getIcon()).into(binding.activityServerDetailIvIcon);

                        binding.activityServerDetailVpImage.setAdapter(new ServerDetailImageAdapter(ServerDetailActivity.this , info.getImage()));
                        binding.activityServerDetailVpImage.setOffscreenPageLimit(5);
                        binding.activityServerDetailVpImage.setPageMargin(10);
                        binding.activityServerDetailTvMoreinfo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new AlertDialog.Builder(ServerDetailActivity.this)
                                        .setTitle("简介")
                                        .setMessage(Html.fromHtml(info.getDesc()))
                                        .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        });
                        binding.activityServerDetailBtnJoingame.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //joinGame(info.getIp(), info.getPort());
                            }
                        });
                        binding.activityServerDetailTvScore.setText(""+info.getScore());
                        binding.activityServerDetailTvScorerNum.setText(info.getScorernum()+"人已评分");
                        binding.activityServerDetailRbScore.setRating(Float.valueOf(String.valueOf(info.getScore())));
                        if (joinGame){
                            //joinGame(info.getIp(), info.getPort());
                        }
                        loadFinish = true;
                        serverName = info.getName();
                        serverIconUrl = info.getIcon();
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        Toast.makeText(ServerDetailActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                    }

                });*/
    }

    /*private boolean canDrawOverlays(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }
        return true;
    }*/

    /*private void joinGame(final String ip, final String port) {
        PreferencesUtils.PREFERENCE_NAME = "launcher0";
        boolean isPermissionChecked = PreferencesUtils.getBoolean(getApplicationContext(),"alert_window_permission", false);
        if (!canDrawOverlays()) {
            isToPermissionSetting = true;
            ToastUtils.show(ServerDetailActivity.this,"请允许MC魔法盒子使用悬浮窗权限");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,10);
            }else{
                getAppDetailSettingIntent();
            }
        }else{
            if (isAppAvilible("com.mojang.minecraftpe")){
                ClipboardManager manager = (ClipboardManager) ServerDetailActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setText(ip);
                showAlertWindow("服务器IP: " + ip + "，已复制到剪切板\n服务器的端口: " + port + "，请自行填写\n请注意\n不要使用Steve名字进入游戏");
                Intent intent = new Intent();
                ComponentName cn = new ComponentName("com.mojang.minecraftpe", "com.mojang.minecraftpe.MainActivity");
                intent.setComponent(cn);
                startActivity(intent);
            }else{
                ToastUtils.show(ServerDetailActivity.this, "请先安装Minecraft PE");
            }
        }
    }

    public void showAlertWindow(String contentStr){
        final WindowManager mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        // 类型
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        // WindowManager.LayoutParams.TYPE_SYSTEM_ALERT

        // 设置flag

        int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
        params.flags = flags;
        // 不设置这个弹出框的透明遮罩显示为黑色
        params.format = PixelFormat.TRANSLUCENT;
        // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
        // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
        // 不设置这个flag的话，home页的划屏会有问题

        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        params.gravity = Gravity.TOP | Gravity.LEFT;

        // 点击窗口外部区域可消除
        // 这点的实现主要将悬浮窗设置为全屏大小，外层有个透明背景，中间一部分视为内容区域
        // 所以点击内容区域外部视为点击悬浮窗外部
        final View popupWindowView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_alert_window_joinserver, null);// 非透明的内容区域

        TextView content = popupWindowView.findViewById(R.id.layout_alert_window_joinserver_content);
        content.setText(contentStr);

        popupWindowView.findViewById(R.id.layout_alert_window_joinserver_btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindowManager.removeView(popupWindowView);
            }
        });

        popupWindowView.setOnTouchListener(new View.OnTouchListener() {
            float downX = 0;
            float downY = 0;
            int oddOffsetX = 0;
            int oddOffsetY = 0;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        downX =  event.getX();
                        downY =  event.getY();
                        oddOffsetX = params.x;
                        oddOffsetY = params.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float moveX = event.getX();
                        float moveY =  event.getY();
                        //不除以3，拖动的view抖动的有点厉害
                        params.x += (moveX - downX)/3;
                        params.y += (moveY - downY)/3;
                        if(popupWindowView != null){
                            mWindowManager.updateViewLayout(popupWindowView,params);
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        int newOffsetX = params.x;
                        int newOffsetY = params.y;
                        break;
                }
                return false;
            }
        });

        mWindowManager.addView(popupWindowView, params);
    }*/

    /*private void getAppDetailSettingIntent(){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
    }*/

    private void loadMyCommentData(final Dialog dialog) {
        /*App.getApp().getRetrofit().create(MApi.class)
                .getComment("server", this.id)
                .enqueue(new Callback<APIReturn<APIReturnData.APIReturnDataGetComment>>() {
                    @Override
                    public void onResponse(Call<APIReturn<APIReturnData.APIReturnDataGetComment>> call, Response<APIReturn<APIReturnData.APIReturnDataGetComment>> response) {
                        if (response.body() != null && response.body().getData() != null && response.body().getStatus() == 200){
                            final APIReturnData.APIReturnDataGetComment data = response.body().getData();
                            if (App.getApp().getLoginedUserInfo().nickname != null){
                                binding.activityServerDetailTvNickname.setText(App.getApp().getLoginedUserInfo().nickname);
                            }
                            binding.activityServerDetailRbUsercomment.setRating(data.score.floatValue());
                            binding.activityServerDetailTvUsercomment.setText(data.text);
                            Picasso.with(ServerDetailActivity.this).load(App.getApp().getLoginedUserInfo().avatar).into(binding.activityServerDetailCivHead);
                            binding.activityServerDetailRlUsercomment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (App.getApp().isLogined()) {
                                        Intent intent = new Intent(ServerDetailActivity.this, ServerSendCommentActivity.class);
                                        intent.putExtra("text", data.text);
                                        intent.putExtra("score", data.score.floatValue());
                                        intent.putExtra("id", id);
                                        startActivityForResult(intent, REQUEST_CODE_DO_COMMENT);
                                    }else{
                                        Intent intnet = new Intent(ServerDetailActivity.this, LoginActivity.class);
                                        startActivity(intnet);
                                        ToastUtils.show(ServerDetailActivity.this, "请先登录");
                                    }
                                }
                            });
                            binding.activityServerDetailBtnDoscore.setVisibility(View.GONE);
                        }else{
                            binding.activityServerDetailRlUsercomment.setVisibility(View.GONE);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<APIReturn<APIReturnData.APIReturnDataGetComment>> call, Throwable t) {
                        dialog.dismiss();
                    }
                });*/
    }

    private boolean isAppAvilible(String packageName) {
        final PackageManager packageManager = getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for ( int i = 0; i < pinfo.size(); i++ )
        {
            if(pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    /*@Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_server_detail, menu);
        App.getApp().getRetrofit().create(MApi.class)
                .isFavourited("server", this.id)
                .enqueue(new Callback<APIReturn<APIReturnData.APIReturnDataIsFavourited>>() {
                    @Override
                    public void onResponse(Call<APIReturn<APIReturnData.APIReturnDataIsFavourited>> call, Response<APIReturn<APIReturnData.APIReturnDataIsFavourited>> response) {
                        if (response.body() != null && response.body().getData() != null){
                            if (response.body().getData().favourited){
                                isFavourited = response.body().getData().favourited;
                                favouriteId = response.body().getData().id;
                                menu.getItem(1).setIcon(R.drawable.ic_favorite_white_24dp);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<APIReturn<APIReturnData.APIReturnDataIsFavourited>> call, Throwable t) {

                    }
                });
        return super.onCreateOptionsMenu(menu);
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_server_detail_favourite:
                if (App.getApp().isLogined()){
                    if (this.isFavourited){
                        unfavourite();
                    }else{
                        favourite();
                    }
                }else{
                    Intent intnet = new Intent(ServerDetailActivity.this, LoginActivity.class);
                    startActivity(intnet);
                    ToastUtils.show(ServerDetailActivity.this, "请先登录");
                }
                break;
            case R.id.menu_server_detail_share:
                if (loadFinish){
                    String title = serverName;
                    String desc = "快来和我一起玩吧！";
                    String url = "https://sv.qi78.com/info.html?id="+this.id;
                    OnekeyShare oks = new OnekeyShare();
                    oks.disableSSOWhenAuthorize();
                    oks.setTitle(title);
                    oks.setText(desc);
                    oks.setTitleUrl(url);
                    oks.setUrl(url);
                    oks.setImageUrl(serverIconUrl);
                    oks.show(this);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

    /*private void unfavourite() {
        if (this.isFavourited && this.favouriteId > 0){
            App.getApp().getRetrofit().create(MApi.class)
                    .deleteFavourite(this.favouriteId)
                    .enqueue(new Callback<APIReturn>() {
                        @Override
                        public void onResponse(Call<APIReturn> call, Response<APIReturn> response) {
                            if (response.body() != null){
                                if (response.body().getStatus() == 200){
                                    isFavourited = false;
                                    favouriteId = -1;
                                    ToastUtils.show(ServerDetailActivity.this, response.body().getMsg());
                                    invalidateOptionsMenu();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<APIReturn> call, Throwable t) {
                            ToastUtils.show(ServerDetailActivity.this, "取消收藏失败");
                        }
                    });
        }else{
            ToastUtils.show(ServerDetailActivity.this, "您还没有收藏");
        }
    }

    private void favourite() {
        if (!this.isFavourited && this.favouriteId < 0){
            App.getApp().getRetrofit().create(MApi.class)
                    .addFavourite("server", this.id)
                    .enqueue(new Callback<APIReturn<APIReturnData.APIReturnDataAddFavourite>>() {
                        @Override
                        public void onResponse(Call<APIReturn<APIReturnData.APIReturnDataAddFavourite>> call, Response<APIReturn<APIReturnData.APIReturnDataAddFavourite>> response) {
                            if (response.body() != null){
                                if (response.body().getStatus() == 200){
                                    isFavourited = true;
                                    favouriteId = response.body().getData().id;
                                    ToastUtils.show(ServerDetailActivity.this, response.body().getMsg());
                                    invalidateOptionsMenu();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<APIReturn<APIReturnData.APIReturnDataAddFavourite>> call, Throwable t) {
                            ToastUtils.show(ServerDetailActivity.this, "收藏失败");
                        }
                    });
        }else{
            ToastUtils.show(ServerDetailActivity.this, "您已经收藏了");
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.isFavourited && this.favouriteId > 0){
            menu.getItem(1).setIcon(R.drawable.ic_favorite_white_24dp);
        }else{
            menu.getItem(1).setIcon(R.drawable.ic_favorite_border_white_24dp);
        }
        return super.onPrepareOptionsMenu(menu);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_DO_COMMENT:
                if (resultCode == 1){
                    Intent intent = new Intent(ServerDetailActivity0.this, ServerDetailActivity0.class);
                    intent.putExtra("id",this.id);
                    startActivity(intent);
                    finish();
                }
                break;
            case 10:
                /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                    if (Settings.canDrawOverlays(this)) {
                        PreferencesUtils.PREFERENCE_NAME = "launcher0";
                        PreferencesUtils.putBoolean(getApplicationContext(),"alert_window_permission", true);

                        Intent daemon = new Intent(this, DaemonService.class);
                        daemon.putExtra("action", "restart");
                        daemon.putExtra("packageName", getPackageName());
                        daemon.putExtra("delayed", 500);
                        startService(daemon);
                        System.exit(0);

                    }else{
                        ToastUtils.show(getApplicationContext(), "未允许使用悬浮窗权限");
                    }
                }*/
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
