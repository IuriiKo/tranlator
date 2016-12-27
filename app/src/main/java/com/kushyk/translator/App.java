package com.kushyk.translator;

import android.app.Application;

import com.kushyk.translator.remote.RemoteService;
import com.kushyk.translator.remote.UrlList;

/**
 * Created by Iurii Kushyk on 26.12.2016.
 */

public class App extends Application {

    public static RemoteService service;

    @Override
    public void onCreate() {
        super.onCreate();
        initRemoteService();
    }

    private static void initRemoteService() {
        service = new RemoteService(RemoteService.initRetrofit(UrlList.BASE_URL));
    }
}
