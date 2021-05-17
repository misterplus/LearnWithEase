package team.one.lwe.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.netease.nim.uikit.common.activity.UI;

import team.one.lwe.LWECache;
import team.one.lwe.R;

public class WelcomeActivity extends UI {

    private static final boolean DEV_FRONT = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        // redirect to login in 3 seconds
        new Handler(msg -> {
            if (LWECache.noCache()) {
                // redirect to main if is developing frontend
                startActivity(new Intent(this, DEV_FRONT ? MainActivity.class : LoginActivity.class));
            } else {
                //TODO: go to main page
                startActivity(new Intent(this, DEV_FRONT ? MainActivity.class : LoginActivity.class));
            }
            return false;
        }).sendEmptyMessageDelayed(0, 3000);
    }
}
