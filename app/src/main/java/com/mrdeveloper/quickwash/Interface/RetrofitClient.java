package com.mrdeveloper.quickwash.Interface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://quickwash.mrdeveloper.xyz/"; // আপনার সার্ভারের ঠিকানা দিন
    private static Retrofit retrofit = null;

    public static ApiInterface getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiInterface.class);
    }
}
