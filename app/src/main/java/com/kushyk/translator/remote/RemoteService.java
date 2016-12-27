package com.kushyk.translator.remote;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Iurii Kushyk on 26.12.2016.
 */

public class RemoteService {

    private  Retrofit retrofit;
    private TranslateService translateService;

    public RemoteService(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public static Retrofit initRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(initHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    private static OkHttpClient initHttpClient() {
        return  new OkHttpClient.Builder().
                addInterceptor(new TranslateInterceptor()).
                addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }

    public TranslateService getTranslateService() {
        if (translateService == null) {
            translateService = retrofit.create(TranslateService.class);
        }
        return translateService;
    }
}
