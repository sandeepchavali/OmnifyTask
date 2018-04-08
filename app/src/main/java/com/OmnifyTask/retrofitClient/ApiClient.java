package com.OmnifyTask.retrofitClient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chavali on 2018-04-08.
 */

public class ApiClient {

    public static final String BASE_URL = "https://hacker-news.firebaseio.com/v0/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())

                    .build();
        }
        return retrofit;
    }
}
