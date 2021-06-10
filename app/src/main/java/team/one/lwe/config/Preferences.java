package team.one.lwe.config;

import android.content.Context;
import android.content.SharedPreferences;

import team.one.lwe.LWEApplication;

public class Preferences {
    private static final String KEY_USER_ACCOUNT = "account";
    private static final String KEY_USER_TOKEN = "token";
    private static final String KEY_VIDEO_QUALITY = "quality_video";

    public static int getVideoQuality() {
        return getInt(KEY_VIDEO_QUALITY, 0);
    }

    public static void setVideoQuality(int quality) {
        saveInt(KEY_VIDEO_QUALITY, quality);
    }

    public static void saveUserAccount(String account) {
        saveString(KEY_USER_ACCOUNT, account);
    }

    public static String getUserAccount() {
        return getString(KEY_USER_ACCOUNT);
    }

    public static void saveUserToken(String token) {
        saveString(KEY_USER_TOKEN, token);
    }

    public static String getUserToken() {
        return getString(KEY_USER_TOKEN);
    }

    public static void cleanCache() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.remove(KEY_USER_ACCOUNT);
        editor.remove(KEY_USER_TOKEN);
        editor.commit();
    }

    private static SharedPreferences getSharedPreferences() {
        return LWEApplication.getInstance().getApplicationContext().getSharedPreferences("lwe_pref", Context.MODE_PRIVATE);
    }

    private static int getInt(String key, int def) {
        return getSharedPreferences().getInt(key, def);
    }

    private static void saveInt(String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private static void saveString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static String getString(String key) {
        return getSharedPreferences().getString(key, null);
    }
}
