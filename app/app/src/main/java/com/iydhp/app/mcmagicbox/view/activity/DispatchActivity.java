package com.iydhp.app.mcmagicbox.view.activity;

import android.content.Intent;
import android.os.Bundle;

import com.iydhp.app.mcmagicbox.view.BaseActivity;

public class DispatchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getIntent().getData() != null){
            Toast.makeText(this, getIntent().getData().getPath(), Toast.LENGTH_LONG).show();
            Toast.makeText(this, getIntent().getData().getQuery(), Toast.LENGTH_LONG).show();
        }*/
        Intent intent = null;
        switch (getIntent().getData().getPath()){
            case "/login":
                intent = new Intent(DispatchActivity.this, LoginActivity.class);
                intent.putExtra("code", getIntent().getData().getQuery().split("=")[1]);
                intent.putExtra("action", "loginFinish");
                break;
            case "/serverInfo":
                intent = new Intent(DispatchActivity.this, ServerDetailActivity.class);
                intent.putExtra("id", Long.valueOf(getIntent().getData().getQuery().split("=")[1]));
                break;
            case "/serverEntry":
                intent = new Intent(DispatchActivity.this, ServerDetailActivity.class);
                intent.putExtra("id", Long.valueOf(getIntent().getData().getQuery().split("=")[1]));
                intent.putExtra("joinGame", true);
                break;
        }

        startActivity(intent);
        finish();
    }
}
