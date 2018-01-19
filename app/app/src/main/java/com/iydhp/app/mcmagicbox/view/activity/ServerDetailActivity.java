package com.iydhp.app.mcmagicbox.view.activity;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import android.widget.EditText;
import android.widget.Toast;

import com.iydhp.app.mcmagicbox.App;
import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.api.ApiCallback;
import com.iydhp.app.mcmagicbox.api.ApiResponse;
import com.iydhp.app.mcmagicbox.api.ApiService;
import com.iydhp.app.mcmagicbox.data.adapter.ServerDetailImagePagerAdapter;
import com.iydhp.app.mcmagicbox.data.model.Comment;
import com.iydhp.app.mcmagicbox.data.model.ServerInfo;
import com.iydhp.app.mcmagicbox.data.model.api.AddFavourite;
import com.iydhp.app.mcmagicbox.data.model.api.IsFavourited;
import com.iydhp.app.mcmagicbox.data.service.User;
import com.iydhp.app.mcmagicbox.databinding.ActivityServerDetailBinding;
import com.iydhp.app.mcmagicbox.databinding.LayoutServerSendCommentBinding;
import com.iydhp.app.mcmagicbox.service.WindowService;
import com.iydhp.app.mcmagicbox.utils.listener.AppBarStateChangeListener;
import com.iydhp.app.mcmagicbox.view.BaseActivity;
import com.iydhp.app.mcmagicbox.view.fragment.serverdetail.ServerDetailImageFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerDetailActivity extends BaseActivity {

    private long id;
    private ActivityServerDetailBinding binding;

    private boolean isFavourited = false;
    private long favouriteId = -1;

    private boolean loadFinish = false;
    private String serverName;
    private String serverIconUrl;
    private String serverInfoGameFloat;
    private String serverIp;
    private String serverPort;

    private Double userCommentScore;
    private String userCommentText;

    private final static int REQUEST_CODE_GET_PERMISSION = 1;
    private final static int REQUEST_CODE_MP = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_server_detail);
        this.id = getIntent().getLongExtra("id", -1);
        initView();
        //test();
    }

    private void test() {

        Log.i("tag", "test");
        File file = new File("/data/data/com.mojang.minecraftpe/games/com.mojang/minecraftpe/external_servers.txt");
        try {
            FileReader reader = new FileReader(file);
            int ch = 0;
            while((ch = reader.read()) != -1){
                char c = (char)ch;
                String s = String.valueOf(c);
                Log.i("reader", s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initView() {
        setSupportActionBar(binding.activityServerDetailTbHeader);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.activityServerDetailCltHeader.setTitle(" ");
        binding.activityServerDetailAblHeader.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.COLLAPSED){
                    binding.activityServerDetailCltHeader.setTitle(getString(R.string.app_name));
                }else if(state == State.IDLE && getCurrentState() == State.COLLAPSED){
                    binding.activityServerDetailCltHeader.setTitle(" ");
                }
            }
        });

        //评分列表
        binding.activityServerDetailBtnListscore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServerDetailActivity.this, ServerCommentActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        //添加评分
        binding.activityServerDetailBtnDoscore.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                final LayoutServerSendCommentBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.layout_server_send_comment, null, false);
                final AlertDialog dialog0 = new AlertDialog.Builder(ServerDetailActivity.this)
                        .setView(binding.getRoot())
                        .setTitle("添加评分")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                Double score = (double) binding.layoutServerSendCommentRbScore.getRating();
                                String text = binding.layoutServerSendCommentEtText.getText().toString();

                                final ProgressDialog progress = ProgressDialog.show(ServerDetailActivity.this, "", "提交中");
                                progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {return;}
                                });

                                if (score >= 0 && text != "") {
                                    App.getRetrofit()
                                            .create(ApiService.class)
                                            .addComment("server",id, score, text)
                                            .enqueue(new ApiCallback<ApiResponse>(false) {
                                                @Override
                                                public void onSuccess(ApiResponse response) {
                                                    super.onSuccess(response);
                                                    Toast.makeText(ServerDetailActivity.this, "评分成功", Toast.LENGTH_SHORT).show();
                                                    progress.dismiss();
                                                    dialog.dismiss();
                                                    restartSelfActivity();
                                                }

                                                @Override
                                                public void onFailure(Throwable t) {
                                                    super.onFailure(t);
                                                    Toast.makeText(ServerDetailActivity.this, "评分失败", Toast.LENGTH_SHORT).show();
                                                    progress.dismiss();
                                                }

                                            });

                                }else{
                                    Toast.makeText(ServerDetailActivity.this, "评分信息错误", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });

        //修改评分
        binding.activityServerDetailRlUsercomment.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                final LayoutServerSendCommentBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.layout_server_send_comment, null, false);
                binding.layoutServerSendCommentRbScore.setRating(Float.valueOf(String.valueOf(userCommentScore)));
                binding.layoutServerSendCommentEtText.setText(userCommentText);
                final AlertDialog dialog0 = new AlertDialog.Builder(ServerDetailActivity.this)
                        .setView(binding.getRoot())
                        .setTitle("修改评分")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                Double score = (double) binding.layoutServerSendCommentRbScore.getRating();
                                String text = binding.layoutServerSendCommentEtText.getText().toString();

                                final ProgressDialog progress = ProgressDialog.show(ServerDetailActivity.this, "", "提交中");
                                progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {return;}
                                });

                                if (score >= 0 && text != "") {
                                    App.getRetrofit()
                                            .create(ApiService.class)
                                            .editComment("server",id, score, text)
                                            .enqueue(new ApiCallback<ApiResponse>(false) {
                                                @Override
                                                public void onSuccess(ApiResponse response) {
                                                    super.onSuccess(response);
                                                    Toast.makeText(ServerDetailActivity.this, "评分成功", Toast.LENGTH_SHORT).show();
                                                    progress.dismiss();
                                                    dialog.dismiss();
                                                    restartSelfActivity();
                                                }

                                                @Override
                                                public void onFailure(Throwable t) {
                                                    super.onFailure(t);
                                                    Toast.makeText(ServerDetailActivity.this, "评分失败", Toast.LENGTH_SHORT).show();
                                                    progress.dismiss();
                                                }

                                            });

                                }else{
                                    Toast.makeText(ServerDetailActivity.this, "评分信息错误", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });

        //加载信息
        App.getRetrofit()
                .create(ApiService.class)
                .getServerInfo(id)
                .enqueue(new ApiCallback<ApiResponse<ServerInfo>>(true) {
                    @Override
                    public void onSuccess(ApiResponse<ServerInfo> response) {
                        final ServerInfo info = response.getData();
                        binding.activityServerDetailTvDesc.setText("“ "+info.getOneworddesc());
                        binding.activityServerDetailTvManager.setText(info.getManager());
                        binding.activityServerDetailTvName.setText(info.getName());
                        binding.activityServerDetailTvOnline.setText(Html.fromHtml("<font><strong>"+info.getNowPlayer()+"</strong></font><strong> / "+info.getMaxPlayer()+"</strong>"));
                        Picasso.with(ServerDetailActivity.this).load(info.getIcon()).into(binding.activityServerDetailIvIcon);
                        Picasso.with(ServerDetailActivity.this).load(info.getBackground()).into(binding.activityServerDetailIvHeader);

                        /*binding.activityServerDetailVpImage.setAdapter(new ServerDetailImagePagerAdapter(getSupportFragmentManager() , info.getImage()));
                        binding.activityServerDetailVpImage.setOffscreenPageLimit(10);
                        binding.activityServerDetailVpImage.setPageMargin(10);*/

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.activity_server_detail_fl_image, ServerDetailImageFragment.newInstance(info.getImage()))
                                .commitAllowingStateLoss();

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

                        /*binding.activityServerDetailBtnJoingame.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                joinGame(info.ip, info.port);
                            }
                        });*/
                        binding.activityServerDetailFabPlay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                joinGame(info.getIp(), info.getPort());
                            }
                        });

                        binding.activityServerDetailTvScore.setText(""+info.getScore());
                        binding.activityServerDetailTvScorerNum.setText(info.getScorernum()+"人已评分");
                        binding.activityServerDetailRbScore.setRating(Float.valueOf(String.valueOf(info.getScore())));
                        loadFinish = true;
                        serverName = info.getName();
                        serverIconUrl = info.getIcon();
                        //TODO IP PORT
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        Toast.makeText(ServerDetailActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                        //dialog.dismiss();
                    }

                });

        //加载个人评分信息
        App.getRetrofit()
                .create(ApiService.class)
                .getComment("server", id)
                .enqueue(new ApiCallback<ApiResponse<Comment>>(true) {
                    @Override
                    public void onSuccess(ApiResponse<Comment> response) {
                        super.onSuccess(response);
                        userCommentScore = response.getData().getScore();
                        userCommentText = response.getData().getText();
                        binding.activityServerDetailRbUsercomment.setRating(Float.valueOf(String.valueOf(response.getData().getScore())));
                        binding.activityServerDetailTvUsercomment.setText(response.getData().getText());
                        User.getInstance(getApplicationContext()).getLoginedUserInfo(new User.GetLoginedUserInfoListener() {
                            @Override
                            public void onFinish(com.iydhp.app.mcmagicbox.data.model.User loginedUserInfo) {
                                binding.activityServerDetailTvNickname.setText(loginedUserInfo.getNickname());
                                Picasso.with(ServerDetailActivity.this).load(loginedUserInfo.getAvatar()).into(binding.activityServerDetailCivHead);
                            }
                        });

                        //隐藏未评分而去评分的按钮
                        binding.activityServerDetailBtnDoscore.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        binding.activityServerDetailRlUsercomment.setVisibility(View.GONE);
                    }
                });

    }

    private void restartSelfActivity(){
        startActivity(getIntent());
        finish();
    }

    //以下是启动流程，悬浮窗等
    //是否有悬浮窗权限
    private boolean canDrawOverlays(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }
        return true;
    }

    //启动游戏
    private void joinGame(final String ip, final String port) {
        serverIp = ip;
        serverPort = port;
        serverInfoGameFloat = "IP: " + ip + "\n端口: " + port + "\nIP已复制到剪切板 请自行填写IP和端口\n请不要使用Steve名字进入游戏";
        //PreferencesUtils.PREFERENCE_NAME = "launcher0";
        //boolean isPermissionChecked = PreferencesUtils.getBoolean(getApplicationContext(),"alert_window_permission", false);
        if (!canDrawOverlays()) {
            //isToPermissionSetting = true;
            Toast.makeText(this, "请允许我的世界魔法盒子使用悬浮窗权限", Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,REQUEST_CODE_GET_PERMISSION);
            }else{
                getAppDetailSettingIntent();
            }
        }else{
            if (isAppAvilible("com.mojang.minecraftpe")){
                ClipboardManager manager = (ClipboardManager) ServerDetailActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setText(ip);

                showAlertWindow(serverInfoGameFloat);

                Intent intent = new Intent();
                ComponentName cn = new ComponentName("com.mojang.minecraftpe", "com.mojang.minecraftpe.MainActivity");
                intent.setComponent(cn);
                startActivity(intent);
            }else{
                Toast.makeText(this, "请先安装Minecraft PE", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showAlertWindow(String contentStr){
        Intent intent = new Intent(this, WindowService.class);
        intent.putExtra("action", "serverInfoV1");
        intent.putExtra("content", contentStr);
        startService(intent);
    }

    private void getAppDetailSettingIntent(){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_GET_PERMISSION:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                    if (Settings.canDrawOverlays(this)) {
                        //PreferencesUtils.PREFERENCE_NAME = "launcher0";
                        //PreferencesUtils.putBoolean(getApplicationContext(),"alert_window_permission", true);
                        joinGame(serverIp, serverPort);
                    }else{
                        Toast.makeText(this, "您还未允许我的世界魔法盒子使用悬浮窗权限", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case REQUEST_CODE_MP:
                showAlertWindow(serverInfoGameFloat);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_server_detail, menu);
        App.getRetrofit()
                .create(ApiService.class)
                .isFavourited("server", this.id)
                .enqueue(new ApiCallback<ApiResponse<IsFavourited>>(true) {
                    @Override
                    public void onSuccess(ApiResponse<IsFavourited> response) {
                        super.onSuccess(response);
                        if (response.getData().getFavourited()){
                            isFavourited = response.getData().getFavourited();
                            favouriteId = response.getData().getId();
                            menu.getItem(1).setIcon(R.drawable.ic_favorite_white_24dp);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                    }

                });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_server_detail_favourite:
                User.getInstance(this).getLoginState(new User.GetLoginStateListener() {
                    @Override
                    public void onFinish(User.LoginState loginState) {
                        if (loginState == User.LoginState.LOGINED){
                            if (isFavourited){
                                unfavourite();
                            }else{
                                favourite();
                            }
                        }else {
                            Intent intnet = new Intent(ServerDetailActivity.this, LoginActivity.class);
                            startActivity(intnet);
                            Toast.makeText(ServerDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                }else{

                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.isFavourited && this.favouriteId > 0){
            menu.getItem(1).setIcon(R.drawable.ic_favorite_white_24dp);
        }else{
            menu.getItem(1).setIcon(R.drawable.ic_favorite_border_white_24dp);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void unfavourite() {
        if (this.isFavourited && this.favouriteId > 0){
            App.getRetrofit()
                    .create(ApiService.class)
                    .deleteFavourite(this.favouriteId)
                    .enqueue(new ApiCallback<ApiResponse>(false) {
                        @Override
                        public void onSuccess(ApiResponse response) {
                            super.onSuccess(response);
                            isFavourited = false;
                            favouriteId = -1;
                            Toast.makeText(ServerDetailActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
                            invalidateOptionsMenu();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            super.onFailure(t);
                            Toast.makeText(ServerDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(ServerDetailActivity.this, "您还没有收藏", Toast.LENGTH_SHORT).show();
        }
    }

    private void favourite() {
        if (!this.isFavourited && this.favouriteId < 0){
            App.getRetrofit()
                    .create(ApiService.class)
                    .addFavourite("server", this.id)
                    .enqueue(new ApiCallback<ApiResponse<AddFavourite>>(true) {
                        @Override
                        public void onSuccess(ApiResponse<AddFavourite> response) {
                            super.onSuccess(response);
                            isFavourited = true;
                            favouriteId = response.getData().getId();
                            Toast.makeText(ServerDetailActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
                            invalidateOptionsMenu();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            super.onFailure(t);
                            Toast.makeText(ServerDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });
        }else{
            Toast.makeText(ServerDetailActivity.this, "您已经收藏了", Toast.LENGTH_SHORT).show();
        }
    }

}
