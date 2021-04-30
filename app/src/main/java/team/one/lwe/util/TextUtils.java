package team.one.lwe.util;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;

import team.one.lwe.ui.activity.LoginActivity;

public class TextUtils {
    public static boolean isEmpty(String text) {
        return (text == null || text.length() == 0);
    }

    public static boolean isLegalUsername(@NotNull String text) {
        return text.matches("[_a-zA-Z0-9]+");
    }

    public static boolean isLegalPassword(@NotNull String text) {
        return text.matches("[-+=_,.a-zA-Z0-9]+");
    }

    public static int getPasswordComplexity(@NotNull String text) {
        int comp = 0;
        if (text.matches(".*[-+=_,.].*")) comp++;
        if (text.matches(".*[a-zA-Z].*")) comp++;
        if (text.matches(".*[0-9].*")) comp++;
        return comp;
    }

    public static void hideKeyboard(MotionEvent event, View view,
                                    Activity activity) {
        try {
            if (view instanceof EditText) {
                int[] location = { 0, 0 };
                view.getLocationInWindow(location);
                int left = location[0], top = location[1], right = left
                        + view.getWidth(), bootom = top + view.getHeight();
                if (event.getRawX() < left || event.getRawX() > right
                        || event.getY() < top || event.getRawY() > bootom) {
                    IBinder token = view.getWindowToken();
                    InputMethodManager inputMethodManager = (InputMethodManager) activity
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(token,
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
