package com.iydhp.app.mcmagicbox.api;

import com.iydhp.app.mcmagicbox.BuildConfig;
import com.socks.library.KLog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ApiCallback<T> implements Callback<T> {

    private boolean hasData = false;

    public ApiCallback (boolean hasData) {
        this.hasData = hasData;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        //是否符合返回标准
        if (response.body() instanceof ApiResponse){
            ApiResponse result = (ApiResponse) response.body();
            if (result != null) {
                if (result.getStatus() == 200) {
                    //是否包含数据
                    if (hasData && result.getData() != null) {
                        onSuccess(response.body());
                    } else if (!hasData) {
                        onSuccess(response.body());
                    } else {
                        onFailure(new Exception(result.getMsg()));
                    }
                } else {
                    onFailure(new Exception(result.getMsg()));
                }
            } else {
                onFailure(new Exception("Server Error: Null Response"));
            }
        } else {
            onFailure(new Exception("Server Error: Unknown Response Type"));
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFailure(t);
    }

    public void onSuccess(T response){

    }

    public void onFailure(Throwable t){
        if (BuildConfig.DEBUG){
            KLog.e(t.getMessage());
        }
    }

}
