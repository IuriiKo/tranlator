package com.kushyk.translator.remote;

import com.kushyk.translator.util.RestQuery;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Iurii Kushyk on 26.12.2016.
 */

public class TranslateInterceptor implements Interceptor {

    public static final String KEY = "key";

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();
        final HttpUrl httpUrl = request.url().newBuilder().addEncodedQueryParameter(KEY, RestQuery.API_KEY).build();
        Request interceptRequest = request.newBuilder().url(httpUrl).build();

        return chain.proceed(interceptRequest);
    }
}
