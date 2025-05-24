package com.mrdeveloper.quickwash.Interface;

import com.google.gson.JsonObject;
import com.mrdeveloper.quickwash.Model.LaundryCategory;
import com.mrdeveloper.quickwash.Model.OrderRequest;
import com.mrdeveloper.quickwash.Model.Product;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

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

    @FormUrlEncoded
    @POST("get_user_by_phone.php")
    Call<ApiResponse> getUserByPhone(
            @Field("phone") String phone
    );

    @GET("get_categories.php")
    Call<ApiResponse> getCategories();

    // প্রোডাক্ট লোড (category_id optional)
    @GET("get_products.php")
    Call<ApiResponse> getProducts(@Query("category_id") int categoryId);

    @POST("submit_order.php")
    Call<ApiResponse> submitOrder(
            @Query("user_id") String userId,
            @Body List<Product> cartItems
    );

    @POST("submit_order.php")
    Call<ResponseBody> submitOrder(@Body OrderRequest orderRequest);

    @GET("get_orders.php")
    Call<List<OrderRequest>> getOrders(@Query("user_id") int user_id);

    @GET("get_order_status.php")
    Call<JsonObject> getOrderStatus(@Query("order_id") int orderId);

    @GET("orders_by_status.php")
    Call<List<OrderRequest>> getAllOrders(@Query("user_id") int userId);

    @GET("orders_by_status.php")
    Call<List<OrderRequest>> getOrdersByStatus(
            @Query("user_id") int userId,
            @Query("status") String status
    );

    @GET("get_orders_by_status_home.php")
    Call<List<OrderRequest>> getOrdersByStatusHome(
            @Query("user_id") int userId,
            @Query("status") String status
    );

    // ================== Update Profile
    @FormUrlEncoded
    @POST("update_profile.php")
    Call<ApiResponse> updateProfile(
            @Field("user_id") int userId,
            @Field("name") String name,
            @Field("email") String email,
            @Field("phone") String phone,
            @Field("gender") String gender,
            @Field("date_of_birth") String dob,
            @Field("address") String address
    );

    @Multipart
    @POST("upload_profile_image.php")
    Call<ApiResponse> uploadProfileImage(
            @Part("user_id") RequestBody userId,
            @Part MultipartBody.Part image
    );



}
