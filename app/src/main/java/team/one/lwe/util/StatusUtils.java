package team.one.lwe.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import team.one.lwe.LWEApplication;

public class StatusUtils {
    public static boolean isNetworkConnected() {
        ConnectivityManager manager = (ConnectivityManager) LWEApplication.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null) {
            return true;
        }else{
            return false;
        }
    }
}