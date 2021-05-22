package team.one.lwe.config;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static final String KEY_USER_ACCOUNT = "account";
    private static final String KEY_USER_TOKEN = "token";

    public static void saveUserAccount(Context context, String account) {
        saveString(context, KEY_USER_ACCOUNT, account);
    }

    public static String getUserAccount(Context context) {
        return getString(context, KEY_USER_ACCOUNT);
    }

    public static void saveUserToken(Context context, String token) {
        saveString(context, KEY_USER_TOKEN, token);
    }

    public static String getUserToken(Context context) {
        return getString(context, KEY_USER_TOKEN);
    }

    public static void cleanCache(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(KEY_USER_ACCOUNT);
        editor.remove(KEY_USER_TOKEN);
        editor.commit();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("lwe_pref", Context.MODE_PRIVATE);
    }

    private static void saveString(Context context, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static String getString(Context context, String key) {
        return getSharedPreferences(context).getString(key, null);
    }
}
