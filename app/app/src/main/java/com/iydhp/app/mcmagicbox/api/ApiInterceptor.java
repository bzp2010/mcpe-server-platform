package com.iydhp.app.mcmagicbox.api;

import android.content.Context;

import com.iydhp.app.mcmagicbox.App;
import com.iydhp.app.mcmagicbox.data.service.User;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

public class ApiInterceptor implements Interceptor {

    private Context context;

    public ApiInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request oldRequest = chain.request();

        if (oldRequest.method() == "GET") return chain.proceed(oldRequest);

        /*if (!(oldRequest.body() instanceof FormBody)){
            return chain.proceed(oldRequest);
        }

        long timestamp = System.currentTimeMillis() / 1000;

        //读取POST参数
        Buffer buffer = new Buffer();
        oldRequest.body().writeTo(buffer);
        Charset charset = Charset.forName("UTF-8");
        MediaType contentType = oldRequest.body().contentType();
        if (contentType != null) {
            charset = contentType.charset(charset);
        }
        String paramsStr = buffer.readString(charset);
        Map<String, String> hashMap = APIUtils.getParamsMap(paramsStr, "utf-8");
        //读取GET参数
        String s = oldRequest.url().encodedPath();
        hashMap.put("s", s);
        //生成sign
        String sign = APIUtils.getSign(hashMap, timestamp);

        // 添加新的参数
        HttpUrl.Builder authorizedUrlBuilder = oldRequest.url()
                .newBuilder()
                .scheme(oldRequest.url().scheme())
                .host(oldRequest.url().host())
                .addQueryParameter("time", String.valueOf(timestamp))
                .addQueryParameter("sign", sign);*/

        if (oldRequest.body() instanceof MultipartBody){
            return chain.proceed(oldRequest);
        }

        // 新的请求
        FormBody.Builder newFormBody = new FormBody.Builder();

        if (oldRequest.body() != null && ((FormBody) oldRequest.body()).size() > 0){
            for (int i = 0; i < ((FormBody) oldRequest.body()).size(); i++){
                newFormBody.add(((FormBody) oldRequest.body()).name(i), ((FormBody) oldRequest.body()).value(i));
            }
        }

        String token = User.getInstance(context).getToken();
        if (token != null && !token.isEmpty()){
            newFormBody.add("token", token);
        }else{
            return chain.proceed(oldRequest);
        }

        Request newRequest = oldRequest.newBuilder()
                .method(oldRequest.method(), newFormBody.build())
                .url(oldRequest.url())
                .build();

        return chain.proceed(newRequest);
    }
}
