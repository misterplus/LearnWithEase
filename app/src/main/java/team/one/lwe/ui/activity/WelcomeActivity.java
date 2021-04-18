package team.one.lwe.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.activity.UI;

import team.one.lwe.LWECache;
import team.one.lwe.R;

public class WelcomeActivity extends UI {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        // redirect to login in 3 seconds
        new Handler(msg -> {
            if (LWECache.noCache()) {
                startActivity(new Intent(this, LoginActivity.class));
            }
            else {
                //TODO: go to main page
                NimUIKit.startP2PSession(this, "plus_dev");
            }
            return false;
        }).sendEmptyMessageDelayed(0, 3000);
    }
}
