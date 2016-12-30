package com.kushyk.translator.util;

import android.content.Context;
import android.net.ConnectivityManager;

import com.kushyk.translator.App;

/**
 * Created by Iurii Kushyk on 29.12.2016.
 */

public class NetUtil {
    public static boolean isNetworkConnected() {
        Context context = App.getContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
