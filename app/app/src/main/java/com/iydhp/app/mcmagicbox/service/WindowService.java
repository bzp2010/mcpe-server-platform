package com.iydhp.app.mcmagicbox.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.iydhp.app.mcmagicbox.R;

public class WindowService extends Service {

    private boolean isGameFloatShowed = false;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        final WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        switch (intent.getStringExtra("action")){
            case "serverInfoV1":
                final WindowManager.LayoutParams joinServerInfoParams = new WindowManager.LayoutParams();
                // 类型
                joinServerInfoParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

                // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
                joinServerInfoParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                // 不设置这个弹出框的透明遮罩显示为黑色
                joinServerInfoParams.format = PixelFormat.TRANSLUCENT;
                // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
                // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
                // 不设置这个flag的话，home页的划屏会有问题

                joinServerInfoParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                joinServerInfoParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                joinServerInfoParams.gravity = Gravity.TOP | Gravity.LEFT;

                // 点击窗口外部区域可消除
                // 这点的实现主要将悬浮窗设置为全屏大小，外层有个透明背景，中间一部分视为内容区域
                // 所以点击内容区域外部视为点击悬浮窗外部
                final View joinServerInfo = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_alert_window_joinserver, null);// 非透明的内容区域

                TextView content = joinServerInfo.findViewById(R.id.layout_alert_window_joinserver_content);
                content.setText(intent.getStringExtra("content"));

                joinServerInfo.findViewById(R.id.layout_alert_window_joinserver_btn_confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        windowManager.removeView(joinServerInfo);
                        /*Intent intent = new Intent(getApplicationContext(), WindowService.class);
                        intent.putExtra("action", "gameFloatV1");
                        startService(intent);*/
                        stopSelf();
                    }
                });

                joinServerInfo.setOnTouchListener(new View.OnTouchListener() {
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
                                oddOffsetX = joinServerInfoParams.x;
                                oddOffsetY = joinServerInfoParams.y;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                float moveX = event.getX();
                                float moveY =  event.getY();
                                //不除以3，拖动的view抖动的有点厉害
                                joinServerInfoParams.x += (moveX - downX)/3;
                                joinServerInfoParams.y += (moveY - downY)/3;
                                if(joinServerInfo != null){
                                    windowManager.updateViewLayout(joinServerInfo, joinServerInfoParams);
                                }

                                break;
                            case MotionEvent.ACTION_UP:
                                int newOffsetX = joinServerInfoParams.x;
                                int newOffsetY = joinServerInfoParams.y;
                                break;
                        }
                        return false;
                    }
                });

                windowManager.addView(joinServerInfo, joinServerInfoParams);
                break;
            case "serverInfoV2":
                //FloatUtil.showFloatView();
                break;
            case "gameFloatV1":
                final WindowManager.LayoutParams gameFloatParams = new WindowManager.LayoutParams();

                // 类型
                gameFloatParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

                // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
                gameFloatParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                // 不设置这个弹出框的透明遮罩显示为黑色
                gameFloatParams.format = PixelFormat.TRANSLUCENT;
                // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
                // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
                // 不设置这个flag的话，home页的划屏会有问题

                gameFloatParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                gameFloatParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                gameFloatParams.gravity = Gravity.TOP | Gravity.LEFT;

                // 点击窗口外部区域可消除
                // 这点的实现主要将悬浮窗设置为全屏大小，外层有个透明背景，中间一部分视为内容区域
                // 所以点击内容区域外部视为点击悬浮窗外部
                final View gameFloatUnShow = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_game_float_unshow, null);// 非透明的内容区域
                final View gameFloatShow = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_game_float_show, null);// 非透明的内容区域

                gameFloatUnShow.findViewById(R.id.layout_game_float_unshow_civ_show).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        windowManager.removeView(gameFloatUnShow);
                        windowManager.addView(gameFloatShow, gameFloatParams);
                        isGameFloatShowed = true;
                    }
                });

                gameFloatUnShow.findViewById(R.id.layout_game_float_unshow_civ_show).setOnTouchListener(new View.OnTouchListener() {
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
                                oddOffsetX = gameFloatParams.x;
                                oddOffsetY = gameFloatParams.y;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                float moveX = event.getX();
                                float moveY =  event.getY();
                                //不除以3，拖动的view抖动的有点厉害
                                gameFloatParams.x += (moveX - downX)/3;
                                gameFloatParams.y += (moveY - downY)/3;
                                //更新UI
                                windowManager.updateViewLayout(gameFloatUnShow, gameFloatParams);

                                break;
                            case MotionEvent.ACTION_UP:
                                int newOffsetX = gameFloatParams.x;
                                int newOffsetY = gameFloatParams.y;
                                break;
                        }
                        return false;
                    }
                });

                gameFloatShow.setOnTouchListener(new View.OnTouchListener() {
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
                                oddOffsetX = gameFloatParams.x;
                                oddOffsetY = gameFloatParams.y;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                float moveX = event.getX();
                                float moveY =  event.getY();
                                //不除以3，拖动的view抖动的有点厉害
                                gameFloatParams.x += (moveX - downX)/3;
                                gameFloatParams.y += (moveY - downY)/3;
                                //更新UI
                                windowManager.updateViewLayout(gameFloatShow, gameFloatParams);

                                break;
                            case MotionEvent.ACTION_UP:
                                int newOffsetX = gameFloatParams.x;
                                int newOffsetY = gameFloatParams.y;
                                break;
                        }
                        return false;
                    }
                });

                gameFloatShow.findViewById(R.id.layout_game_float_show_btn_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        windowManager.removeView(gameFloatShow);
                        windowManager.addView(gameFloatUnShow, gameFloatParams);
                        isGameFloatShowed = false;
                    }
                });

                gameFloatShow.findViewById(R.id.layout_game_float_show_btn_record).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        makeMediaProjection();
                    }
                });

                windowManager.addView(gameFloatUnShow, gameFloatParams);
                break;
        }

        return START_REDELIVER_INTENT;
    }

    private void makeMediaProjection() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
