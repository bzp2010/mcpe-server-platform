package com.iydhp.app.mcmagicbox.view.activity;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.iydhp.app.mcmagicbox.R;
import com.iydhp.app.mcmagicbox.data.adapter.PhotoAdapter;
import com.iydhp.app.mcmagicbox.databinding.ActivityPhotoBinding;
import com.iydhp.app.mcmagicbox.view.BaseActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhotoActivity extends BaseActivity {

    private ActivityPhotoBinding binding;
    private String[] urls;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_photo);
        urls = getIntent().getStringArrayExtra("urls");
        if(urls == null || urls.length == 0){
            Toast.makeText(this, "图片打开错误", Toast.LENGTH_SHORT).show();
            finish();
        }
        initView();
    }

    private void initView() {
        setSupportActionBar(binding.activityPhotoTbHeader);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<String> urlList = new ArrayList<>();
        for (String url : urls){
            urlList.add(url);
        }
        binding.activityPhotoVpContainer.setAdapter(new PhotoAdapter(getSupportFragmentManager(), urlList));
        binding.activityPhotoVpContainer.setOffscreenPageLimit(10);
        binding.activityPhotoVpContainer.setPageMargin(10);
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
