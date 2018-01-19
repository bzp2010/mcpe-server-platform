package com.iydhp.app.mcmagicbox.api;

import com.iydhp.app.mcmagicbox.data.model.Comment;
import com.iydhp.app.mcmagicbox.data.model.ServerCategoryItem;
import com.iydhp.app.mcmagicbox.data.model.ServerInfo;
import com.iydhp.app.mcmagicbox.data.model.ServerListItem;
import com.iydhp.app.mcmagicbox.data.model.Splash;
import com.iydhp.app.mcmagicbox.data.model.User;
import com.iydhp.app.mcmagicbox.data.model.api.AddFavourite;
import com.iydhp.app.mcmagicbox.data.model.api.CheckTokenResponse;
import com.iydhp.app.mcmagicbox.data.model.api.IsFavourited;
import com.iydhp.app.mcmagicbox.data.model.api.LoginResponse;
import com.iydhp.app.mcmagicbox.data.model.api.UpdateAvatar;
import com.iydhp.app.mcmagicbox.data.model.api.UserFavourite;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/app/getSplashImage")
    Call<ApiResponse<Splash>> getSplash();

    @GET("api/getCategory")
    Call<ApiResponse<ServerCategoryItem[]>> getCategory();

    @GET("api/getServerList")
    Call<ApiResponse<ServerListItem[]>> getServerList(@Query("category") Long category);

    @POST("api/server/search")
    @FormUrlEncoded
    Call<ApiResponse<ApiResponsePage<ServerListItem[]>>> searchServer(@Field("key") String key, @Field("page") Long page);

    @GET("api/getServerInfo")
    Call<ApiResponse<ServerInfo>> getServerInfo(@Query("id") Long id);

    @POST("api/server/getComment")
    @FormUrlEncoded
    Call<ApiResponse<Comment>> getComment(@Field("type") String type, @Field("serverid") Long serverid);

    @POST("user/app/login")
    @FormUrlEncoded
    Call<ApiResponse<LoginResponse>> login(@Field("code") String code);

    @POST("api/user/getUserInfo")
    @FormUrlEncoded
    Call<ApiResponse<User>> getUserInfo(@Field("type") String token);

    @POST("api/user/updateNickname")
    @FormUrlEncoded
    Call<ApiResponse> updateNickname(@Field("nickname") String nickname);

    @POST("api/user/updateAvatar")
    @Multipart
    Call<ApiResponse<UpdateAvatar>> updateAvatar(@Part MultipartBody.Part token, @Part MultipartBody.Part file);

    @POST("api/server/isFavourited")
    @FormUrlEncoded
    Call<ApiResponse<IsFavourited>> isFavourited(@Field("type") String type, @Field("id") Long serverid);

    @POST("api/server/addFavourite")
    @FormUrlEncoded
    Call<ApiResponse<AddFavourite>> addFavourite(@Field("type") String type, @Field("id") Long serverid);

    @POST("api/server/deleteFavourite")
    @FormUrlEncoded
    Call<ApiResponse> deleteFavourite(@Field("id") Long favouriteId);

    @POST("user/app/checkToken")
    @FormUrlEncoded
    Call<ApiResponse<CheckTokenResponse>> checkToken(@Field("token") String token);

    @POST("api/server/listFavourite")
    @FormUrlEncoded
    Call<ApiResponse<ApiResponsePage<UserFavourite[]>>> listFavourite(@Field("page") Long page);

    @POST("api/server/accusationComment")
    @FormUrlEncoded
    Call<ApiResponse> accusationComment(@Field("id") Long id);

    @POST("api/server/listComment")
    @FormUrlEncoded
    Call<ApiResponse<ApiResponsePage<Comment[]>>> listComment(@Field("page") Long page, @Field("type") String type, @Field("serverid") Long serverid);

    @POST("api/server/addComment")
    @FormUrlEncoded
    Call<ApiResponse> addComment(@Field("type") String type, @Field("serverid") Long serverid, @Field("score") Double score, @Field("text") String text);

    @POST("api/server/editComment")
    @FormUrlEncoded
    Call<ApiResponse> editComment(@Field("type") String type, @Field("serverid") Long serverid, @Field("score") Double score, @Field("text") String text);

}
