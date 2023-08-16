package com.example.mtg_tome.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    private RetrofitClient() {
        // private constructor to prevent instantiation
    }

    public static Retrofit getInstance() {
        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    // Create a new interceptor
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                    //Add the interceptor to the OkHttpClient
                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(logging)
                            .build();

                    retrofit = new Retrofit.Builder()
                            .baseUrl("https://api.scryfall.com/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(client)
                            .build();
                }
            }
        }
        return retrofit;
    }
}
