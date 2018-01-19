package com.iydhp.app.mcmagicbox.view.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.databinding.ActivityMainBinding;
import com.iydhp.app.mcmagicbox.view.BaseActivity;
import com.iydhp.app.mcmagicbox.view.fragment.MainFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.socks.library.KLog;
import com.tencent.bugly.beta.Beta;

import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Beta.checkUpgrade();
        checkPermissions();
        initView();
    }

    private void initView() {
        if (findFragment(MainFragment.class) == null) {
            loadRootFragment(binding.activityMainContainer.getId(), MainFragment.newInstance());
        }
    }

    private void checkPermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE
                        //Manifest.permission.SYSTEM_ALERT_WINDOW
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        for (int i = 0; i < report.getDeniedPermissionResponses().size(); i++){
                            switch (report.getDeniedPermissionResponses().get(i).getPermissionName()){
                                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                                    Toast.makeText(MainActivity.this, "必须给予文件权限", Toast.LENGTH_SHORT).show();
                                    Dexter.withActivity(MainActivity.this)
                                            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                                        @Override
                                        public void onPermissionGranted(PermissionGrantedResponse response) {

                                        }

                                        @Override
                                        public void onPermissionDenied(PermissionDeniedResponse response) {

                                        }

                                        @Override
                                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                                        }
                                    }).check();
                                    break;
                                case Manifest.permission.READ_EXTERNAL_STORAGE:
                                    Toast.makeText(MainActivity.this, "必须给予文件权限", Toast.LENGTH_SHORT).show();
                                    Dexter.withActivity(MainActivity.this)
                                            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                                        @Override
                                        public void onPermissionGranted(PermissionGrantedResponse response) {

                                        }

                                        @Override
                                        public void onPermissionDenied(PermissionDeniedResponse response) {

                                        }

                                        @Override
                                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                                        }
                                    }).check();;
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();
    }

    @Override
    public void onBackPressedSupport() {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("要退出吗！？")
                .setPositiveButton("要￣へ￣", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        System.exit(0);
                    }
                })
                .setNegativeButton("不要(〃'▽'〃)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }
}
