package team.one.lwe.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.netease.nim.uikit.common.activity.UI;

import team.one.lwe.crash.ActivityCollector;

public abstract class LWEUI extends UI {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * 获取点击事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isHideInput(view, ev)) {
                IBinder token = null;
                if (view != null) {
                    token = view.getWindowToken();
                    view.clearFocus();
                }
                hideSoftInput(token);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判定是否需要隐藏
     */
    protected boolean isHideInput(View v, MotionEvent ev) {
        if ((v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            return !(ev.getX() > left) || !(ev.getX() < right) || !(ev.getY() > top) || !(ev.getY() < bottom);
        }
        return false;
    }

    /**
     * 隐藏软键盘
     */
    protected void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
