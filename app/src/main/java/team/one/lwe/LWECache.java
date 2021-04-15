package team.one.lwe;

import android.content.Context;

import com.netease.nim.uikit.api.NimUIKit;

public class LWECache {
    private static Context context;
    private static String account;

    public static boolean noCache() {
        return account == null;
    }

    public static void clear() {
        account = null;
    }

    public static String getAccount() {
        return account;
    }

    public static void setAccount(String account) {
        LWECache.account = account;
        NimUIKit.setAccount(account);
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        LWECache.context = context.getApplicationContext();
    }
}
