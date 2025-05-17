package com.mrdeveloper.quickwash.Interface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("sign_up.php")
    Call<ApiResponse> signUp(
      @Field("name") String name,
      @Field("phone") String phone,
      @Field("password") String password
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<ApiResponse> getLoginResponse(
            @Field("phone") String phone,
            @Field("password") String password
    );

}
