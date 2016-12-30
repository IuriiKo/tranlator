package com.kushyk.translator;

import android.app.Application;
import android.content.Context;

/**
 * Created by Iurii Kushyk on 26.12.2016.
 */

public class App extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

    }

    public static Context getContext() {
        return context;
    }
}
