package com.iydhp.app.mcmagicbox.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.iydhp.app.mcmagicbox.App;

import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.api.ApiCallback;
import com.iydhp.app.mcmagicbox.api.ApiResponse;
import com.iydhp.app.mcmagicbox.api.ApiService;
import com.iydhp.app.mcmagicbox.data.model.Event;
import com.iydhp.app.mcmagicbox.data.model.api.UpdateAvatar;
import com.iydhp.app.mcmagicbox.data.service.User;
import com.iydhp.app.mcmagicbox.databinding.ActivityUserModifyBinding;
import com.iydhp.app.mcmagicbox.utils.FileUtils;
import com.socks.library.KLog;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UserModifyActivity extends AppCompatActivity {

    private ActivityUserModifyBinding binding;

    private final static int REQUEST_CODE_CHOOSE_AVATAR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User.getInstance(this).getLoginState(new User.GetLoginStateListener() {
            @Override
            public void onFinish(User.LoginState loginState) {
                if (loginState == User.LoginState.LOGINED){
                    binding = DataBindingUtil.setContentView(UserModifyActivity.this, R.layout.activity_user_modify);
                    initView();
                }else{
                    finish();
                }
            }
        });

    }

    private void initView() {
        setSupportActionBar(binding.activityUserModifyTbHeader);
        getSupportActionBar().setTitle("修改个人信息");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //nickname
        User.getInstance(this).getLoginedUserInfo(new User.GetLoginedUserInfoListener() {
            @Override
            public void onFinish(final com.iydhp.app.mcmagicbox.data.model.User loginedUserInfo) {
                binding.activityUserModifyTvNicknameField.setText(loginedUserInfo.getNickname());
                binding.activityUserModifyRlNickname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final EditText et = new EditText(UserModifyActivity.this);
                        et.setHint(loginedUserInfo.getNickname());

                        new AlertDialog.Builder(UserModifyActivity.this).setTitle("修改昵称")
                                .setView(et)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, int which) {
                                        String input = et.getText().toString();
                                        if (input == ""){
                                            Toast.makeText(UserModifyActivity.this, "请输入昵称", Toast.LENGTH_SHORT).show();
                                        }
                                        if (!input.equals(loginedUserInfo.getNickname())) {
                                            updateNickname(dialog, input);
                                        }else {
                                            Toast.makeText(UserModifyActivity.this, "新昵称不能与旧昵称相同", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .show();
                    }
                });

                //avatar
                Picasso.with(UserModifyActivity.this).load(loginedUserInfo.getAvatar()).into(binding.activityUserModifyCivHeadField);
                binding.activityUserModifyRlHead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Matisse.from(UserModifyActivity.this)
                                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.GIF))
                                .countable(true)
                                .maxSelectable(1)
                                //.gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                                .thumbnailScale(0.85f)
                                .imageEngine(new PicassoEngine())
                                .forResult(REQUEST_CODE_CHOOSE_AVATAR);
                    }
                });
            }
        });
    }

    /**
     * 更新昵称实际操作
     * @param nickname
     */
    public void updateNickname(final DialogInterface dialog , final String nickname){
        App.getRetrofit().create(ApiService.class)
                .updateNickname(nickname)
                .enqueue(new ApiCallback<ApiResponse>(false) {
                    @Override
                    public void onSuccess(ApiResponse response) {
                        super.onSuccess(response);
                        Toast.makeText(UserModifyActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        binding.activityUserModifyTvNicknameField.setText(nickname);
                        User.getInstance(UserModifyActivity.this).updateLoginState(new User.UpdateLoginStateListener() {
                            @Override
                            public void onFinish() {
                                App.getEventBus().post(new Event(Event.EventId.PROFILE_CHANGE, null));
                            }
                        });
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        Toast.makeText(UserModifyActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                });
    }

    private void uploadAvatar(File file){
        if (file.exists()){
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);

            MultipartBody.Part fileBody =
                    MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            MultipartBody.Part tokenBody =
                    MultipartBody.Part.createFormData("token", User.getInstance(this).getToken());

            App.getApp().getRetrofit().create(ApiService.class)
                    .updateAvatar(tokenBody, fileBody)
                    .enqueue(new ApiCallback<ApiResponse<UpdateAvatar>>(true) {
                        @Override
                        public void onSuccess(ApiResponse<UpdateAvatar> response) {
                            super.onSuccess(response);
                                Toast.makeText(UserModifyActivity.this, "修改成功", Toast.LENGTH_SHORT).show();

                                Picasso.with(UserModifyActivity.this).load(response.getData().getUrl()).into(binding.activityUserModifyCivHeadField);
                                User.getInstance(UserModifyActivity.this).updateLoginState(new User.UpdateLoginStateListener() {
                                    @Override
                                    public void onFinish() {
                                        App.getEventBus().post(new Event(Event.EventId.PROFILE_CHANGE, null));
                                    }
                                });
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            super.onFailure(t);
                            Toast.makeText(UserModifyActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_CHOOSE_AVATAR:
                if (resultCode == RESULT_OK) {
                    if (Matisse.obtainResult(data) != null) {
                        List<Uri> uris = Matisse.obtainResult(data);
                        if (uris.size() == 1) {
                            File file = new File(FileUtils.getRealFilePath(this, uris.get(0)));
                            ArrayList<String> ext = new ArrayList<String>();
                            KLog.i(file.getName());
                            KLog.i(file.getName().split("\\."));

                            for (int i = 0; i < file.getName().split("\\.").length; i++){
                                ext.add(file.getName().split("\\.")[i]);
                            }
                            if (ext.get(ext.size() - 1).toLowerCase().equals("gif")){
                                uploadAvatar(file);
                            }else{
                                Uri destination = Uri.fromFile(new File(getCacheDir(), file.getName()+".cropped.png"));
                                Crop.of(uris.get(0), destination)
                                        .withMaxSize(512,512)
                                        .asSquare().start(this);
                            }

                        }
                    } else {
                        Toast.makeText(UserModifyActivity.this, "请选择要修改的头像图片", Toast.LENGTH_SHORT).show();
                    }
                }else if (resultCode == Crop.RESULT_ERROR) {
                    Toast.makeText(UserModifyActivity.this, "图片错误", Toast.LENGTH_SHORT).show();
                }
                break;
            case Crop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    Uri uri = Crop.getOutput(data);
                    File file = new File(FileUtils.getRealFilePath(this, uri));
                    this.uploadAvatar(file);
                } else if (resultCode == Crop.RESULT_ERROR) {
                    Toast.makeText(this, Crop.getError(data).getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
