package com.iydhp.app.mcmagicbox;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.iydhp.app.mcmagicbox.api.ApiInterceptor;
import com.iydhp.app.mcmagicbox.data.service.User;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.stat.MtaSDkException;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;
import com.tencent.stat.common.StatConstants;

import org.greenrobot.eventbus.EventBus;

import me.yokeyword.fragmentation.Fragmentation;
import me.yokeyword.fragmentation.helper.ExceptionHandler;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    private static App app;
    private static Retrofit retrofit;
    //private static DaoSession daoSession;
    private OkHttpClient client;
    //private EventBus eventBus;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        //initEventBus();
        initMta();
        initOkHttp();
        initRetrofit();
        initFragmentation();

        User.getInstance(this).updateLoginState(null);
        //initDatabase();
        //initPicasso();
        //updateLoginState();
        Beta.autoCheckUpgrade = true;
        Beta.autoDownloadOnWifi = true;
        Bugly.init(getApplicationContext(), "e25affcb7a", false);
    }

    private void initMta() {
        StatConfig.setAutoExceptionCaught(false);
        try {
            StatService.startStatService(this, "AQ8ZC9W9SI9M", StatConstants.VERSION);
        } catch (MtaSDkException e) {
            e.printStackTrace();
        }
        StatService.trackCustomEvent(this, "onCreate", "");
    }

    /*private void initDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "mcmagicbox", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }*/

    private void initFragmentation() {
        Fragmentation.builder()
                // 设置 栈视图 模式为 悬浮球模式   SHAKE: 摇一摇唤出   NONE：隐藏
                .stackViewMode(Fragmentation.NONE)
                // ture时，遇到异常："Can not perform this action after onSaveInstanceState!"时，会抛出
                // false时，不会抛出，会捕获，可以在handleException()里监听到
                .debug(BuildConfig.DEBUG)
                // 在debug=false时，即线上环境时，上述异常会被捕获并回调ExceptionHandler
                .handleException(new ExceptionHandler() {
                    @Override
                    public void onException(Exception e) {
                        // 建议在该回调处上传至我们的Crash监测服务器
                        // 以Bugtags为例子: 手动把捕获到的 Exception 传到 Bugtags 后台。
                        // Bugtags.sendException(e);
                        StatService.reportException(App.this, e);
                    }
                })
                .install();
    }

    private void initOkHttp() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new ApiInterceptor(this));
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        client = builder.build();
    }

    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.MSP_API_URL_PRODUCT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static App getApp() {
        return app;
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    public static EventBus getEventBus() {
        return EventBus.getDefault();
    }

}
