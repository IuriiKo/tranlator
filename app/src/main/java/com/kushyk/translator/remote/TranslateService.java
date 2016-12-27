package com.kushyk.translator.remote;

import com.kushyk.translator.remote.response.TranslateDetectResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Iurii Kushyk on 26.12.2016.
 */

public interface TranslateService {
    @GET("detect?fields=detections")
    Observable<TranslateDetectResponse> detect(@Query("q") String text);
}
