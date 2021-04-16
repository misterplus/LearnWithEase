package team.one.lwe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.netease.nim.uikit.common.activity.UI;

import team.one.lwe.R;

public class WelcomeActivity extends UI {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        // redirect to login in 3 seconds
        new Handler(msg -> {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            return false;
        }).sendEmptyMessageDelayed(0, 3000);
    }
}
